package es.dexusta.ticketcompra.util;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Subregion;

public class RegionList {
    private final Region mRegion;
    private final List<Subregion> mSubregions = new ArrayList<Subregion>();
    private final List<SubregionList> mSubregionLists = new ArrayList<SubregionList>();
    
    public RegionList(Region region) {
        mRegion = region;
    }
    
    public Region getRegion() {
        return mRegion;
    }
    
    public int getSubregionCount() {
        return mSubregionLists.size();
    }
    
    public int getTownCount() {
        int townCount = 0;
        
        for (SubregionList subregionList : mSubregionLists) {
            townCount += subregionList.getTownCount();
        }
        
        return townCount;
    }
    
    public long getId() {
        return mRegion.getId();
    }
    
    public void setId(long id) {
        mRegion.setId(id);
        if (mSubregions != null) {
            for (Subregion subregion : mSubregions) {
                subregion.setRegionId(id);
            }
        }
    }
    
    public String getName() {
        return mRegion.getName();
    }
    
    public void setName(String name) {
        mRegion.setName(name);
    }
    
    public void addSubregion(SubregionList subregionList) {
        mSubregions.add(subregionList.getSubregion());
        mSubregionLists.add(subregionList);
    }
    
    public List<SubregionList> getSubregionLists() {
        return mSubregionLists;
    }
    
    public List<Subregion> getSubregions() {        
        return mSubregions;
    }
    
    public void updateIds() {
        long id = mRegion.getId();  
        if (id > 0) {
            for (SubregionList subregionList : mSubregionLists) {
                subregionList.getSubregion().setRegionId(id);                
            }
        }
    }
        

}
