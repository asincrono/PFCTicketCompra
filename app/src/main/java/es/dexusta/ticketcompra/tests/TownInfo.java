package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Town;

/**
 * Created by asincrono on 09/05/14.
 */
public class TownInfo {
    private HashMap<String, Town> mStructure = new HashMap<String, Town>();

    public void add(Town[] towns) {
        for (Town town : towns) {
            mStructure.put(town.getName(), town);
        }
    }

    public void add(List<Town> townList) {
        add(townList.toArray(new Town[townList.size()]));
    }

    public long getId(String name) {
        Town town = mStructure.get(name);
        return (town == null) ? -1 : town.getId();
    }

    public Town getTown(String name) {
        return mStructure.get(name);
    }

    public List<Town> getTowns() {
        return new ArrayList<Town>(mStructure.values());
    }
}
