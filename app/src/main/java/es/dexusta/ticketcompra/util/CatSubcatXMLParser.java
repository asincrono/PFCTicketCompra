package es.dexusta.ticketcompra.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import es.dexusta.ticketcompra.model.Category;
import es.dexusta.ticketcompra.model.Subcategory;

public class CatSubcatXMLParser implements XmlParser {
    private static final String  TAG             = "CatSubcatStructureXMLParser";
    private static final boolean DEBUG           = true;

    private static final String  ATTR_NAME   = "name";
    private static final String  ns              = null;
    private static final String  TAG_STRUCTURE   = "structure";
    private static final String  TAG_CATEGORY    = "category";
    private static final String  TAG_SUBCATEGORY = "subcategory";

    private CatSubcatStructure   mStructure;

    @Override
    public CatSubcatStructure parse(XmlResourceParser parser) throws XmlPullParserException, IOException {
        mStructure = new CatSubcatStructure();
        Category category;
        List<Subcategory> subcategoryList;
        
        do {
            parser.next();
        } while (parser.getEventType() != XmlResourceParser.START_TAG);

        parser.require(XmlResourceParser.START_TAG, ns, TAG_STRUCTURE);
        parser.next();        
        parser.require(XmlResourceParser.START_TAG, ns, TAG_CATEGORY);
        
        do {
            if (parser.getEventType() == XmlResourceParser.START_TAG) {
                category = readCategory(parser);
                subcategoryList = readSubcategories(parser);
                mStructure.add(category, subcategoryList);
            }                                    
        } while (parser.next() != XmlResourceParser.END_DOCUMENT);
         
        return mStructure;
    }
    
    private Category readCategory(XmlResourceParser parser) throws XmlPullParserException, IOException {
        Category category = null;
        
        parser.require(XmlResourceParser.START_TAG, ns, TAG_CATEGORY);
        category = new Category();
        category.setName(parser.getAttributeValue(ns, ATTR_NAME));
        parser.next();
        
        return category;
    }

    private List<Subcategory> readSubcategories(XmlResourceParser parser) throws XmlPullParserException, IOException {
        Subcategory subcategory;
        List<Subcategory> list = new ArrayList<Subcategory>();        
        
        parser.require(XmlResourceParser.START_TAG, ns, TAG_SUBCATEGORY);
        
        // parará en end_tag de category.
        do {
            if (parser.getEventType() == XmlResourceParser.START_TAG) {
                subcategory = new Subcategory();
                subcategory.setName(parser.getAttributeValue(ns, ATTR_NAME));
                list.add(subcategory);
                parser.next(); // Skip the useless subcategory end_tag
                // Next event should be subcategory start_tag or category end_tag -> finish.
            }
        } while (parser.next() != XmlResourceParser.END_TAG);
        
        return list;
    }

}
