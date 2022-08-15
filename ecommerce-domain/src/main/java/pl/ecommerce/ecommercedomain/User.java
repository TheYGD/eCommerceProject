package pl.ecommerce.ecommercedomain;

import lombok.Getter;
import lombok.Setter;
import pl.ecommerce.ecommercedomain.entity.BaseEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class User extends BaseEntity {
}
