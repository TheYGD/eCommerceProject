package pl.ecommerce.data.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "categories")

@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

    private String name;
    private String description;



    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
