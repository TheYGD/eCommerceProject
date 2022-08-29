package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "products")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    private String name;
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private AvailableProduct availableProduct;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
    private BigDecimal price;

    @Column(name = "image_id")
    private String image;


    public Product(String name, String description, Category category, User seller, BigDecimal price, String image) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.seller = seller;
        this.price = price;
        this.image = image;
    }

    public String getStructuredDescription() {
        return "<p>" + description.replaceAll("\n", "</p> <br> <p>") + "</p>";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
