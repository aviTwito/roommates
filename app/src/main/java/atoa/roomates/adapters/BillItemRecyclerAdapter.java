package atoa.roomates.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import atoa.roomates.models.BillModel;
import atoa.roomates.models.BillModelFixed;
import atoa.roomates.models.billModelNotFixed;
import atoa.roomates.R;
import atoa.roomates.activities.BillsActivity;

/**
 * class which responsible for binding list of bill the the bills recycler view 
 */
public class BillItemRecyclerAdapter extends RecyclerView.Adapter<BillItemRecyclerAdapter.ListItemViewHolder>{

    static ArrayList<BillModel> list;
    LayoutInflater inflater;
    static Context context;

    public BillItemRecyclerAdapter(ArrayList<BillModel> list, Context context){
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bill_item, parent , false);
        ListItemViewHolder vh = new ListItemViewHolder(view);
        return vh;    }

    @Override
    public void onBindViewHolder(BillItemRecyclerAdapter.ListItemViewHolder holder, int position) {
        BillModel billModel = list.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        switch(billModel.getType()){
            case WATER:
                holder.ivBillType.setImageResource(R.drawable.water);
                holder.billName.setText(R.string.water);
                break;
            case INTERNET:
                holder.ivBillType.setImageResource(R.drawable.computer);
                holder.billName.setText(R.string.internet);
                break;
            case ELECTRICITY:
                holder.ivBillType.setImageResource(R.drawable.electrical);
                holder.billName.setText(R.string.electricity);
                break;
            case TV:
                holder.ivBillType.setImageResource(R.drawable.hdtv);
                holder.billName.setText(R.string.television);
                break;
            case ARNONA:
                holder.ivBillType.setImageResource(R.drawable.bill);
                holder.billName.setText(R.string.arnona);
                break;
            case GAS:
                holder.ivBillType.setImageResource(R.drawable.gas);
                holder.billName.setText(R.string.gas);
                break;
            case OTHER:
                holder.ivBillType.setImageResource(R.drawable.notepad);
                holder.billName.setText(R.string.other);
                break;
        }

        holder.amount.setText(Double.toString(billModel.getAmount()) +" "+context.getString(R.string.NIS));

        if(!billModel.getNote().matches("")){
            holder.note.setVisibility(View.VISIBLE);
            holder.note.setText(billModel.getNote());
        }


        if (billModel instanceof billModelNotFixed) {
            holder.fromDate.setVisibility(View.VISIBLE);
            holder.toData.setVisibility(View.VISIBLE);
            holder.fromDate.setText(formatter.format(((billModelNotFixed) billModel).getFrom()));
            holder.toData.setText(formatter.format(((billModelNotFixed) billModel).getTo()));

        }
        else if (billModel instanceof BillModelFixed){
            holder.time.setVisibility(View.VISIBLE);
            holder.dayOfPayment.setVisibility(View.VISIBLE);
            holder.time.setText(R.string.pay_day);
            holder.dayOfPayment.setText(Integer.toString(((BillModelFixed) billModel).getDayOfPayment()) + " " + context.getString(R.string.a_month));
        }
        else{
            holder.fromDate.setVisibility(View.GONE);
            holder.toData.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.dayOfPayment.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void deleteBill(int pos){
        list.remove(pos);
        notifyItemRemoved(pos);

    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView card;
        ImageButton btDelete;
        ImageButton btnEdit;
        TextView toData;
        TextView fromDate;
        TextView amount;
        ImageView ivBillType;
        TextView billName;
        TextView dayOfPayment;
        TextView time;
        TextView note;

        public ListItemViewHolder(View itemView) {
            super(itemView);

            card = (CardView)itemView.findViewById(R.id.cvBillItem);
            btDelete = (ImageButton)itemView.findViewById(R.id.btnDeleteItem);
            btnEdit = (ImageButton)itemView.findViewById(R.id.btnEditItem);
            toData = (TextView)itemView.findViewById(R.id.tvToDate);
            fromDate = (TextView)itemView.findViewById(R.id.tvFromDate);
            amount = (TextView)itemView.findViewById(R.id.tvAmount);
            ivBillType = (ImageView)itemView.findViewById(R.id.imgBillType);
            billName = (TextView)itemView.findViewById(R.id.billName);
            dayOfPayment = (TextView)itemView.findViewById(R.id.dayOfPayment);
            note = (TextView)itemView.findViewById(R.id.note);
            time = (TextView)itemView.findViewById(R.id.tvTime);
            btDelete.setOnClickListener(this);
            btnEdit.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()){
                case R.id.btnDeleteItem:
                    ((BillsActivity)context).deleteItemBill(position);
                    deleteBill(position);
                    break;
                case R.id.btnEditItem://Toast.makeText((BillScreen)context , "check" , Toast.LENGTH_SHORT).show();
                            ((BillsActivity) context).editBill(position);
                    break;
            }


        }
    }
}
