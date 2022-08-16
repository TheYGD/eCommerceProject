package pl.ecommerce.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.ecommerce.domain.dto.UserRegisterDto;
import pl.ecommerce.domain.entity.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );


    @Mapping(source = "email", target = "credentials.email")
    @Mapping(source = "username", target = "credentials.username")
    @Mapping(source = "password", target = "credentials.password")
    User userRegisterDtoToUser(UserRegisterDto userRegisterDto);
}
