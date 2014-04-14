package es.dexusta.ticketcompra.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.Log;
import es.dexusta.ticketcompra.model.Region;
import es.dexusta.ticketcompra.model.Subregion;
import es.dexusta.ticketcompra.model.Town;

public class RegionXMLParser implements XmlParser {
    private static final String TAG = "RegionXMLParser";
    private static final boolean DEBUG = false ;
    
    private static final String KEY_ATTR_NAME = "name";
    
    // We don't use namespaces ???
    private static final String ns = null;

    public CountryList parse(XmlResourceParser parser) throws XmlPullParserException, IOException {
        CountryList countryList = null;
//        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        
          while (parser.next() != XmlResourceParser.START_TAG) {
            switch (parser.getEventType()) {
            case XmlResourceParser.START_DOCUMENT:
                if (DEBUG) Log.d(TAG, "parse: " + "StartDocument");
                break;

            case XmlResourceParser.END_TAG:
                if (DEBUG) Log.d(TAG, "parse: " + "StartTag" + parser.getName());
                break;
            default:
                break;
            }
        };
        
        countryList = readCountry(parser);

        return countryList;
    }
    
    private CountryList readCountry(XmlResourceParser parser) throws XmlPullParserException, IOException {
        CountryList countryList = new CountryList();        
        
        // Security check.
        parser.require(XmlPullParser.START_TAG, ns, "country");
        
        countryList.setName(parser.getAttributeValue(null, KEY_ATTR_NAME));
        
        while(parser.next() != XmlPullParser.END_TAG) {        
            countryList.addRegion(readRegion(parser));
        }
        
        return countryList;
    }
    
    private RegionList readRegion(XmlResourceParser parser) throws XmlPullParserException, IOException {
        RegionList regionList = new RegionList(new Region());
                
        // Security check.        
        parser.require(XmlPullParser.START_TAG, ns, "region");
        
        regionList.setName(parser.getAttributeValue(ns, KEY_ATTR_NAME));

        while(parser.next() != XmlPullParser.END_TAG) {        
            regionList.addSubregion(readSubregion(parser));
        }
        
        return regionList;
    }
    
    private SubregionList readSubregion(XmlResourceParser parser) throws XmlPullParserException, IOException {
        SubregionList subregionList = new SubregionList(new Subregion());
        
        parser.require(XmlPullParser.START_TAG, ns, "subregion");
        
        subregionList.setName(parser.getAttributeValue(ns, KEY_ATTR_NAME));        

        Town town;
        
        parser.next();        
        
        while (parser.getName().equals("town")) {
            // NOTA: Para consumir <town name="hola"/> hacen falta dos parser.next();
            // Uno para START_TAG (en el que se puede obtener el atributo).
            // Y otro para END_TAG (que devuelve atributo null si se consulta el atributo).

            if (parser.getEventType() == XmlPullParser.START_TAG) {            
                town = new Town();
                town.setName(parser.getAttributeValue(ns, KEY_ATTR_NAME));
                subregionList.addTown(town);
            }
            parser.next();       
        }
        
        return subregionList;
    }
}
