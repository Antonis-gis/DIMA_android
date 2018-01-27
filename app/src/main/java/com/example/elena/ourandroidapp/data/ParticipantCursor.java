package com.example.elena.ourandroidapp.data;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by elena on 24/01/18.
 */

public class ParticipantCursor extends CursorWrapper {
    public ParticipantCursor(Cursor cursor) {
        super(cursor);
    }



    public String getPollId(){
        return getString(getColumnIndex(PollDBContract.ParticipantEntry.POLL_ID_CLMN));

    }

    public String getId(){
        return getString(getColumnIndex(PollDBContract.ParticipantEntry._ID));

    }
    public String getNumber(){
        return getString(getColumnIndex(PollDBContract.ParticipantEntry.PHONE_NUMBER_CLMN));

    }


}