package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Shop;

public interface ShopSelectionCallback extends FragmentCallback{
    
    public void onShopSelection(Shop shop);
    public void onCancelShopSelection();
    public void onClickAddShop();
    public ShopAdapter getShopAdapter();

}
