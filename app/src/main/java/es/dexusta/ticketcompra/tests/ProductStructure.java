package es.dexusta.ticketcompra.tests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asincrono on 06/05/14.
 */
public class ProductStructure {
    private long id;
    private String name;
    private String description;

    @SerializedName("subcategoryName")
    private String subcategory_name;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubcategoryName() {
        return subcategory_name;
    }

    public void setSubcategoryName(String subcategoryName) {
        subcategory_name = subcategoryName;
    }

    @Override
    public String toString() {
        return "ProductStructure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subcategory_name='" + subcategory_name + '\'' +
                '}';
    }
}
