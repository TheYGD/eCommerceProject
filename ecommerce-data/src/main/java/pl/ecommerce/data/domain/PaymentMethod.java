package pl.ecommerce.data.domain;


public enum PaymentMethod {
    BANK_TRANSFER, CREDIT_CARD;

    public String formattedName() {
        String lowercaseWithSpaces = name().replaceAll("_", " ").toLowerCase();
        return lowercaseWithSpaces.substring(0, 1).toUpperCase() + lowercaseWithSpaces.substring(1);
    }
}
