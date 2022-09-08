package pl.ecommerce.specification.params;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductAttributeParamEnum extends ProductAttributeParam {

    private BigDecimal value;


    public ProductAttributeParamEnum(Long categoryId, BigDecimal value) {
        super(categoryId);
        this.value = value;
    }
}
