package com.firebase.androidchat;

import com.firebase.client.Firebase;

/**
 * Created by mimming on 12/5/14.
 */
public class ChatApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
