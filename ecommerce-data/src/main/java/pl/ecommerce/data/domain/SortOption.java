package pl.ecommerce.data.domain;

import lombok.Getter;
import pl.ecommerce.exceptions.ItemNotFoundException;

import java.util.Arrays;

@Getter
public enum SortOption {

    DEFAULT(0), PRICE_LOWEST_FIRST(1), PRICE_HIGHEST_FIRST(2);


    public int id;

    SortOption(int id) {
        this.id = id;
    }

    public String getName() {
        String line = "";
        String[] words = name().toLowerCase().split("_");
        for (String word : words) {
            line += word + " ";
        }

        return line.substring(0, 1).toUpperCase() + line.substring(1, line.length() - 1);
    }


    public static SortOption getById(int id) {
        return Arrays.stream(values())
                .filter( option -> option.id == id )
                .findFirst()
                .orElse( DEFAULT );
    }
}
