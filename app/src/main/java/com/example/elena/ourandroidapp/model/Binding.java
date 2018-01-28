package com.example.elena.ourandroidapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by elena on 28/01/18.
 */

public class Binding {
    private DatabaseReference ref;
    private ValueEventListener listener;

    public DatabaseReference getRef() {
        return ref;
    }

    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }

    public ValueEventListener getListener() {
        return listener;
    }

    public void setListener(ValueEventListener listener) {
        this.listener = listener;
    }

    public Binding(DatabaseReference ref, ValueEventListener listener){
        this.ref = ref;
        this.listener =listener;
    }

}
