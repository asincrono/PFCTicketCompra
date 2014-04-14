package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Shop;

public interface ShopSelectionCallback {
    
    public void onShopSelected(Shop shop);
    public void onCancelShopSelection();
    public void onClickAddShop();
    public ShopAdapter getShopAdapter();

}
