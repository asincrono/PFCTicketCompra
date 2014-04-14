package es.dexusta.ticketcompra.util;

import java.util.Collection;
import java.util.HashMap;

import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;

public class SubregionTest {
    public final Subregion            mSubregion;
    private final HashMap<Long, Town> mTownsMap = new HashMap<Long, Town>();

    public SubregionTest(Subregion subregion) {
        mSubregion = subregion;
    }
    
    public Subregion getSubregion() {
        return mSubregion;
    }

    public long getId() {
        return mSubregion.getId();
    }
    
    public void setId(long id) {
        mSubregion.setId(id);
    }
    
    public String getName() {
        return mSubregion.getName();
    }
    
    public void setName(String name) {
        mSubregion.setName(name);
    }

    public void addTown(Town town) {
        mTownsMap.put(town.getId(), town);
    }

    public Town getTown(long id) {
        return mTownsMap.get(id);
    }
    
    public Collection<Town> getTowns() {
        return mTownsMap.values();
    }
}
