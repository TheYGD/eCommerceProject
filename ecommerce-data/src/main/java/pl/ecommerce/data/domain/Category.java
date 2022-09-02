package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "categories")
@Table(name = "categories")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    private long orderId;
    private String name;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "product_attributes_for_categories",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id")
    )
    private List<ProductAttribute> productAttributes;

    /*************************** Decoration ***************************/
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new LinkedList<>();
    /******************************************************************/


    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Long getId() {
        return orderId;
    }
}
