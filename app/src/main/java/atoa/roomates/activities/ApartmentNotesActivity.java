package atoa.roomates.activities;
/**
 * Copyright 2016 Avi twito,Or Am-Amshalem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;
import atoa.roomates.adapters.NotesRecycleViewAdapter;
import atoa.roomates.models.NotesModel;
import atoa.roomates.models.RoomateModel;


/**
 * class to represents to notes screen
 * lets the user add notes,delete notes and view other users notes
 */
public class ApartmentNotesActivity extends Fragment implements View.OnClickListener{
    //UI
    private ImageButton mBtnSendMessage;
    private EditText mEtUserMessage;

    //data
    View view;
    RoomateModel mRoomateModel;
    SharedPreferences mPreferences;
    Firebase mFirebase;
    RecyclerView mListMessages;
    public ProgressBar mProgressBar;
    FirebaseRecyclerAdapter<NotesModel, NotesRecycleViewAdapter.notesRecycle> mAdapter;
    private String mLink;
    Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.apartment_notes, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        view = getView();
        Firebase.setAndroidContext(getContext());
        mProgressBar = (ProgressBar)view.findViewById(R.id.progressIndicator);

        //get the app shared preferences
        mPreferences = getContext().getSharedPreferences("appData", 0);

        mRoomateModel = GenericMethods.getInstance(mContext).getUserData();

        //creates reference link with the apartment number to the 'fire base' data base
        try {
            mLink =  "https://roomates-1330.firebaseio.com/" + URLEncoder.encode(Integer.toString(mRoomateModel.getApartmentNumber()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mFirebase = new Firebase(mLink);
        mEtUserMessage = (EditText) view.findViewById(R.id.message_input);
        mBtnSendMessage = (ImageButton) view.findViewById(R.id.send_button);
        mListMessages = (RecyclerView) view.findViewById(R.id.messageList);

        GenericMethods.getInstance(mContext).setRecyclerSetting(mListMessages);
        new NotesRecycleViewAdapter(mContext,ApartmentNotesActivity.this);
        mBtnSendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mEtUserMessage.getText().toString().matches("")) {
            Toast.makeText(getContext(), R.string.error_empty_message, Toast.LENGTH_SHORT).show();
        } else {
            String photoPath = mPreferences.getString(RegistraionActivity.USER_PROFILE_PICTURE, "");
            Bitmap bitmap = null;
            Bitmap bmRotated = null;
            String encodedImage;
            try {
                bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                ExifInterface exif = new ExifInterface(photoPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                bmRotated = GenericMethods.getInstance(mContext).rotateBitmap(bitmap, orientation);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bmRotated != null) {
                encodedImage = GenericMethods.getInstance(mContext).encodeImage(bmRotated);
            }
            else{
                encodedImage = ImageBase64.encode(BitmapFactory.decodeResource(getResources(), R.drawable.defult_icon));
            }
            //get current date and time
            String date = (DateFormat.format("dd-MM-yyyy hh:mm", new java.util.Date()).toString());
            NotesModel note = new NotesModel(encodedImage , mEtUserMessage.getText().toString(), date);
            //inserts to 'firebase' data base the message content
            mFirebase.push().setValue(note);
            mEtUserMessage.setText("");
            mEtUserMessage.clearFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            mEtUserMessage.clearFocus();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new FirebaseRecyclerAdapter<NotesModel, NotesRecycleViewAdapter.notesRecycle>(
                NotesModel.class,
                R.layout.notes_item,
                NotesRecycleViewAdapter.notesRecycle.class,
                mFirebase
        ) {
            @Override
            protected void populateViewHolder(NotesRecycleViewAdapter.notesRecycle notesRecycle, NotesModel notesModel, int i) {
                notesRecycle.getUserMessage().setText(notesModel.getUserMessage());
                notesRecycle.getDateMessage().setText(notesModel.getMessageDate());
                Bitmap bitmap = GenericMethods.getInstance(mContext).decodeImage(notesModel.getEncodedImage());
                notesRecycle.getUserImage().setImageBitmap(bitmap);
            }
        };
        mListMessages.setAdapter(mAdapter);

        mFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(View.GONE);
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }


    /**
     * called when the user is pressing the note delete button
     * @param pos position of the selected note item
     */
    public void deleteNote(int pos){
        Firebase itemRef = mAdapter.getRef(pos);
        itemRef.removeValue();
    }
}