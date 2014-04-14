package es.dexusta.ticketcompra.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import es.dexusta.ticketcompra.model.Town;

public class Country {
    private String name;
    private final HashMap<Long, RegionTest> mRegionMap = new HashMap<Long, RegionTest>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addRegion(RegionTest region) {
        mRegionMap.put(region.mRegion.getId(), region);
    }

    public RegionTest getRegion(long id) {
        return mRegionMap.get(id);
    }

    public SubregionTest getSubregion(long subRegionId) {
        SubregionTest subregion;
        for (RegionTest region : mRegionMap.values()) {
            subregion = region.getSubregion(subRegionId); 
            if ((subregion = region.getSubregion(subRegionId)) != null) {
                return subregion;
            }
        }
        return null;
    }

    public Collection<RegionTest> getRegions() {
        return mRegionMap.values();
    }

    public Collection<SubregionTest> getSubregions(long regionId) {
        return mRegionMap.get(regionId).getSubregions();
    }

    public Collection<Town> getTowns(RegionTest region) {
        Collection<Town> result = null;

        for (SubregionTest subregion : region.getSubregions()) {
            if (result == null) {
                result = new ArrayList<Town>(subregion.getTowns());
            }
        }

        return result;
    }

}
