package atoa.roomates.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import atoa.roomates.models.ShopListItemModel;
import atoa.roomates.R;
import atoa.roomates.activities.ShopCartActivity;
//import atoa.roomates.Screens.ShopCartScreen;

/**
 * Created by Avi on 5/7/2016.
 */
public class ShopItemRecyclerViewAdapter extends RecyclerView.Adapter<ShopItemRecyclerViewAdapter.ListItemViewHolder> {

    static ArrayList<ShopListItemModel> list;
    LayoutInflater inflater;
    static Context context;


    public ShopItemRecyclerViewAdapter(ArrayList<ShopListItemModel> list, Context context) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }


    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.shop_list_item, parent, false);
        ListItemViewHolder vh = new ListItemViewHolder(view);
        return vh;

    }

    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        ShopListItemModel current = list.get(position);
        holder.name.setText(current.getName());
    }

    public int getItemCount() {
        return list.size();

    }

    public void delete(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(ShopListItemModel model) {
        list.add(model);
        notifyItemInserted(list.size());

    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageButton btnDelete;

        ListItemViewHolder(final View itemView) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.name);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDeleteItem);
            btnDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ((ShopCartActivity) context).deleteItem(getAdapterPosition());
            delete(getAdapterPosition());
        }


    }
}
