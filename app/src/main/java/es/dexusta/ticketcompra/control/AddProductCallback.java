package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Product;
import es.dexusta.ticketcompra.model.Subcategory;

public interface AddProductCallback {
    public void onAddProduct(Product product);

    public void onCancelAddProduct();

    public Category getSelectedCategory();

    public Subcategory getSeletedSubcategory();
}
