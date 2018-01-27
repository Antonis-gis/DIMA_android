package com.example.elena.ourandroidapp.services;

import android.content.Context;

import com.example.elena.ourandroidapp.data.PollCursor;
import com.example.elena.ourandroidapp.data.PollSQLiteRepository;
import com.example.elena.ourandroidapp.model.Poll;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elena on 09/12/17.
 */

public class PollService {
    private PollSQLiteRepository repository;
    private static PollService instance;
    private PollService(Context context){

        repository = new PollSQLiteRepository(context);

    }
    public static synchronized PollService getInstance(Context context){
        if (instance == null)
            instance = new PollService(context);

        return instance;
    }

    public List<Poll> getAllPolls(){


        PollCursor pollCursor = repository.findAll();
        final int count = pollCursor.getCount();

        final List<Poll> polls = new ArrayList<>();

        while(pollCursor.moveToNext()){

        }


        return polls;

    }
}
