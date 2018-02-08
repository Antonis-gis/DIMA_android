package com.example.elena.ourandroidapp.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.activities.ChooseContactsActivity;
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;

import java.util.List;

/**
 * Created by elena on 25/11/17.
 */

public class ContactArrayAdapter extends ArrayAdapter<ChooseContactsActivity.BoolContactEntry> {
    Context context;
    public ContactArrayAdapter(Context context, List<ChooseContactsActivity.BoolContactEntry> objects) {
        super(context, 0, objects);
        this.context=context;

    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup){
        final ChooseContactsActivity.BoolContactEntry cb = getItem(position);
        ///Pair<Boolean, Contact> pair = getItem(position);
        //final Contact contact = pair.second;
final Contact contact=cb.getContact();


        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = layoutInflater.inflate(R.layout.contact_item, null);

            convertView.setTag(new  ContactViewHolder(convertView));
        }

         final ContactViewHolder viewHolder = ( ContactViewHolder) convertView.getTag();

            ///viewHolder.cb.setChecked(cb.getChoosen());


        //viewHolder.name.setText(contact.getName());
        viewHolder.cName.setText(contact.getName());
        if(cb.getChoosen()){
            viewHolder.cp.setVisibility(View.VISIBLE);
        }else{

            viewHolder.cp.setVisibility(View.GONE);
        }
final View finalView=convertView;
/*
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                //viewHolder.cName.performClick();
                if(isChecked){
                    cb.setChoosen(true);
                }else{
                    cb.setChoosen(false);

                }

            }
        });
        */


        return convertView;
    }
    static class ContactViewHolder {
ImageView cp;

        TextView cName;

        public  ContactViewHolder(View view){
            cName = (TextView) view.
                    findViewById(R.id.contact_name);
            cp = view.findViewById(R.id.changedPic);
            //cb =  view.findViewById(R.id.checkBox);
        }

    }
}
