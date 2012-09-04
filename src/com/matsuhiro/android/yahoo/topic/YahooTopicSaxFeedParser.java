package com.matsuhiro.android.yahoo.topic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class YahooTopicSaxFeedParser extends BaseFeedParser {
    static final String RSS = "rss";
    
    public YahooTopicSaxFeedParser(InputStream is) {
        super(is);
    }

    public List<Message> parse() {
        final Message currentMessage = new Message();
        RootElement root = new RootElement(RSS);
        final List<Message> messages = new ArrayList<Message>();
        Element channel = root.getChild(CHANNEL);
        Element item = channel.getChild(ITEM);
        item.setEndElementListener(new EndElementListener(){
            public void end() {
                messages.add(currentMessage.copy());
            }
        });
        item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setTitle(body);
            }
        });
        item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setLink(body);
            }
        });
        item.getChild(PUB_DATE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setDate(body);
            }
        });
        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return messages;
    }
}
