package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Chain;

public interface ChainSelectionCallback extends FragmentABCallback {
    public void onChainSelected(Chain chain);    
    public void onCancelChainSelection();
    public ChainAdapter getChainAdapter();
}


