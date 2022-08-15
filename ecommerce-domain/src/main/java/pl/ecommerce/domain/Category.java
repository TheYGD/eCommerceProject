package pl.ecommerce.ecommercedomain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.ecommerce.ecommercedomain.entity.BaseEntity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

    private String name;
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private List<Product> products = new LinkedList<>();


    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
