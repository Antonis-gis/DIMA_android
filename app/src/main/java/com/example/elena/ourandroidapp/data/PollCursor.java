package com.example.elena.ourandroidapp.data;

import android.database.Cursor;
import android.database.CursorWrapper;

//should it extract all tables???? No, bt how to extract then?
/**
 * Created by elena on 08/12/17.
 */

public class PollCursor extends CursorWrapper{
    public PollCursor(Cursor cursor) {
        super(cursor);
    }

    public String getName(){
        return getString(getColumnIndex(PollDBContract.PollEntry.POLL_NAME_CLMN));
    }

    public String getId(){
        return getString(getColumnIndex(PollDBContract.PollEntry._ID));

    }
    public String getQuestion(){
        return getString(getColumnIndex(PollDBContract.PollEntry.POLL_QUESTION_CLMN));

    }
    public Integer getIfAnon(){
        return getInt(getColumnIndex(PollDBContract.PollEntry.POLL_ANON_CLMN));

    }

    public Integer getIfChanged(){
        return getInt(getColumnIndex(PollDBContract.PollEntry.POLL_CHANGED_CLMN));

    }

    public Integer getIfAlreadyVoted(){
        return getInt(getColumnIndex(PollDBContract.PollEntry.POLL_ALREADY_VOTED_CLMN));

    }


}



