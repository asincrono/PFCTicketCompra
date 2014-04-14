package es.dexusta.ticketcompra.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.os.AsyncTask;

public class XmlParserTask extends AsyncTask<Void, Void, Object> {
    private static final String TAG = "XmlParserTask";
    private static final boolean DEBUG = true;

    private XmlResourceParser mResourceParser;
    private XmlParser mXmlParser;
    
    private XmlParserCallbacks mXmlParserCallbacks;
    
    public XmlParserTask(XmlResourceParser resourceParser, XmlParser parser, XmlParserCallbacks parserCallbacks) {
        mResourceParser = resourceParser;
        mXmlParser = parser;
        mXmlParserCallbacks = parserCallbacks;
    }

    @Override
    protected Object doInBackground(Void... params) {        
        
        Object result = null;
        try {
            result = mXmlParser.parse(mResourceParser);
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }   // TODO Auto-generated method stub
        return result;
    }

    @Override
    protected void onPostExecute(Object result) {
        mXmlParserCallbacks.onXmlParsed(result);
    }

    public interface XmlParserCallbacks {
        public void onXmlParsed(Object result);
    }

}
