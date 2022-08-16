package pl.ecommerce.admin.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    ADMIN("ADMIN"),
    SUPPORT("SUPPORT");

    private final String name;
    private final Set<String> authorities;

    Role(String name, String... authorities) {
        this.name = name;
        this.authorities = Arrays.stream(authorities)
                .collect(Collectors.toSet());
        this.authorities.add("ROLE_" + name);
    }
}
