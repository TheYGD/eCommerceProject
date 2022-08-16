package pl.ecommerce.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "category")
    private List<Product> products = new LinkedList<>();


    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
