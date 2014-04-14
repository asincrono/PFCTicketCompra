package es.dexusta.ticketcompra.util;

import java.util.ArrayList;
import java.util.List;

import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;

public class SubregionList {
    private final Subregion mSubregion;
    private final List<Town> mTowns = new ArrayList<Town>();
    
    public SubregionList(Subregion subregion) {
        mSubregion = subregion;
    }
    
    public Subregion getSubregion() {
        return mSubregion;
    }
    
    public int getTownCount() {
        return mTowns.size();
    }
    
    public long getId() {
        return mSubregion.getId();
    }
    
    public void setId(long id) {
        mSubregion.setId(id);
        if (mTowns != null) {
            for (Town town : mTowns) {
                town.setSubregionId(id);
            }
        }
    }
    
    public void setRegionId(long id) {
        mSubregion.setRegionId(id);
    }
    
    public long getRegionId() {
        return mSubregion.getRegionId();
    }
    
    public String getName() {
        return mSubregion.getName();
    }
    
    public void setName(String name) {
        mSubregion.setName(name);
    }
    
    public void addTown(Town town) {
        mTowns.add(town);
    }
    
    public List<Town> getTowns() {
        return mTowns;
    }
    
    public void updateIds() {
        long id = mSubregion.getId();
        if (id > 0) {
            for (Town town : mTowns) {
                town.setSubregionId(id);
            }
        }
    }

}
