package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Chain;
import es.dexusta.ticketcompra.model.Shop;

public interface AddShopCallbacks extends FragmentABCallback {

    public void onAcceptAddShop(Shop shop);
    public Chain getChain();
    public void onCancelAddShop();

}
