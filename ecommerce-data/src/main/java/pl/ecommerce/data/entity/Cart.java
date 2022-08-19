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

    @ManyToMany
    @JoinTable(
            name="products_in_carts",
            joinColumns = @JoinColumn(name="cart_id"),
            inverseJoinColumns = @JoinColumn(name="product_id"))
    private List<ProductInCart> productList = new LinkedList<>();
}
