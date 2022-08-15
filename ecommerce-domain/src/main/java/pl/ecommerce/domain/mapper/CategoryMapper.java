package pl.ecommerce.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.ecommerce.domain.dto.CategoryDto;
import pl.ecommerce.domain.entity.Category;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    Category DtoToEntity(CategoryDto categoryDto);
    CategoryDto EntityToDto(Category category);
}
