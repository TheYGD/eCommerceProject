package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "categories")
@Table(name = "categories")

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    private long orderId;
    private String name;
    private String description;

    @OneToMany(mappedBy = "category")
    private List<CategoryAttribute> categoryAttributes;

    /*************************** Decoration ***************************/
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new LinkedList<>();
    /******************************************************************/


    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }


    public List<CategoryAttribute> getAllCategoryAttributes() {
        List<CategoryAttribute> attributeList = new LinkedList<>();

        Category category = this;
        while (category != null) {
            for (int i = category.categoryAttributes.size() - 1; i >= 0; i--) {
                attributeList.add( category.categoryAttributes.get(i) );
            }
            category = category.getParent();
        }

        Collections.reverse(attributeList);

        return attributeList;
    }
}
