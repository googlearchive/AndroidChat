package com.gsoltis.androidchat;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: greg
 * Date: 6/21/13
 * Time: 1:47 PM
 */
public abstract class FirebaseListAdapter<T> extends BaseAdapter {

    private Query ref;
    private Class<T> modelClass;
    private int layout;
    private LayoutInflater inflater;
    private List<T> models;
    private Map<String, T> modelNames;
    private ChildEventListener listener;


    public FirebaseListAdapter(Query ref, Class<T> modelClass, int layout, Activity activity) {
        this.ref = ref;
        this.modelClass = modelClass;
        this.layout = layout;
        inflater = activity.getLayoutInflater();
        models = new ArrayList<T>();
        modelNames = new HashMap<String, T>();
        listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("out: " + System.currentTimeMillis());
                long start = System.currentTimeMillis();
                T model = dataSnapshot.getValue(FirebaseListAdapter.this.modelClass);
                long postParse = System.currentTimeMillis();
                models.add(model);
                modelNames.put(dataSnapshot.getName(), model);

                notifyDataSetChanged();
                long end = System.currentTimeMillis();
                Log.i("FirebaseListAdapter", "Child added took " + (end - start) + "ms, " + (postParse - start) + "ms parsing, " +
                    (end - postParse) + "ms notifying"
                );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String modelName = dataSnapshot.getName();
                T oldModel = modelNames.get(modelName);
                T newModel = dataSnapshot.getValue(FirebaseListAdapter.this.modelClass);
                int index = models.indexOf(oldModel);

                models.set(index, newModel);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String modelName = dataSnapshot.getName();
                T oldModel = modelNames.get(modelName);
                models.remove(oldModel);
                modelNames.remove(modelName);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                String modelName = dataSnapshot.getName();
                T oldModel = modelNames.get(modelName);
                T newModel = dataSnapshot.getValue(FirebaseListAdapter.this.modelClass);
                int index = models.indexOf(oldModel);
                models.remove(index);
                if (previousChildName == null) {
                    models.add(0, newModel);
                } else {
                    T previousModel = modelNames.get(previousChildName);
                    int previousIndex = models.indexOf(previousModel);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == models.size()) {
                        models.add(newModel);
                    } else {
                        models.add(nextIndex, newModel);
                    }
                }
            }

            @Override
            public void onCancelled() {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }
        });
    }

    public void cleanup() {
        ref.removeEventListener(listener);
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
        }

        T model = models.get(i);
        populateView(view, model);
        return view;
    }

    protected abstract void populateView(View v, T model);
}
