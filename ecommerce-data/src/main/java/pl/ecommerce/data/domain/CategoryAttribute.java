package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "category_attributes")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttribute extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String name;
    private boolean number;

    @OneToOne
    private PseudoEnum pseudoEnum;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryAttribute that = (CategoryAttribute) o;
        return getId().equals( that.getId() );
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
