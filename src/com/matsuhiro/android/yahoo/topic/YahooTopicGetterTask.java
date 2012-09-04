package com.matsuhiro.android.yahoo.topic;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class YahooTopicGetterTask extends AsyncTask<Void, Void, List<Message> >{
    private static String TAG = "YAHOOTOPIC";
    private YahooTopicListener mNotifyer;
    
    public YahooTopicGetterTask(YahooTopicListener notifyer) {
        this.mNotifyer = notifyer;
    }
    
    @Override
    protected List<Message> doInBackground(Void... params) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.encodedAuthority("rss.dailynews.yahoo.co.jp");
        builder.path("/fc/domestic/rss.xml");
        Log.d(TAG, "builder.build().toString()="+builder.build().toString());
        HttpGet httpget = new HttpGet(builder.build().toString());
        HttpResponse response;
        List<Message> messages = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        try {
            response = httpClient.execute(httpget);
            
            int status_code = response.getStatusLine().getStatusCode();
            Log.d(TAG,"status_code="+status_code);
            if (status_code < 400){
                InputStream istream = response.getEntity().getContent();
                FeedParser parser = new YahooTopicSaxFeedParser(istream);
                messages = parser.parse();
//                for (Message msg : messages){
//                    Log.d(TAG, "msg.getTitle()="+msg.getTitle());
//                    Log.d(TAG, "msg.getLink()="+msg.getLink());
//                }
                istream.close();
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // TODO finalize
        }
        return messages;
    }

    @Override
    protected void onPostExecute(List<Message> messages) {
        if (messages == null) {
            mNotifyer.notifyError();
            return;
        }
        mNotifyer.notifyTopics(messages);
    }
    
    public interface YahooTopicListener {
        void notifyTopics(List<Message> messages);
        void notifyError();
    }
}
