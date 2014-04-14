package es.dexusta.ticketcompra.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.Log;
import es.dexusta.ticketcompra.model.Chain;

public class ChainsXmlParser implements XmlParser {
    private static final String TAG = "ChainsXMLParser";
    private static final boolean DEBUG = true;
    
    private static final String ATTR_NAME = "name";
    private static final String TAG_CHAINS = "chains";
    private static final String TAG_CHAIN = "chain";
    
    private static final String ns = null;

    @Override
    public List<Chain> parse(XmlResourceParser parser) throws XmlPullParserException, IOException {
        List<Chain> chains = null;
        Chain chain = null;

        do {
            parser.next();
        } while (parser.getEventType() != XmlResourceParser.START_TAG);
        
        parser.require(XmlResourceParser.START_TAG, ns, TAG_CHAINS);        
        parser.next();
        parser.require(XmlResourceParser.START_TAG, ns, TAG_CHAIN);
        
        chains = new ArrayList<Chain>();
        
        do {            
            switch (parser.getEventType()) {
            case XmlResourceParser.START_TAG:
                if (DEBUG) Log.d(TAG, "START_TAG: " + parser.getName());    
                break;
            case XmlResourceParser.END_TAG:
                if (DEBUG) Log.d(TAG, "END_TAG: " + parser.getName());
                break;
            }
            
            chain = new Chain();
            chain.setName(parser.getAttributeValue(ns, ATTR_NAME));
            chains.add(chain);
            parser.next(); // Skip useless event chain end_tag.
            // Next event should be chain start_tag or chains end_tag -> finish.
        } while (parser.next() == XmlResourceParser.START_TAG);        
        
        return chains;  
    }
    
}
