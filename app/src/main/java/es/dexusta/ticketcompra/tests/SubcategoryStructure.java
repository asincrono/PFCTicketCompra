package es.dexusta.ticketcompra.tests;

import es.dexusta.ticketcompra.model.Subcategory;

/**
 * Created by asincrono on 06/05/14.
 */
public class SubcategoryStructure {
    private long             id;
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubcategoryStructure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
