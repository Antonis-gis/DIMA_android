package com.example.elena.ourandroidapp.data;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by elena on 23/01/18.
 */

public class OptionCursor extends CursorWrapper {
    public OptionCursor(Cursor cursor) {
        super(cursor);
    }

    public String getText(){
        return getString(getColumnIndex(PollDBContract.OptionEntry.OPTION_TEXT_CLMN));
    }

    public String getPollId(){
        return getString(getColumnIndex(PollDBContract.OptionEntry.POLL_ID_CLMN));

    }

    public String getId(){
        return getString(getColumnIndex(PollDBContract.OptionEntry._ID));

    }
    public Integer getVotesCount(){
        return getInt(getColumnIndex(PollDBContract.OptionEntry.VOTES_COUNT_CLMN));

    }


}
