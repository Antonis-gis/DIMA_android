package com.example.elena.ourandroidapp.adapters;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;

/**
 * Created by laura on 4/02/18.
 */

public class MyOptionAdapter extends ArrayAdapter<String> {

    public MyOptionAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup){
        String option = getItem(position);


        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.new_option, null);

            convertView.setTag(new OptionAdapter(convertView, this));
        }
        final String str;
        final Button voteBtn = (Button)convertView.findViewById(R.id.vote_btn);
        final TextView voteTxt = (TextView)convertView.findViewById(R.id.vote_txt);
        str = "Your vote has been saved";
        voteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                voteTxt.setText(str); //or some other task
                notifyDataSetChanged();
                voteBtn.setVisibility(View.GONE);
            }
        });
        final OptionAdapter viewHolder = (OptionAdapter) convertView.getTag();

        return convertView;
    }
    static class OptionAdapter {

        TextView name;
        EditText optionText;

        public OptionAdapter(View view, MyOptionAdapter MyOptionAdapter){
            optionText = (EditText) view.
                    findViewById(R.id.new_option_string);


        }


    }
}
