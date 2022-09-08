package pl.ecommerce.specification;

import org.springframework.data.jpa.domain.Specification;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.specification.params.ProductAttributeParam;
import pl.ecommerce.specification.params.ProductAttributeParamEnum;
import pl.ecommerce.specification.params.ProductAttributeParamNumber;

import javax.persistence.criteria.*;
import java.util.List;


public class AvailableProductSpecification {


    public static AvailableProductSpecificationBuilder builder() {
        return new AvailableProductSpecificationBuilder();
    }

    private static Specification<AvailableProduct> byNumberAttribute(ProductAttributeParamNumber param) {
        return (root, query, builder) -> {

            ListJoin<AvailableProduct, ProductAttribute> productAttributeJoin = root.join("product")
                    .joinList("attributes");

            Join<CategoryAttribute, CategoryAttribute> categoryAttributeJoin = productAttributeJoin
                    .join("categoryAttribute");

            if (param.getMinValue() == null) {
                if (param.getMaxValue() == null) {
                    throw new InvalidArgumentException("Error! Try again later.");
                }

                return builder.and( builder.equal( categoryAttributeJoin.get("id"), param.getCategoryId() ),
                        builder.lessThanOrEqualTo( productAttributeJoin.get("value"), param.getMaxValue() ) );
            }

            if (param.getMaxValue() == null) {
                return builder.and( builder.equal( categoryAttributeJoin.get("id"), param.getCategoryId() ),
                        builder.greaterThanOrEqualTo( productAttributeJoin.get("value"), param.getMinValue() ) );
            }

            return builder.and( builder.equal( categoryAttributeJoin.get("id"), param.getCategoryId() ),
                    builder.between( productAttributeJoin.get("value"), param.getMinValue(), param.getMaxValue() ) );
        };
    }

    private static Specification<AvailableProduct> byEnumAttribute(ProductAttributeParamEnum param) {
        return (root, query, builder) -> {

            ListJoin<AvailableProduct, ProductAttribute> productAttributeJoin = root.join("product")
                    .joinList("attributes");

            Join<CategoryAttribute, CategoryAttribute> categoryAttributeJoin = productAttributeJoin
                    .join("categoryAttribute");

            return builder.and( builder.equal( categoryAttributeJoin.get("id"), param.getCategoryId() ),
                    builder.equal( productAttributeJoin.get("value"), param.getValue() ) );
        };
    }

    private static Specification<AvailableProduct> byCategory(long categoryStartId, long categoryEndId) {
        return (root, query, builder) -> {

            Join<AvailableProduct, Category> categoryJoin = root.join("product").join("category");

            return builder.between( categoryJoin.get("orderId"), categoryStartId, categoryEndId );
        };
    }

    private static Specification<AvailableProduct> byQuery(String searchQuery) {
        return (root, query, builder) -> {

            Join<AvailableProduct, Product> productJoin = root.join("product");

            return builder.or( builder.like( builder.lower(productJoin.get("name")), "%" + searchQuery.toLowerCase() + "%" ),
                    builder.like( builder.lower(productJoin.get("description")), "%" + searchQuery.toLowerCase() + "%" ) );
        };
    }

    private static Specification<AvailableProduct> bySeller(long sellerId) {
        return (root, query, builder) -> {

            Join<AvailableProduct, User> sellerJoin = root.join("product").join("seller");

            return builder.equal( sellerJoin.get("id"), sellerId );
        };
    }



    public static class AvailableProductSpecificationBuilder {

        private Specification<AvailableProduct> specification = Specification.where(null);


        public AvailableProductSpecificationBuilder categoryIdBetween(long categoryStartId, long categoryEndId) {
            specification = specification.and( byCategory(categoryStartId, categoryEndId) );
            return this;
        }

        public AvailableProductSpecificationBuilder attributes(List<ProductAttributeParam> attributes) {

            for (ProductAttributeParam attribute : attributes) {
                if (attribute.getClass() == ProductAttributeParamNumber.class) { // number
                    specification = specification.and( byNumberAttribute( (ProductAttributeParamNumber) attribute) );
                }
                else { // enum
                    specification = specification.and( byEnumAttribute( (ProductAttributeParamEnum) attribute) );
                }
            }

            return this;
        }

        public AvailableProductSpecificationBuilder query(String query) {
            if (!query.isBlank()) {
                specification = specification.and( byQuery(query) );
            }

            return this;
        }

        public AvailableProductSpecificationBuilder sellerId(Long sellerId) {
            if (sellerId != null) {
                specification = specification.and(bySeller(sellerId));
            }
            return this;
        }

        public Specification<AvailableProduct> build() {
            return specification;
        }
    }

}
