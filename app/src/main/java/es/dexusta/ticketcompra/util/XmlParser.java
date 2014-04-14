package es.dexusta.ticketcompra.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;

public interface XmlParser {
    Object parse (XmlResourceParser parser) throws XmlPullParserException, IOException;
}
