package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "pseudo_enum_values")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PseudoEnumValue extends BaseEntity {

    private long number;

    @ManyToOne
    @JoinColumn(name = "pseudo_enum_id")
    private PseudoEnum pseudoEnum;

    private String value;
}
