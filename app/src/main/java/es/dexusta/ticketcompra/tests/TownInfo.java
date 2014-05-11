package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Town;

/**
 * Created by asincrono on 09/05/14.
 */
public class TownInfo {
    private HashMap<String, Town> mData = new HashMap<String, Town>();

    public TownInfo(List<Town> towns) {
        add(towns);
    }

    public void add(Town[] towns) {
        for (Town town : towns) {
            mData.put(town.getName(), town);
        }
    }

    public void add(List<Town> towns) {
        for (Town town : towns) {
            mData.put(town.getName(), town);
        }
    }

    public long getId(String name) {
        Town town = mData.get(name);
        return (town == null) ? -1 : town.getId();
    }

    public Town getTown(String name) {
        return mData.get(name);
    }

    public List<Town> getTowns() {
        return new ArrayList<Town>(mData.values());
    }
}
