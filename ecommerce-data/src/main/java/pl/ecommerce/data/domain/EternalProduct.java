package pl.ecommerce.data.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "eternal_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EternalProduct extends BaseEntity {

    private String name;
    private String description;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private int quantity;
    private BigDecimal price;

    @Column(name = "image_id")
    private String image;


    public EternalProduct(Product product) {
        name = product.getName();
        description = product.getDescription();
        this.product = product;
        category = product.getCategory();
        seller = product.getSeller();
        quantity = product.getQuantity();
        price = product.getPrice();
        image = product.getImage();
    }



    public String getStructuredDescription() {
        return "<p>" + description.replaceAll("\n", "</p> <br> <p>") + "</p>";
    }
}
