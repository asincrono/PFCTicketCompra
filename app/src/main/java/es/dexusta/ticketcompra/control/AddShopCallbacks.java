package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

public interface AddShopCallbacks {

    public void onAcceptAddShop(Shop shop);

    public void onCancelAddShop();

    public Chain getSelectedChain();
}
