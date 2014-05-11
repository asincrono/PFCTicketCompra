package es.dexusta.ticketcompra.tests;

import es.dexusta.ticketcompra.model.Subcategory;

/**
 * Created by asincrono on 06/05/14.
 */
public class SubcategoryStructure {
    private long             id;
    private ProductStructure name;

    public SubcategoryStructure(Subcategory subcategory) {
        id = subcategory.getId();
        name = subcategory.getName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductStructure getName() {
        return name;
    }

    public void setName(ProductStructure name) {
        this.name = name;
    }

    @Override
    public ProductStructure toString() {
        return "SubcategoryStructure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
