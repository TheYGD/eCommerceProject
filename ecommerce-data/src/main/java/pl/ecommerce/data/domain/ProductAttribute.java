package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product_attributes")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttribute extends BaseEntity {

    private String name;
    private boolean number;

    @OneToOne
    private PseudoEnum pseudoEnum;

    private BigDecimal value;

}
