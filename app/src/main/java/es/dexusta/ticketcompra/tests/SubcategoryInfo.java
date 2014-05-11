package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Subcategory;

/**
 * Created by asincrono on 09/05/14.
 */
public class SubcategoryInfo {
    private HashMap<String, Subcategory> mData = new HashMap<String, Subcategory>();

    public SubcategoryInfo(List<Subcategory> subcategories) {
        add(subcategories);
    }

    public SubcategoryInfo(Subcategory[] subcategories) {
        add(subcategories);
    }

    public void add(Subcategory[] subcategories) {
        for (Subcategory subcategory : subcategories) {
            mData.put(subcategory.getName(), subcategory);
        }
    }

    public void add(List<Subcategory> subcategories) {
        for (Subcategory subcategory : subcategories) {
            mData.put(subcategory.getName(), subcategory);
        }
    }

    public long getId(String name) {
        Subcategory subcategory = mData.get(name);
        return (subcategory == null) ? -1 : subcategory.getId();
    }

    public Subcategory getSubcategory(String name) {
        return mData.get(name);
    }

    public List<Subcategory> getSubcategories() {
        return new ArrayList<Subcategory>(mData.values());
    }
}
