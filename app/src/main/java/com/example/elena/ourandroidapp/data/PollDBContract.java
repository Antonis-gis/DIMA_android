package com.example.elena.ourandroidapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by elena on 08/12/17.
 */

public class PollDBContract {
    private static final String DATABASE_NAME = "PollsRepository.db";

    private static final String SQL_CREATE_POLL_TABLE = "CREATE TABLE IF NOT EXISTS "+PollEntry.TABLE_NAME+
            " ("+PollEntry._ID+" varchar PRIMARY KEY, "+
            PollEntry.POLL_NAME_CLMN+" text,  "+ PollEntry.POLL_QUESTION_CLMN+" text, "+ PollEntry.POLL_ANON_CLMN+" integer, "+ PollEntry.POLL_CHANGED_CLMN+" integer, UNIQUE ("+ PollEntry._ID + ") ON CONFLICT REPLACE)";
    private static final String SQL_CREATE_OPTION_TABLE = "CREATE TABLE IF NOT EXISTS "+OptionEntry.TABLE_NAME+
            " ("+OptionEntry._ID+" integer PRIMARY KEY AUTOINCREMENT, "+
            OptionEntry.OPTION_TEXT_CLMN+" text, "
            +OptionEntry.VOTES_COUNT_CLMN+" integer, "+
            OptionEntry.POLL_ID_CLMN+" integer, " +"FOREIGN KEY("+ OptionEntry.POLL_ID_CLMN +") REFERENCES "+ PollEntry.TABLE_NAME + "(_id) ON DELETE CASCADE, UNIQUE ("+ OptionEntry.OPTION_TEXT_CLMN + " , " + OptionEntry.POLL_ID_CLMN + ") ON CONFLICT REPLACE)";
    private static final String SQL_CREATE_VOTE_TABLE = "CREATE TABLE IF NOT EXISTS "+VoteEntry.TABLE_NAME+
            " ("+PollEntry._ID+" integer PRIMARY KEY AUTOINCREMENT, "+
            VoteEntry.PHONE_NUMBER_CLMN +" varchar , "+
            VoteEntry.OPTION_ID_CLMN+" integer," +"FOREIGN KEY("+ VoteEntry.OPTION_ID_CLMN +") REFERENCES "+ OptionEntry.TABLE_NAME + "(_id) ON DELETE CASCADE, UNIQUE ("+ VoteEntry.PHONE_NUMBER_CLMN + " , " + VoteEntry.OPTION_ID_CLMN + ") ON CONFLICT REPLACE)";
    private static final String SQL_CREATE_PARTICIPANT_TABLE = "CREATE TABLE IF NOT EXISTS "+ParticipantEntry.TABLE_NAME+
            " ("+ParticipantEntry._ID+" integer PRIMARY KEY AUTOINCREMENT, "+
            ParticipantEntry.PHONE_NUMBER_CLMN +" varchar , "+
            ParticipantEntry.POLL_ID_CLMN+" integer," +"FOREIGN KEY("+ ParticipantEntry.POLL_ID_CLMN +") REFERENCES "+ PollEntry.TABLE_NAME + "(_id) ON DELETE CASCADE, UNIQUE ("+ ParticipantEntry.PHONE_NUMBER_CLMN + " , " + ParticipantEntry.POLL_ID_CLMN + ") ON CONFLICT REPLACE)";
    private static final String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE IF NOT EXISTS "+ContactEntry.TABLE_NAME+
            " ("+ContactEntry._ID+" integer PRIMARY KEY AUTOINCREMENT, "+
            ContactEntry.CONTACT_NAME_CLMN+" text,  "+ ContactEntry.PHONE_NUMBER_CLMN+" text,  UNIQUE ("+ ContactEntry.CONTACT_NAME_CLMN + ") ON CONFLICT REPLACE)";
    public static SQLiteDatabase getWritableDatabase(Context context){
        return new PollDBHelper(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context){
        return new PollDBHelper(context).getReadableDatabase();
    }

    public static class PollEntry implements BaseColumns {

        public static final String TABLE_NAME = "poll";
        public static final String POLL_NAME_CLMN = "poll_name";
        public static final String POLL_QUESTION_CLMN = "poll_question";
        public static final String POLL_ANON_CLMN = "poll_anonymous";
        public static final String POLL_CHANGED_CLMN = "poll_changed";

    }
    public static class OptionEntry implements BaseColumns {

        public static final String TABLE_NAME = "option";
        public static final String OPTION_TEXT_CLMN = "option_text";
        public static final String POLL_ID_CLMN = "poll_id";
        public static final String VOTES_COUNT_CLMN = "votes_count";

    }
    public static class VoteEntry implements BaseColumns {

        public static final String TABLE_NAME = "vote";
        public static final String OPTION_ID_CLMN = "option_id";
        public static final String PHONE_NUMBER_CLMN = "phone_number";

    }
    public static class ParticipantEntry implements BaseColumns {

        public static final String TABLE_NAME = "participant";
        public static final String PHONE_NUMBER_CLMN = "phone_number";
        public static final String POLL_ID_CLMN = "poll_id";

    }

    public static class ContactEntry implements BaseColumns {

        public static final String TABLE_NAME = "contact";
        public static final String CONTACT_NAME_CLMN = "contact_name";
        public static final String PHONE_NUMBER_CLMN = "contact_number";

    }

    private static class PollDBHelper extends SQLiteOpenHelper {

        public PollDBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_POLL_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_OPTION_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_VOTE_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_PARTICIPANT_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_CONTACT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }
}
}

