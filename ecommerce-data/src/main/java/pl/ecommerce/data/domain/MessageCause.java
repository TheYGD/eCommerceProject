package pl.ecommerce.data.domain;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Getter
public enum MessageCause {

    ORDER_DID_NOT_ARRIVE(1, "Order did not arrived"),
    ORDER_INCOMPLETE(2,"Order arrived incomplete"),
    ORDER_DAMAGED(3,"Order arrived damaged"),
    ORDER_OTHER_QUESTION(4,"Other question about order"),

    PRODUCT_DELIVERY_METHODS(5,"Available delivery methods"),
    PRODUCT_DELIVERY_TIME(6,"Estimated delivery time"),
    PRODUCT_OTHER_QUESTION(7,"Other question about product");


    private final int id;
    private final String cause;

    MessageCause(int id, String cause) {
        this.id = id;
        this.cause = cause;
    }


    public static List<MessageCause> getCausesForProduct() {
        return Arrays.stream(MessageCause.values())
                .filter( cause -> cause.name().startsWith("PRODUCT_") )
                .collect(Collectors.toList());
    }

    public static List<MessageCause> getCausesForOrder() {
        return Arrays.stream(MessageCause.values())
                .filter( cause -> cause.name().startsWith("ORDER_") )
                .collect(Collectors.toList());
    }

    public static Optional<MessageCause> findById(int id) {
        return Arrays.stream(values())
                .filter( cause -> cause.id == id )
                .findFirst();
    }

    public boolean isProductCause() {
        return name().startsWith("PRODUCT_");
    }

    public boolean isOrderCause() {
        return name().startsWith("ORDER_");
    }


    public int getId() {
        return id;
    }

    public String getCause() {
        return cause;
    }
}
