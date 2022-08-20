package pl.ecommerce.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity(name="carts")
@Getter
@Setter
@Table(name ="carts")
public class Cart extends BaseEntity {

    @OneToOne(mappedBy = "cart")
    private User owner;

    @OneToMany(mappedBy = "cart")
    private List<ProductInCart> productList = new LinkedList<>();
}
