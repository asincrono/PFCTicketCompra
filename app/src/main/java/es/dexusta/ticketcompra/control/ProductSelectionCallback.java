package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Product;

public interface ProductSelectionCallback extends FragmentCallback{
    public void onProductSelected(Product product, int position);

    public ProductAdapter getProductAdapter();

    public int getSelectedProductPosition();

    public void onClickAddProduct();

    public void onCancelProductSelection();
}
