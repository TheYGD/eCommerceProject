package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.SoldProductsGroup;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.repository.SoldProductsGroupRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfileService {

    private final SoldProductsGroupRepository soldProductsGroupRepository;


    public List<SoldProductsGroup> getSoldProductsGroupList(UserCredentials userCredentials) {
        return soldProductsGroupRepository.findAllBySeller(userCredentials.getUserAccount());
    }
}
