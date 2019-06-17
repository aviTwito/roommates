package atoa.roomates.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import atoa.roomates.activities.ApartmentNotesActivity;
import atoa.roomates.models.NotesModel;
import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;


public class NotesRecycleViewAdapter extends RecyclerView.Adapter<NotesRecycleViewAdapter.notesRecycle> {

    ArrayList<NotesModel> list;
    LayoutInflater inflater;
    static Context context;
    static ApartmentNotesActivity fragment;

    public NotesRecycleViewAdapter(Context context, ApartmentNotesActivity fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public NotesRecycleViewAdapter.notesRecycle onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notes_item, parent, false);
        notesRecycle pvh = new notesRecycle(view);
        return pvh;
    }

    @Override
    public void onBindViewHolder(notesRecycle holder, int position) {
        NotesModel current = list.get(position);
//        byte[] b = Base64.decode(current.getEncodedImage(), Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

        Bitmap bitmap = GenericMethods.getInstance(context).decodeImage(current.getEncodedImage());

        holder.userMessage.setText(current.getUserMessage());
        holder.userPicture.setImageBitmap(bitmap);
        //holder.userPicture.setRotation(180);
        holder.dateMessage.setText(current.getMessageDate().toString());

    }

    public NotesRecycleViewAdapter(ArrayList<NotesModel> list, LayoutInflater inflater) {
        this.list = list;
        this.inflater = inflater;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class notesRecycle extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView userMessage;
        private ImageView userPicture;
        private TextView dateMessage;
        private ImageButton deleteButton;

        public TextView getUserMessage() {
            return userMessage;
        }

        public ImageView getUserImage() {
            return userPicture;
        }

        public TextView getDateMessage() {
            return dateMessage;
        }

        public notesRecycle(View itemView) {
            super(itemView);

            userMessage = (TextView) itemView.findViewById(R.id.userMessage);
            userPicture = (ImageView) itemView.findViewById(R.id.userPicture);
            dateMessage = (TextView) itemView.findViewById(R.id.dateMessage);
            deleteButton = (ImageButton)itemView.findViewById(R.id.btnDeleteNote);
            deleteButton.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            fragment.deleteNote(getAdapterPosition());
        }
    }
}
