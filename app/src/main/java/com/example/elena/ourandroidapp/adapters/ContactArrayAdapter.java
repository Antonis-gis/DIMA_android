package com.example.elena.ourandroidapp.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;

import java.util.List;

/**
 * Created by elena on 25/11/17.
 */

public class ContactArrayAdapter extends ArrayAdapter<Pair<Boolean, Contact>> {
    public ContactArrayAdapter(Context context, List<Pair<Boolean, Contact>> objects) {
        super(context, 0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){

        Pair<Boolean, Contact> pair = getItem(position);
        Contact contact = pair.second;

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.contact_item, null);

            convertView.setTag(new  ContactViewHolder(convertView));
        }

         ContactViewHolder viewHolder = ( ContactViewHolder) convertView.getTag();

        //viewHolder.name.setText(contact.getName());
        viewHolder.cName.setText(contact.getName());

        return convertView;
    }
    static class ContactViewHolder {


        TextView cName;

        public  ContactViewHolder(View view){
            cName = (TextView) view.
                    findViewById(R.id.contact_name);
            //name = (TextView) view.
                //    findViewById(R.id.pollQuestionTextView);
        }

    }
}
