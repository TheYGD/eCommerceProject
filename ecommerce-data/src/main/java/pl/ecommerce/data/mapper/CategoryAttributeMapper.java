package pl.ecommerce.data.mapper;

import pl.ecommerce.data.domain.CategoryAttribute;
import pl.ecommerce.data.dto.CategoryAttributeDto;
import pl.ecommerce.data.dto.PseudoEnumValueDto;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryAttributeMapper {

    public static CategoryAttributeMapper INSTANCE = new CategoryAttributeMapper();

    public CategoryAttributeDto toDto(CategoryAttribute categoryAttribute) {
        List<PseudoEnumValueDto> enumValueDtoList = new LinkedList<>();

        if (!categoryAttribute.isNumber()) {
            enumValueDtoList.addAll(
                    categoryAttribute.getPseudoEnum().getValues().stream()
                    .map( value -> {
                        PseudoEnumValueDto dto = new PseudoEnumValueDto( value.getNumber(), value.getName() );
                        return dto;
                    })
                    .collect(Collectors.toList())
            );
        }

        CategoryAttributeDto categoryAttributeDto = new CategoryAttributeDto(
                categoryAttribute.getId(),
                categoryAttribute.getCategory().getName(),
                categoryAttribute.getName(),
                enumValueDtoList);

        return categoryAttributeDto;
    }
}
