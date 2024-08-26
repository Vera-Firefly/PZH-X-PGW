package net.kdt.pojavlaunch.modloaders;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class ForgeVersionListHandler extends DefaultHandler {
    private List<String> mForgeVersions;
    private StringBuilder mCurrentVersion = null;
    @Override
    public void startDocument() throws SAXException {
        mForgeVersions = new ArrayList<>();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(mCurrentVersion != null) mCurrentVersion.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equals("version")) mCurrentVersion = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("version")) {
            String version = mCurrentVersion.toString();
            mForgeVersions.add(version);
            mCurrentVersion = null;
        }
    }
    public List<String> getVersions() {
        return mForgeVersions;
    }
}
