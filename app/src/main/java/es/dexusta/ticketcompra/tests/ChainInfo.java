package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Chain;

/**
 * Created by asincrono on 09/05/14.
 */
public class ChainInfo {
    private HashMap<String, Chain> mStructure = new HashMap<String, Chain>();

    public ChainInfo(List<Chain> chains) {
        add(chains);
    }

    public ChainInfo(Chain[] chains) {
        add(chains);
    }

    public void add(Chain[] chains) {
        for (Chain chain : chains) {
            mStructure.put(chain.getName(), chain);
        }
    }

    public void add(List<Chain> chains) {
        for (Chain chain : chains) {
            mStructure.put(chain.getName(), chain);
        }
    }

    public long getId(String name) {
        Chain chain = mStructure.get(name);
        return (chain == null) ? -1 : chain.getId();
    }

    public Chain getChain(String name) {
        return mStructure.get(name);
    }

    public HashMap<Long, Chain> getIdChainMap() {
        Collection<Chain> chains = mStructure.values();
        HashMap<Long, Chain> idChainMap = new HashMap<Long, Chain>();
        for (Chain chain :chains) {
            idChainMap.put(chain.getId(), chain);
        }
        return idChainMap;
    }

    public List<Chain> getChains() {
        return new ArrayList<Chain>(mStructure.values());
    }
}
