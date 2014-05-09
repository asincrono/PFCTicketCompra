package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;

/**
 * Created by asincrono on 07/05/14.
 */
public class RegionInfo {
    private HashMap<Region, HashMap<Subregion, HashMap<String, Town>>> mStructure = new HashMap<Region, HashMap<Subregion, HashMap<String, Town>>>();

    private HashMap<String, Region> mRegionMap   = new HashMap<String, Region>();
    private HashMap<Long, Region>   mRegionIdMap = new HashMap<Long, Region>();

    private HashMap<String, Subregion> mSubregionMap   = new HashMap<String, Subregion>();
    private HashMap<Long, Subregion>   mSubregionIdMap = new HashMap<Long, Subregion>();

    public void add(Region[] regions) {
        for (Region region : regions) {
            mRegionMap.put(region.getName(), region);
            mRegionIdMap.put(region.getId(), region);
            mStructure.put(region, null);
        }
    }

    public void add(Subregion[] subregions) {
        Region region;
        HashMap<Subregion, HashMap<String, Town>> subregionTwonMap;
        for (Subregion subregion : subregions) {
            mSubregionMap.put(subregion.getName(), subregion);
            mSubregionIdMap.put(subregion.getId(), subregion);

            region = mRegionIdMap.get(subregion.getRegionId());
            subregionTwonMap = mStructure.get(region);

            if (subregionTwonMap == null) {
                subregionTwonMap = new HashMap<Subregion, HashMap<String, Town>>();
                mStructure.put(region, subregionTwonMap);
            }

            subregionTwonMap.put(subregion, null);
        }
    }

    public void add(Town[] towns) {
        Subregion subregion;
        HashMap<Subregion, HashMap<String, Town>> subregionTownMap;
        HashMap<String, Town> townMap;

        for (Town town : towns) {
            subregion = mSubregionIdMap.get(town.getSubregionId());
            subregionTownMap = mStructure.get(mRegionIdMap.get(subregion.getRegionId()));


            townMap = subregionTownMap.get(subregion);
            if (townMap == null) {
                townMap = new HashMap<String, Town>();
                subregionTownMap.put(subregion, townMap);
            }

            townMap.put(town.getName(), town);
        }
    }

    public List<Region> getRegions() {
        return mRegionMap.isEmpty() ? null : new ArrayList<Region>(mRegionMap.values());
    }

    public List<Subregion> getSubregions(Region region) {
        HashMap<Subregion, HashMap<String, Town>> map =  mStructure.get(region);
        return (map == null) ? null : new ArrayList<Subregion>(map.keySet());
    }

    public List<Town> getTowns(Subregion subregion) {
        Region region = mRegionIdMap.get(subregion.getRegionId());
        if (region != null) {
            HashMap<String, Town> map = mStructure.get(region).get(subregion);
            return (map == null) ? null : new ArrayList<Town>(map.values());
        }

        return null;
    }

    public long getTownId(String townName) {
        Town town = null;
        Collection<HashMap<Subregion, HashMap<String, Town>>> subregionTownMaps =  mStructure.values();
        for (HashMap<Subregion, HashMap<String, Town>> subregionMap : subregionTownMaps) {
            Collection<HashMap<String, Town>> townMaps = subregionMap.values();

            for (HashMap<String, Town> townMap : townMaps) {
                town = townMap.get(townName);
                if (town != null) return town.getId();
            }
        }
        return -1;
    }

    public void clear () {
        mStructure = null;
        mRegionIdMap = null;
        mRegionMap = null;
        mSubregionIdMap = null;
        mSubregionMap = null;
    }
}
