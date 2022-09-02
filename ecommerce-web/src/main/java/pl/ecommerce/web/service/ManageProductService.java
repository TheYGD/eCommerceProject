package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.data.other.HashGenerator;
import pl.ecommerce.exceptions.ForbiddenException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.*;

import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ManageProductService {

    private final String IMAGES_FOLDER_PATH = "images/products/";

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final AvailableProductRepository availableProductRepository;
    private final ProductRepository productRepository;
    private final ProductInCartRepository productInCartRepository;
    private final CartRepository cartRepository;


    public List<Category> getCategoryList() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Long createProduct(UserCredentials userCredentials, ProductDto productDto) {

        User user = userCredentials.getUserAccount();
        Category category = categoryRepository.findByOrderId( productDto.getCategory() )
                .orElseThrow( () -> new ItemNotFoundException("This category does not exist!"));

        String imgName;
        if (productDto.getImage().getSize() == 0)  {
            imgName = null;
        }
        else {
            imgName = generateImgName();
            saveImage(imgName, productDto.getImage());
        }

        Product product = productRepository.save(
                Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .category(category)
                .seller(user)
                .price( new BigDecimal(productDto.getPrice().replaceAll(",", ".")) )
                .image(imgName).build());

        AvailableProduct availableProduct = new AvailableProduct(product, productDto.getQuantity(), 0,
                false);
        availableProduct.setId(product.getId());
        product.setAvailableProduct(availableProduct);

        productRepository.save(product);

        return availableProduct.getId();
    }

    private void saveImage(String imgName, MultipartFile image) {
        Path uploadPath = Paths.get(IMAGES_FOLDER_PATH);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while saving image!");
        }

        try (InputStream inputStream = image.getInputStream()) {
            Path filePath = uploadPath.resolve(imgName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new RuntimeException("Error while saving image!");
        }

    }

    private String generateImgName() {

        String id;
        do {
            id = HashGenerator.generate(32);
        } while ( availableProductRepository.findByImage(id).isPresent() );

        return id;
    }

    public AvailableProduct getProduct(UserCredentials userCredentials, Long id) {
        AvailableProduct availableProduct = productService.getProduct(id);

        if (!availableProduct.getProduct().getSeller().equals(userCredentials.getUserAccount())) {
            throw new ForbiddenException("This product is not yours!");
        }

        return availableProduct;
    }


    @Transactional
    public void editProduct(UserCredentials userCredentials, ProductDto productDto, Long id) {

        AvailableProduct availableProduct = getProduct(userCredentials, id);
        Product product = availableProduct.getProduct();

        Category category = categoryRepository.findByOrderId( Long.valueOf(productDto.getCategory()) )
                .orElseThrow( () -> new ItemNotFoundException("This category does not exist!"));

        availableProduct.setQuantity(productDto.getQuantity());

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCategory( category );
        product.setPrice( new BigDecimal(productDto.getPrice().replaceAll(",", ".")) );


        if (productDto.getImage().getSize() != 0) {
            deleteImage(product.getImage());
            String imgName = generateImgName();
            saveImage(imgName, productDto.getImage());
            product.setImage(imgName);
        }

        availableProductRepository.save(availableProduct);
        productRepository.save(product);
    }

    private void deleteImage(String image) {

        try {
            Path imagePath = Paths.get(IMAGES_FOLDER_PATH + image);
            Files.delete(imagePath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void deleteProduct(UserCredentials userCredentials, Long id) {
        AvailableProduct availableProduct = getProduct(userCredentials, id);
        Product product = availableProduct.getProduct();

        product.setAvailableProduct(null);
        productInCartRepository.deleteAllByProduct(availableProduct);
        markAsJustDeletedProducts(availableProduct);

        availableProductRepository.delete(availableProduct);
        productRepository.save(product);
    }

    public void markAsJustDeletedProducts(AvailableProduct availableProduct) {
        List<Cart> carts = productInCartRepository.findAllByProduct(availableProduct).stream()
                .map( productInCart -> productInCart.getCart() )
                .map( cart -> {
                    cart.setJustDeletedProducts(true);
                    return cart;
                } )
                .toList();

        cartRepository.saveAll(carts);
    }

    public ProductDto getProductDto(UserCredentials userCredentials, AvailableProduct availableProduct) {

        return ProductDto.builder()
                .name(availableProduct.getProduct().getName())
                .description(availableProduct.getProduct().getDescription())
                .category(availableProduct.getProduct().getCategory().getId())
                .quantity(availableProduct.getQuantity())
                .price(String.valueOf(availableProduct.getProduct().getPrice()))
                .build();

    }
}
