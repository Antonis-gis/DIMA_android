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
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by laura on 3/02/18.
 */

public class Option_Adapter extends ArrayAdapter<Poll.Option> {
    int numberOfParticipants;
    int voted;
    public Option_Adapter(Context context, List<Poll.Option> objects, Poll poll) {
        super(context, 0, objects);
        this.numberOfParticipants = poll.getParticipants().size();
        this.voted=poll.checkIfVoted();
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
        int optionPopularity = (int) (((float)option.getVotesCount()/numberOfParticipants)*100);
        viewHolder.mProgress.setProgress(optionPopularity);
        if(option instanceof PollNotAnonymous.OptionNotAnonymous){
            viewHolder.showBtn.setVisibility(View.VISIBLE);
            String str="";
            for (String voted_participant : ((PollNotAnonymous.OptionNotAnonymous) option).getVoted()){
                Contact c = GlobalContainer.getContacts().get(voted_participant);
                if(c!=null) {

                        str += GlobalContainer.getContacts().get(voted_participant).getName() + ", ";

                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                    mPhoneNumber=mPhoneNumber.replaceAll("\\s+","");
                    if(voted_participant.equals(mPhoneNumber)){
                        str += "you, ";
                    }
                    else {
                        str += voted_participant + ", ";
                    }
                }
            }

            if(str.length()>2) {
                str = str.substring(0, str.length() - 2);
            }
            viewHolder.votedText.setText(str);
        }
        if(voted==1){
            viewHolder.voteBtn.setVisibility(View.GONE);
        }


        return convertView;
    }
    static class NewOptionViewHolder {

        TextView optionText;
        ProgressBar mProgress;
        TextView votedText;
        Button showBtn;
        Button voteBtn;

        public  NewOptionViewHolder(View view, Option_Adapter newOptionAdapter){
            optionText = (TextView) view.
                    findViewById(R.id.option_string);
            mProgress=view.findViewById(R.id.votes_progress_bar);
            votedText = view.findViewById(R.id.list_of_voted);
            showBtn = view.findViewById(R.id.show_vote_participants);
            voteBtn = view.findViewById(R.id.vote_btn);


        }


    }


}
