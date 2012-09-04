package com.matsuhiro.android.yahoo.topic;

import java.io.InputStream;

public abstract class BaseFeedParser implements FeedParser {
    static final String CHANNEL = "channel";
    static final String PUB_DATE = "pubDate";
    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String TITLE = "title";
    static final String ITEM = "item";
    
    private final InputStream _InputStrem;

    protected BaseFeedParser(InputStream is){
        this._InputStrem = is;
    }

    protected InputStream getInputStream() {
        return _InputStrem;
    }
}