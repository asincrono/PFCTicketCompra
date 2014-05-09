package es.dexusta.ticketcompra.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dexusta.ticketcompra.model.Subcategory;

/**
 * Created by asincrono on 09/05/14.
 */
public class SubcategoryInfo {
    private HashMap<String, Subcategory> mStructure = new HashMap<String, Subcategory>();

    public void add(Subcategory[] subcategories) {
        for (Subcategory subcategory : subcategories) {
            mStructure.put(subcategory.getName(), subcategory);
        }
    }

    public void add(List<Subcategory> subcategoryList) {
        add(subcategoryList.toArray(new Subcategory[subcategoryList.size()]));
    }

    public long getId(String name) {
        Subcategory subcategory = mStructure.get(name);
        return (subcategory == null) ? -1 : subcategory.getId();
    }

    public Subcategory getSubcategory(String name) {
        return mStructure.get(name);
    }

    public List<Subcategory> getSubcategories() {
        return new ArrayList<Subcategory>(mStructure.values());
    }
}
