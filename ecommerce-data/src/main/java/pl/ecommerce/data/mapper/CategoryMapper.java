package pl.ecommerce.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pl.ecommerce.data.dto.CategoryDto;
import pl.ecommerce.data.entity.Category;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper( CategoryMapper.class );

    Category DtoToEntity(CategoryDto categoryDto);
    //CategoryDto EntityToDto(Category category);
}