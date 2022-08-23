//package pl.ecommerce.data.mapper;
//
//import org.springframework.stereotype.Component;
//import pl.ecommerce.data.dto.ProductDto;
//import pl.ecommerce.data.domain.Product;
//
//import java.math.BigDecimal;
//
//@Component
//public class ProductMapper {
//
//    public static ProductMapper INSTANCE = new ProductMapper();
//
//
//    public Product dtoToEntity(ProductDto productDto) {
//        Product product = new Product();
//
//        product.setName(productDto.getName());
//        product.setDescription(productDto.getDescription());
//        product.setQuantity(productDto.getQuantity());
//        product.setPrice( new BigDecimal(productDto.getPrice().replaceAll(",", ".")) );
//
//        return product;
//    }
//}
