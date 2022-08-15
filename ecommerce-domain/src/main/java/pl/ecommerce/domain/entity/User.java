package pl.ecommerce.domain.entity;

import lombok.Getter;
import lombok.Setter;
import pl.ecommerce.domain.entity.BaseEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class User extends BaseEntity {
}
