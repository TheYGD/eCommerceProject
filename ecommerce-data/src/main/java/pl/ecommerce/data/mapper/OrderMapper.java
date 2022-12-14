package pl.ecommerce.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.ecommerce.data.domain.Address;
import pl.ecommerce.data.domain.Order;
import pl.ecommerce.data.dto.OrderDto;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper( OrderMapper.class );


    @Mapping(source = "addressLine1", target="address.addressLine1")
    @Mapping(source = "addressLine2", target="address.addressLine2")
    @Mapping(source = "city", target="address.city")
    @Mapping(source = "state", target="address.state")
    @Mapping(source = "postalCode", target="address.postalCode")
    @Mapping(source = "country", target="address.country")
    Order DtoToEntity(OrderDto orderDto);

    Address DtoToEntityAddress(OrderDto orderDto);
}
