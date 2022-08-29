package pl.ecommerce.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "carts")

@Getter
@Setter
public class Cart extends BaseEntity {

    @OneToOne(mappedBy = "cart")
    private User owner;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<ProductInCart> productList = new LinkedList<>();
}
