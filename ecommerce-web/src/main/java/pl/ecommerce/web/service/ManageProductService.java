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
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.EternalProductRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.ProductWithQuantityRepository;

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

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final EternalProductRepository eternalProductRepository;
    private final ProductWithQuantityRepository productWithQuantityRepository;


    public List<Category> getCategoryList() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Long createProduct(UserCredentials userCredentials, ProductDto productDto) {

        User user = userCredentials.getUserAccount();
        Category category = categoryRepository.findByName(productDto.getCategory())
                .orElseThrow( () -> new ItemNotFoundException("This category does not exist!"));

        String imgName;
        if (productDto.getImage().getSize() == 0)  {
            imgName = null;
        }
        else {
            imgName = generateImgName();
            saveImage(imgName, productDto.getImage());
        }

        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .category(category)
                .seller(user)
                .quantity(productDto.getQuantity())
                .price( new BigDecimal(productDto.getPrice().replaceAll(",", ".")) )
                .image(imgName).build();

        product = productRepository.save(product);

        return product.getId();
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
        } while ( productRepository.existsByImage(id) );

        return id;
    }

    public Product getProduct(UserCredentials userCredentials, Long id) {
        Product product = productService.getProduct(id);

        if (!product.getSeller().equals(userCredentials.getUserAccount())) {
            throw new ForbiddenException("This product is not yours!");
        }

        return product;
    }

    public void editProduct(UserCredentials userCredentials, ProductDto productDto, Long id) {

        Product product = getProduct(userCredentials, id);

        Category category = categoryRepository.findByName(productDto.getCategory())
                .orElseThrow( () -> new ItemNotFoundException("This category does not exist!"));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setCategory( category );
        product.setQuantity(productDto.getQuantity());
        product.setPrice( new BigDecimal(productDto.getPrice().replaceAll(",", ".")) );


        if (productDto.getImage().getSize() != 0) {
            deletePreviousImage(product.getImage());
            String imgName = generateImgName();
            saveImage(imgName, productDto.getImage());
            product.setImage(imgName);
        }
    }

    private void deletePreviousImage(String image) {

        try {
            Path imagePath = Paths.get(IMAGES_FOLDER_PATH + image);
            Files.delete(imagePath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void deleteProduct(UserCredentials userCredentials, Long id) {
        Product product = getProduct(userCredentials, id);
        EternalProduct eternalProduct = eternalProductRepository.findByProduct(product)
                .orElseThrow( () -> new ItemNotFoundException("Eternal product not found!"));
        productWithQuantityRepository.deleteAllByProduct(product);

        eternalProduct.setProduct(null);
        productRepository.delete(product);
    }
}
