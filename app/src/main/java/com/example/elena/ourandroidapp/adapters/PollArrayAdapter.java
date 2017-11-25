package com.example.elena.ourandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.model.Poll;

import java.util.List;

/**
 * Created by elena on 25/11/17.
 */

public class PollArrayAdapter extends ArrayAdapter<Poll> {
    public PollArrayAdapter(Context context, List<Poll> objects) {
        super(context, 0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){

        Poll poll = getItem(position);

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.poll_item, null);

            convertView.setTag(new  PollViewHolder(convertView));
        }

         PollViewHolder viewHolder = ( PollViewHolder) convertView.getTag();

        viewHolder.titleTV.setText(poll.getTitle());
        viewHolder.directorTV.setText(poll.getQuestion());

        return convertView;
    }
    static class PollViewHolder {

        TextView titleTV;
        TextView directorTV;

        public  PollViewHolder(View view){
            titleTV = (TextView) view.
                    findViewById(R.id.pollTitleTextView);
            directorTV = (TextView) view.
                    findViewById(R.id.pollQuestionTextView);
        }

    }
}
