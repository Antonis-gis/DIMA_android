package com.example.elena.ourandroidapp.data;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by elena on 23/01/18.
 */

public class VoteCursor extends CursorWrapper {
    public VoteCursor(Cursor cursor) {
        super(cursor);
    }

    public String getOptionId(){
        return getString(getColumnIndex(PollDBContract.VoteEntry.OPTION_ID_CLMN));
    }

    public String getNumber(){
        return getString(getColumnIndex(PollDBContract.VoteEntry.PHONE_NUMBER_CLMN));

    }

    public String getOptionText(){
        return getString(getColumnIndex(PollDBContract.OptionEntry.OPTION_TEXT_CLMN));

    }

    public String getPollId(){
        return getString(getColumnIndex(PollDBContract.OptionEntry.POLL_ID_CLMN));

    }

}
