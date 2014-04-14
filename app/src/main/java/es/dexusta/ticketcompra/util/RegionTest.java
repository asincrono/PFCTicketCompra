package es.dexusta.ticketcompra.util;

import java.util.Collection;
import java.util.HashMap;

import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Town;

public class RegionTest {
    final Region                               mRegion;
    private final HashMap<Long, SubregionTest> mSubregionMap = new HashMap<Long, SubregionTest>();

    public RegionTest(Region region) {
        mRegion = region;
    }

    public Region getRegion() {
        return mRegion;
    }
    
    public long getId() {
        return mRegion.getId();
    }

    public void setId(long id) {
        mRegion.setId(id);
    }
    
    public String getName() {
        return mRegion.getName();
    }
    
    public void setName(String name) {
        mRegion.setName(name);
    }

    public void addSubregion(SubregionTest subregion) {
        mSubregionMap.put(subregion.mSubregion.getId(), subregion);
    }

    public SubregionTest getSubregion(long id) {
        return mSubregionMap.get(id);
    }

    public Town getTown(long id) {
        Town town = null;

        for (SubregionTest subregion : mSubregionMap.values()) {
            if ((town = subregion.getTown(id)) != null) {
                return town;
            }
        }

        return town;
    }

    public Collection<SubregionTest> getSubregions() {
        return mSubregionMap.values();
    }
}
