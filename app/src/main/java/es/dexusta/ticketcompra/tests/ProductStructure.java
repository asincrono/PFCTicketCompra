package es.dexusta.ticketcompra.tests;

import com.google.gson.annotations.SerializedName;

import es.dexusta.ticketcompra.model.Product;

/**
 * Created by asincrono on 06/05/14.
 */
public class ProductStructure {
    private long             id;
    private long             subcategoryId;
    private java.lang.String name;
    private java.lang.String description;

    @SerializedName("subcategoryName")
    private java.lang.String subcategoryName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getDescription() {
        return description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        subcategoryName = subcategoryName;
    }

    public Product getProduct() {
        Product product = new Product();

        product.setName(name);
        product.setDescription(description);
        product.setSubcategoryId(subcategoryId);
        return product;
    }

    @Override
    public java.lang.String toString() {
        return "ProductStructure{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subcategory_name='" + subcategoryName + '\'' +
                '}';
    }
}
