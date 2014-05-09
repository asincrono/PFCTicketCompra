package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Chain;

/**
 * Created by asincrono on 09/05/14.
 */
public class ChainInfo {
    private HashMap<String, Chain> mStructure = new HashMap<String, Chain>();

    public void add(Chain[] chains) {
        for (Chain chain : chains) {
            mStructure.put(chain.getName(), chain);
        }
    }

    public void add(List<Chain> chains) {
        add(chains.toArray(new Chain[chains.size()]));
    }

    public long getId(String name) {
        Chain chain = mStructure.get(name);
        return (chain == null) ? -1 : chain.getId();
    }

    public Chain getChain(String name) {
        return mStructure.get(name);
    }

    public List<Chain> getChains() {
        return new ArrayList<Chain>(mStructure.values());
    }
}
