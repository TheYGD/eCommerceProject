package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.mapper.ProductMapper;
import pl.ecommerce.data.other.HashGenerator;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CategoryRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.UserRepository;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@AllArgsConstructor
public class AddProductService {

    private final String IMAGES_FOLDER_PATH = "images/products/";

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public List<Category> getCategoryList() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Long createProduct(ProductDto productDto, UserCredentials userCredentials) {

        User user = userCredentials.getUserAccount();
        Product product = ProductMapper.INSTANCE.dtoToEntity(productDto);
        Category category = categoryRepository.findByName(productDto.getCategory())
                .orElseThrow( () -> new ItemNotFoundException("This category does not exist!"));
        String imgName = generateImgName();

        product.setSeller(user);
        product.setImageId(imgName);
        product.setCategory(category);

        saveImage(imgName, productDto.getImage());

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
        } while ( productRepository.existsByImageId(id) );

        return id;
    }
}
