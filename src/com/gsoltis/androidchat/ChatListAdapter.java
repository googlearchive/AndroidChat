package com.gsoltis.androidchat;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import com.firebase.client.Query;

/**
 * User: greg
 * Date: 6/21/13
 * Time: 2:39 PM
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    private String username;

    public ChatListAdapter(Query ref, Activity activity, int layout, String username) {
        super(ref, Chat.class, layout, activity);
        this.username = username;
    }

    @Override
    protected void populateView(View view, Chat chat) {
        String author = chat.getAuthor();
        TextView authorText = (TextView)view.findViewById(R.id.author);
        authorText.setText(author + ": ");
        if (author.equals(username)) {
            authorText.setTextColor(Color.RED);
        } else {
            authorText.setTextColor(Color.BLUE);
        }
        ((TextView)view.findViewById(R.id.message)).setText(chat.getMessage());
    }
}
