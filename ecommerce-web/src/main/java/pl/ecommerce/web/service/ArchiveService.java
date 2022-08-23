package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.EternalProduct;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.EternalProductRepository;

@Service
@AllArgsConstructor
public class ArchiveService {

    private final EternalProductRepository eternalProductRepository;


    public EternalProduct getEternalProduct(Long id) {
        return eternalProductRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("Eternal product not found!"));
    }

}
