package atoa.roomates.activities;
/**
 * Copyright 2016 Avi twito,Or Am-Amshalem
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;
import atoa.roomates.support.RecyclerItemClickListener;
import atoa.roomates.support.VolleyRequestHandler;
import atoa.roomates.adapters.RoomatesRecycleViewAdapter;
import atoa.roomates.models.RoomateModel;

/**
 * class which represents the roommates screen of the app
 * the user can see the list of roommates of the apartment, and add/delete roommates
 */
public class RoomatesActivity extends AppCompatActivity {

    private static final String DELETE_ROOMMATE_URL = "http://roomates.96.lt/DeleteRoomate.php";
    private final String ROOMMATES_LIST_URL = "http://roomates.96.lt/GetRoomates.php?apartmentNum=";

    RecyclerView mRoomatesList;
    ArrayList<RoomateModel> mList = new ArrayList<>();
    RoomatesRecycleViewAdapter mAdapter;
    RoomateModel mRoomateModel;
    public ProgressBar mProgressBar;
    Context mContext;
    TextView mToolBarTitle;
    Typeface mFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomates_list);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException e) {

        }
        mProgressBar = (ProgressBar) findViewById(R.id.progressIndicator);
        mToolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/noot-aj.ttf");
        mToolBarTitle.setTypeface(mFont);

        mRoomatesList = (RecyclerView) findViewById(R.id.roomatesList);
        mRoomateModel = GenericMethods.getInstance(mContext).getUserData();


        String roommatesListLink = ROOMMATES_LIST_URL + mRoomateModel.getApartmentNumber();

        getRoomatesList(roommatesListLink);

        GenericMethods.getInstance(this).setRecyclerSetting(mRoomatesList);

        mRoomatesList.addOnItemTouchListener(new RecyclerItemClickListener(RoomatesActivity.this, mRoomatesList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
                RoomateModel tmp = mList.get(position);
                String phoneNum = tmp.getPhoneNumber();
                deleteRoomate(view, phoneNum, position);

            }
        }));

        mRoomatesList.setClickable(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                // overridePendingTransition(R.animator.anim_left, R.animator.anim_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * deletes the selected roommate from the Users table in the data base
     *
     * @param view        the selected recycler view item
     * @param phoneNumber the selected user phone number
     */
    public void deleteRoomate(View view, final String phoneNumber, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.remove_roomate));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_ROOMMATE_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("PhoneNumber", phoneNumber);
                        return params;
                    }
                };
                VolleyRequestHandler.getInstance(mContext).addToRquestQueue(stringRequest);

                mList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * called when the add roommate button is pressed. starts 'AddRoomatesScreen' activity
     *
     * @param view is the add roommate button
     */
    public void addRoomate(View view) {
        Intent intent = new Intent(this, AddRoommateActivity.class);
        startActivity(intent);
    }


    /**
     * called when the activity is created, fetching the apartment roommates list from the apartment
     *
     * @param link to run a php script which returns Json Array where each object holds roommate data
     */
    public void getRoomatesList(String link) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, link, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray roomatesArray = response.getJSONArray("result");
                            for (int i = 0; i < roomatesArray.length(); i++) {
                                JSONObject roommateObject = roomatesArray.getJSONObject(i);

                                RoomateModel temp = new RoomateModel(roommateObject);
                                temp.setPhoneNumber(roommateObject.getString("phone"));

                                mList.add(temp);
                            }
                            mAdapter = new RoomatesRecycleViewAdapter(mList, mContext);
                            mRoomatesList.setAdapter(mAdapter);
                            mProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleyRequestHandler.getInstance(mContext).addToRquestQueue(jsonObjectRequest);
    }
}


