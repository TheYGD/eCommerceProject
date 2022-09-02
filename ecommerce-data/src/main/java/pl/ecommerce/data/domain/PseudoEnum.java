package pl.ecommerce.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;


@Entity
@Table(name = "pseudo_enums")

@Getter
@Setter
public class PseudoEnum extends BaseEntity {

    @OneToMany(mappedBy = "pseudoEnum", cascade = CascadeType.ALL)
    private List<PseudoEnumValue> values = new LinkedList<>();
}
