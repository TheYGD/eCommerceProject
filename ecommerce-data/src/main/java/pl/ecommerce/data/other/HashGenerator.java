package pl.ecommerce.data.other;


import java.util.Random;

public class HashGenerator {

    private static Random random = new Random();

    public static String generate(int length) {

        StringBuilder idBuilder;

        idBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int charOrd = random.nextInt(36);
            if (charOrd < 26) { // this way we get a-z or 0-9
                charOrd += 97;
            }
            else {
                charOrd += 22;
            }

            idBuilder.append((char) charOrd);
        }

        return idBuilder.toString();
    }
}
