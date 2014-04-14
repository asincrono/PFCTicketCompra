package es.dexusta.ticketcompra.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Subcategory;

public class CatSubcatStructure implements Iterable<Category> {
    private List<Category>                       mCategories = new ArrayList<Category>();
    private HashMap<Category, List<Subcategory>> mContents = new HashMap<Category, List<Subcategory>>();
    
    public void updateCategoryId(Category category) {
        long id = category.getId(); 
        List<Subcategory> subcategories = getSubcategories(category);
        if (subcategories != null) {
            for (Subcategory subcategory : subcategories) {
                subcategory.setCategoryId(id);
            }
        }        
    }

    public void add(Category category) {
        mCategories.add(category);
        mContents.put(category, null);
    }    
    
    public void add(Category category, Subcategory subcategory) {
        List<Subcategory> list = mContents.get(category);
        boolean newEntry = false;

        if (list == null) {
            newEntry = true;
            list = new ArrayList<Subcategory>();
        }

        list.add(subcategory);

        if (newEntry) {            
            mCategories.add(category);
            mContents.put(category, list);
        }
    }
    
    public void add(Category category, List<Subcategory> subactegoryList) {
        List<Subcategory> list = mContents.get(category);
        boolean newEntry = false;
        
        if (list == null) {
            newEntry = true;
            list = new ArrayList<Subcategory>();
        }
        
        list.addAll(subactegoryList);
        
        if (newEntry) {
            mCategories.add(category);
            mContents.put(category, list);
        }        
    }
    
    public List<Category> getCategories() {
        return mCategories;
    }

    public int getCategoriesNumber() {
        return mCategories.size();
    }
    
    public List<Subcategory> getSubcategories() {
        List<Subcategory> list = new ArrayList<Subcategory>();
        for (Category category : mCategories) {
            list.addAll(mContents.get(category));
        }
        return list;
    }
    
    public int getSubcategoriesNumber(Category category) {
        List<Subcategory> subcategories = mContents.get(category);
        
        if (subcategories == null) return 0;
       
        return subcategories.size();
    }
    
    public int getSubcategoriesNumber() {
        int counter = 0;
        for (Category cat : mCategories) {
            counter += getSubcategoriesNumber(cat);
        }
        return counter;
    }
    
    public Category getCategory(int position) {
        return mCategories.get(position);
    }    
    
    public List<Subcategory> getSubcategories(int position) {
        return mContents.get(mCategories.get(position));
    }
    
    public List<Subcategory> getSubcategories(Category category) {
        return mContents.get(category);
    }

    @Override
    public Iterator<Category> iterator() {
        return mCategories.iterator();        
    }

}
