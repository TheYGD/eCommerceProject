package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.CategoryAttribute;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.CategoryAttributeRepository;

@Service
@AllArgsConstructor
public class AttributeService {

    private CategoryAttributeRepository categoryAttributeRepository;


    public CategoryAttribute getCategoryAttribute(long id) {
        return categoryAttributeRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("Error! Try again later."));
    }
}
