package com.example.elena.ourandroidapp.data;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by elena on 24/01/18.
 */

public class ContactCursor extends CursorWrapper {
    public ContactCursor(Cursor cursor) {
        super(cursor);
    }

    public String getId(){
        return getString(getColumnIndex(PollDBContract.ContactEntry._ID));
    }

    public String getNumber(){
        return getString(getColumnIndex(PollDBContract.ContactEntry.PHONE_NUMBER_CLMN));

    }

    public String getName(){
        return getString(getColumnIndex(PollDBContract.ContactEntry.CONTACT_NAME_CLMN));

    }


}