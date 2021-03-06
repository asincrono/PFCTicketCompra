package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Product;

public interface ProductSelectionCallback {
    public void onProductSelected(Product product, int position);
    public void onCancelProductSelection();
}
