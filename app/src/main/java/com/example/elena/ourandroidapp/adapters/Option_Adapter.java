package com.example.elena.ourandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.model.Poll;

import java.util.List;

/**
 * Created by laura on 3/02/18.
 */

public class Option_Adapter extends ArrayAdapter<Poll.Option> {
    int numberOfParticipants;
    public Option_Adapter(Context context, List<Poll.Option> objects, int numberOfParticipants) {
        super(context, 0, objects);
        this.numberOfParticipants = numberOfParticipants;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup){
        Poll.Option option = getItem(position);


        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.poll_option, null);

            convertView.setTag(new Option_Adapter.NewOptionViewHolder(convertView, this));
        }

        final Option_Adapter.NewOptionViewHolder viewHolder = (Option_Adapter.NewOptionViewHolder) convertView.getTag();


        viewHolder.optionText.setText(option.getText());
        if(numberOfParticipants==0){
            numberOfParticipants=1;
        }
        int optionPopularity = (option.getVotesCount()/numberOfParticipants)*100;
        viewHolder.mProgress.setProgress(optionPopularity);


        return convertView;
    }
    static class NewOptionViewHolder {

        TextView optionText;
        ProgressBar mProgress;

        public  NewOptionViewHolder(View view, Option_Adapter newOptionAdapter){
            optionText = (TextView) view.
                    findViewById(R.id.option_string);
            mProgress=view.findViewById(R.id.votes_progress_bar);


        }


    }


}
