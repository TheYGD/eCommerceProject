package pl.ecommerce.specification.params;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductAttributeParamNumber extends ProductAttributeParam {

    private BigDecimal minValue;
    private BigDecimal maxValue;


    public ProductAttributeParamNumber(Long categoryId, BigDecimal minValue, BigDecimal maxValue) {
        super(categoryId);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
