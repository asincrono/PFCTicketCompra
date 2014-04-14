package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Chain;

public interface ChainSelectionCallback {
    public void onChainSelected(Chain chain);    
    public void onCancelChainSelection();
    public ChainAdapter getChainAdapter();
}


