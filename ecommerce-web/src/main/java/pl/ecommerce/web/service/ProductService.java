package pl.ecommerce.web.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.other.ProductSort;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.AvailableProductRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.specification.AvailableProductSpecification;
import pl.ecommerce.specification.params.ProductAttributeParam;
import pl.ecommerce.specification.params.ProductAttributeParamEnum;
import pl.ecommerce.specification.params.ProductAttributeParamNumber;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProductService {

    @Value("${pl.ecommerce.products-on-page}")
    private int RECORDS_ON_PAGE;
    private final CartService cartService;
    private final CategoryService categoryService;
    private final MessageService messageService;
    private final AttributeService attributeService;
    private final AvailableProductRepository availableProductRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSort productSort;

    public ProductService(CartService cartService, CategoryService categoryService, MessageService messageService,
                          AttributeService attributeService, AvailableProductRepository availableProductRepository,
                          ProductRepository productRepository, CategoryRepository categoryRepository,
                          ProductSort productSort) {
        this.cartService = cartService;
        this.categoryService = categoryService;
        this.messageService = messageService;
        this.attributeService = attributeService;
        this.availableProductRepository = availableProductRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productSort = productSort;
    }



    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No item found with id=" + id) );
    }


    public Page<AvailableProduct> findProducts(Category category, String query, int pageNr, int sortOption,
                                               Map<String, String> otherValues) {

        long categoryOrderStart = category.getOrderId();
        long categoryOrderEnd = categoryService.getCategoryOrderEnd(category);
        List<ProductAttributeParam> attributes = parseMapToAttributes(otherValues);

        Specification<AvailableProduct> specification = AvailableProductSpecification.builder()
                .categoryIdBetween( categoryOrderStart, categoryOrderEnd )
                .query(query)
                .attributes(attributes)
                .sellerId(null)
                .build();

        Pageable pageable = PageRequest.of(pageNr - 1, RECORDS_ON_PAGE, productSort.getSort(sortOption));
        return availableProductRepository.findAll(specification, pageable);
    }

    private List<ProductAttributeParam> parseMapToAttributes(Map<String, String> otherValues) {
        List<ProductAttributeParam> attributes = new LinkedList<>();

        otherValues.forEach((key, value) -> {
            long attributeId;

            // is key a number - it is an attribute
            try {
                attributeId = Long.parseLong(key);
            }
            catch (Exception e) {
                return;
            }

            ProductAttributeParam attribute;

            CategoryAttribute categoryAttribute = attributeService.getCategoryAttribute(attributeId);
            if (categoryAttribute.isNumber()) {
                String[] values = value.split("x");
                BigDecimal min;
                BigDecimal max;

                try {
                    min = new BigDecimal(values[0]);
                } catch (Exception e) {
                    min = null;
                }

                try {
                    max = new BigDecimal(values[1]);
                } catch (Exception e) {
                    max = null;
                }

                attribute = new ProductAttributeParamNumber(attributeId, min, max);
            } else {
                attribute = new ProductAttributeParamEnum(attributeId, new BigDecimal(value));
            }

            attributes.add(attribute);
        });

        return attributes;
    }


    /**
     * @return message if added successfully, product already in cart or error
     */
    public void addProductToCart(UserCredentials userCredentials, Long productId, Integer quantity,
                                           HttpServletRequest request, HttpServletResponse response) {
        cartService.addProductToCart(userCredentials, productId, quantity, request, response);
    }


    public AvailableProduct getProduct(Long id) {
        return availableProductRepository.findById(id)
                .orElseThrow( () -> {
                    String message = "Could not find product with id: %d!".formatted(id);
                    log.error(message);
                    return new ItemNotFoundException(message);
                });
    }


    public Category getCategory(Long id) {
        return categoryRepository.findByOrderId(id)
                .orElseThrow( () -> new ItemNotFoundException("Category with id=%d doesn't exist!".formatted(id)) );
    }


    public Long askAboutProduct(UserCredentials userCredentials, Long productId, Map<String, String> formData) {

        if (!formData.containsKey("titleId") || !formData.containsKey("content")) {
            throw new InvalidArgumentException("Error! Try again later.");
        }

        int titleId = Integer.parseInt(formData.get("titleId"));
        String content = formData.get("content");

        MessageCause messageCause = MessageCause.findById(titleId)
                .orElseThrow( () -> new ItemNotFoundException("Message cause not found!") );
        if (!messageCause.isProductCause()) {
            log.warn("Invalid message cause id=%d chose for message about order.".formatted(titleId));
            throw new InvalidArgumentException("Error! Try again later");
        }

        AvailableProduct availableProduct = getProduct(productId);

        String link = "/products/" + productId;
        String linkName = availableProduct.getProduct().getName();

        return messageService.createChat(userCredentials.getUserAccount(), availableProduct.getProduct().getSeller(), messageCause, content,
                link, linkName );
    }


    public List<MessageCause> getProductMessageTitles() {
        return MessageCause.getCausesForProduct();
    }

}
