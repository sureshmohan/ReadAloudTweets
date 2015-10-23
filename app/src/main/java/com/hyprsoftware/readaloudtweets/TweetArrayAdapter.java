package com.hyprsoftware.readaloudtweets;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.Tweet;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by suresh on 9/20/2015.
 */
public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    private final Context cntxt;
    private final List<Tweet> tweets;

    public TweetArrayAdapter(Context context, int resource, List<Tweet> objects) {
        super(context, resource, objects);

        cntxt = context;
        tweets = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) cntxt
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.tweet_card, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.tw__tweet_author_full_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView tweetText=(TextView) rowView.findViewById(R.id.tw__tweet_text);

        final Tweet twt = tweets.get(position);
        textView.setText(twt.user.name);
        tweetText.setText(twt.text);

        if(twt.entities.media != null && twt.entities.media.get(0).mediaUrl != null) {
            Uri uu = Uri.parse(twt.entities.media.get(0).mediaUrl);
            imageView.setImageURI(uu);
        }
        return rowView;
    }
}
