package es.dexusta.ticketcompra.tests;

import java.util.List;

import es.dexusta.ticketcompra.model.Category;

/**
 * Created by asincrono on 06/05/14.
 */
public class CategoryStructure {
    private long id;
    private String                     name;
    private List<SubcategoryStructure> subcategories;

    public CategoryStructure(Category category) {
        id = category.getId();
        name = category.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubcategoryStructure> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<SubcategoryStructure> subcategories) {
        this.subcategories = subcategories;
    }

    @Override
    public String toString() {
        return "CategoryStructure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subcategories=" + subcategories +
                '}';
    }
}
