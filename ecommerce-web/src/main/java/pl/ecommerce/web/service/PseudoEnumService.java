package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.PseudoEnum;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class PseudoEnumService {

    public boolean valueExists(PseudoEnum pseudoEnum, BigDecimal value) {
        return pseudoEnum.getValues().stream()
                .anyMatch( enumValue -> enumValue.getNumber() == value.longValue() );
    }
}
