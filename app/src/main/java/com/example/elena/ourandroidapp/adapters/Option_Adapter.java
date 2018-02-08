package com.example.elena.ourandroidapp.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.elena.ourandroidapp.activities.ItemActivity;
import com.example.elena.ourandroidapp.services.ApplicationContextProvider;
import com.example.elena.ourandroidapp.R;
import com.example.elena.ourandroidapp.data.PollSQLiteRepository;
import com.example.elena.ourandroidapp.model.Contact;
import com.example.elena.ourandroidapp.model.Poll;
import com.example.elena.ourandroidapp.model.PollNotAnonymous;
import com.example.elena.ourandroidapp.services.DatabaseService;
import com.example.elena.ourandroidapp.services.GlobalContainer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by laura on 3/02/18.
 */

public class Option_Adapter extends ArrayAdapter<Poll.Option> {
    int numberOfParticipants;
    int voted;
    String poll_id;
    //ItemActivity.optionsProvider op;
    public Option_Adapter(Context context, List<Poll.Option> objects, String poll_id) {
        super(context, 0, objects);
        this.numberOfParticipants = GlobalContainer.getPolls().get(poll_id).getParticipants().size();
        this.voted=GlobalContainer.getPolls().get(poll_id).checkIfVoted();
        this.poll_id=poll_id;
        //this.op= op;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup){
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
        viewHolder.votesNumber.setText(Integer.toString(option.getVotesCount()));
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

        if(GlobalContainer.getPolls().get(poll_id).checkIfVoted()==1){
            viewHolder.voteBtn.setVisibility(View.GONE);
        } else {

            viewHolder.voteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do something
                    /*
                    for (int y = 0; y < viewGroup.getChildCount(); y++) {
                        viewGroup.getChildAt(y).findViewById(R.id.vote_btn).setVisibility(View.GONE);
                    }
                    */
                    String str = ((TextView) viewHolder.optionText).getText().toString();
                    final DatabaseService mPollService = DatabaseService.getInstance();
                    mPollService.sendVote(poll_id, GlobalContainer.getPolls().get(poll_id).getOptions().get(str));
                    TextView alreadyVotedText = ((ConstraintLayout)viewGroup.getParent()).findViewById(R.id.already_voted_string);
                    alreadyVotedText.setVisibility(TextView.VISIBLE);
                    GlobalContainer.getPolls().get(poll_id).setVoted();
                    if(GlobalContainer.getPolls().get(poll_id).getType()==1){
                        GlobalContainer.getPolls().get(poll_id).incrementVotesCount(str);
                    } else {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String mPhoneNumber = auth.getCurrentUser().getPhoneNumber();
                        ((PollNotAnonymous)GlobalContainer.getPolls().get(poll_id)).addVoted(str, mPhoneNumber);
                        //op.getOptions().addVoted(str, mPhoneNumber);
                    }

                    GlobalContainer.getPolls().put(GlobalContainer.getPolls().get(poll_id).getId(), GlobalContainer.getPolls().get(poll_id));
                    PollSQLiteRepository repository = new PollSQLiteRepository(ApplicationContextProvider.getContext());
                    repository.deletePoll(GlobalContainer.getPolls().get(poll_id).getId());
                    repository.add(GlobalContainer.getPolls().get(poll_id));
                    //op.getOptions().clear();
                    //op.getOptions().addAll(GlobalContainer.getPolls().get(poll_id).getOptions().values());

                    notifyDataSetChanged();
                }
            });
        }
        if(GlobalContainer.getPolls().get(poll_id) instanceof  PollNotAnonymous){
            //final Button showBtn = (Button) viewGroup.getChildAt(i).findViewById(R.id.show_vote_participants);
            viewHolder.showBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( viewHolder.votedText.getVisibility() == View.VISIBLE){
                        viewHolder.votedText.setVisibility(View.GONE);
                        viewHolder.showBtn.setText("Show voted");
                    } else{
                        viewHolder.votedText.setVisibility(View.VISIBLE);
                        viewHolder.showBtn.setText("Hide voted");
                    }
                }
            });
        }


        return convertView;
    }
    static class NewOptionViewHolder {

        TextView optionText;
        ProgressBar mProgress;
        TextView votedText;
        Button showBtn;
        Button voteBtn;
        TextView votesNumber;

        public  NewOptionViewHolder(View view, Option_Adapter newOptionAdapter){
            optionText = (TextView) view.
                    findViewById(R.id.option_string);
            mProgress=view.findViewById(R.id.votes_progress_bar);
            votedText = view.findViewById(R.id.list_of_voted);
            showBtn = view.findViewById(R.id.show_vote_participants);
            voteBtn = view.findViewById(R.id.vote_btn);
            votesNumber = view.findViewById(R.id.votes_number);
         //   mProgress.getIndeterminateDrawable().setColorFilter(0x3F51B5, android.graphics.PorterDuff.Mode.MULTIPLY);

        }


    }


}
