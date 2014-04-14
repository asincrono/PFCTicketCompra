package es.dexusta.ticketcompra.util;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public interface XmlParser {
    Object parse (XmlResourceParser parser) throws XmlPullParserException, IOException;
}
