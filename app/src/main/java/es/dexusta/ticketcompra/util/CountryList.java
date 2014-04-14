package es.dexusta.ticketcompra.util;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Subregion;

public class CountryList {
    private String mName;
    private final List<RegionList> mRegions = new ArrayList<RegionList>();

    public void setName(String name) {
        mName = name;
    }
    
    public String getName() {
        return mName;
    }
    
    public int getRegionCount() {
        return mRegions.size();
    }
    
    public RegionList getRegion(Region region) {
        for (RegionList regionList : mRegions) {
            if (regionList.getRegion().equals(region)) return regionList;
        }
        return null;
    }
    
    public SubregionList getSubregion(Subregion subregion) {
        for (RegionList regionList : mRegions) {
            for (SubregionList subregionList : regionList.getSubregionLists()) {
                if (subregionList.getSubregion().equals(subregion)) return subregionList;
            }
        }
        return null;
    }
    
    public int getSubregionCount() {
        int subregionCount = 0;
        for (RegionList regionList : mRegions) {
            subregionCount += regionList.getSubregionCount();
        }
        return subregionCount;
    }
    
    public int getTownCount() {
        int townCount = 0;
        for (RegionList regionList : mRegions) {
            townCount += regionList.getTownCount();
        }
        return townCount;
    }
    
    public void addRegion(RegionList regionList) {
        mRegions.add(regionList);        
    }
    
    public List<Region> getRegions() {        
        List<Region> list = new ArrayList<Region>();
        
        for (RegionList regionList : mRegions) {
            list.add(regionList.getRegion());
        }
        
        return list;
    }

    public List<Subregion> getSubregions (Region region) {
        for (RegionList regionList : mRegions) {
            if (regionList.getRegion().equals(region)) return regionList.getSubregions();
        }
        return null;
    }
}
