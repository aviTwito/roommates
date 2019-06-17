package atoa.roomates.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import atoa.roomates.R;
import atoa.roomates.models.QuestionAndAnswer;

/**
 * Created by Avi on 8/21/2016.
 */
public class QuestionAndAnswersAdapter extends RecyclerView.Adapter<QuestionAndAnswersAdapter.QuestionViewHolder> implements View.OnClickListener {
    private int expandedPosition = -1;
    ArrayList<QuestionAndAnswer> list;
    LayoutInflater inflater;
    Context context;


    public QuestionAndAnswersAdapter(ArrayList<QuestionAndAnswer> list, Context context) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.question, parent, false);
        QuestionViewHolder pvh = new QuestionViewHolder(view);
        pvh.itemView.setOnClickListener(QuestionAndAnswersAdapter.this);
        pvh.itemView.setTag(pvh);
        return pvh;

    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        QuestionAndAnswer current = list.get(position);
        holder.question.setText(current.getQuestion());
        holder.answer.setText(current.getAnswer());

        if (position == expandedPosition) {
            holder.layout.setVisibility(View.VISIBLE);
        } else {
            holder.layout.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        QuestionViewHolder holder = (QuestionViewHolder) view.getTag();


        // Check for an expanded view, collapse if you find one
        if (expandedPosition >= 0) {
            int prev = expandedPosition;
            notifyItemChanged(prev);
        }
        // Set the current position to "expanded"
        expandedPosition = holder.getAdapterPosition();
        notifyItemChanged(expandedPosition);


    }


    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView question, answer;
        LinearLayout layout;

        QuestionViewHolder(View itemView) {
            super(itemView);

            question = (TextView) itemView.findViewById(R.id.question);
            answer = (TextView) itemView.findViewById(R.id.answer);
            layout = (LinearLayout)itemView.findViewById(R.id.llExpandArea);

        }

    }
}
