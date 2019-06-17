package atoa.roomates.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import atoa.roomates.models.RoomateModel;
import atoa.roomates.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Avi on 4/28/2016.
 */
public class RoomatesRecycleViewAdapter extends RecyclerView.Adapter<RoomatesRecycleViewAdapter.RoomateViewHolder> {

    ArrayList<RoomateModel> list;
    LayoutInflater inflater;
    Context context;


    public RoomatesRecycleViewAdapter(ArrayList<RoomateModel> list, Context context) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public RoomateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.roomate_list_item, parent, false);
        RoomateViewHolder pvh = new RoomateViewHolder(view);
        return pvh;

    }

    @Override
    public void onBindViewHolder(final RoomateViewHolder holder, int position) {
        RoomateModel current = list.get(position);
        holder.name.setText(current.getName() + " " + current.getLastName());

        Picasso.with(context).load(current.getProfilePicture()).rotate(90).into(holder.img);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class RoomateViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        CircleImageView img;


        RoomateViewHolder(View itemView) {
            super(itemView);

            cv = (CardView) itemView.findViewById(R.id.card_view);
            name = (TextView) itemView.findViewById(R.id.title);
            img = (CircleImageView) itemView.findViewById(R.id.profilePicture);

        }

    }

}

