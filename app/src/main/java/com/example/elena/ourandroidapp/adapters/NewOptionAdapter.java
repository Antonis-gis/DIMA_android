package com.example.elena.ourandroidapp.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.activities.NewPollActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laura on 3/02/18.
 */

public class NewOptionAdapter extends ArrayAdapter<NewPollActivity.OptionHolder> {
    //private ArrayList<String> list = new ArrayList<String>();
    public NewOptionAdapter(Context context, List<NewPollActivity.OptionHolder> objects) {
        super(context, 0, objects);
        //this.list = new ArrayList<String>(objects);

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup){
        final NewPollActivity.OptionHolder oh = getItem(position);
        String option = getItem(position).getText();


        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.new_option, null);

            convertView.setTag(new NewOptionAdapter.NewOptionViewHolder(convertView, this));
        }
        /*
        Button deleteBtn = (Button)convertView.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                remove(getItem(position)); //or some other task
                notifyDataSetChanged();
            }
        });
        */
        final NewOptionAdapter.NewOptionViewHolder viewHolder = (NewOptionAdapter.NewOptionViewHolder) convertView.getTag();


        viewHolder.optionText.setText(option);
        /*
        final EditText optionT = (EditText) convertView.findViewById(R.id.new_option_string);
        optionT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    Editable new_text = viewHolder.optionText.getText();
                    remove(getItem(position));
                    insert(new_text.toString(), position);

                }
            }
        });
        */
viewHolder.optionText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {

        } else {
            oh.setText(viewHolder.optionText.getText().toString());


        }
    }

});
        Button delBtn = (Button)convertView.findViewById(R.id.delete_btn);
        delBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                for(NewPollActivity.OptionHolder searched : oh.returnOptions()){
                    if(searched.getId()==oh.getId()){
                        oh.returnOptions().remove(searched);
                        break;
                    }
                }
                notifyDataSetChanged();


            }
        });


        return convertView;
    }
    static class NewOptionViewHolder {

        TextView name;
        EditText optionText;

        public  NewOptionViewHolder(View view, NewOptionAdapter newOptionAdapter){
            optionText = (EditText) view.
                    findViewById(R.id.new_option_string);


        }


    }


}
