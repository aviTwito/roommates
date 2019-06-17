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
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;
import atoa.roomates.support.VolleyRequestHandler;
import atoa.roomates.adapters.BillItemRecyclerAdapter;
import atoa.roomates.models.BillModel;
import atoa.roomates.models.BillModelFixed;
import atoa.roomates.models.RoomateModel;
import atoa.roomates.models.billModelNotFixed;


/**
 * class to represents the bill screen
 * shows the apartment current bills and lets the user add new bills or delete existing bills
 */
public class BillsActivity extends AppCompatActivity {

    //DATA
    BillItemRecyclerAdapter mAdapter;
    ArrayList<BillModel> mList = new ArrayList<BillModel>();
    RecyclerView mBillslist;
    Context mContext;
    RoomateModel mRoomateModel;
    final private String DELETE_BILL_URL = "http://roomates.96.lt/DeleteBill.php";

    //UI
    FloatingActionButton mBtnAddNew;
    private ProgressBar mProgressBar;
    TextView mTvNoBills, mToolBarTitle;
    Typeface mTypeface;
    Typeface mFont;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bills);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTvNoBills = (TextView) findViewById(R.id.tvNoBills);
        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/noot-aj.ttf");
        mTvNoBills.setTypeface(mTypeface);

        //changes the tool bar font
        mToolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/noot-aj.ttf");
        mToolBarTitle.setTypeface(mFont);


        mRoomateModel = GenericMethods.getInstance(this).getUserData();

        mBillslist = (RecyclerView) findViewById(R.id.billsList);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        GenericMethods.getInstance(this).setRecyclerSetting(mBillslist);


        mContext = this;
        mProgressBar.setVisibility(View.VISIBLE);

        mBtnAddNew = (FloatingActionButton) findViewById(R.id.fab);
        mBtnAddNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(BillsActivity.this, SelectBillActivity.class);
                BillsActivity.this.startActivity(intent);
            }
        });


        getBillsList(mRoomateModel.getApartmentNumber());
    }

    /**
     * called when trash button on a recycle view item is clicked
     *deletes the selected bill from 'Bills' table in the app data base
     *
     * @param pos the position of the selected item in the recycler view
     */
    public void deleteItemBill(int pos) {
        final int id = mList.get(pos).getId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_BILL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                return params;
            }
        };
        VolleyRequestHandler.getInstance(this).addToRquestQueue(stringRequest);
    }


    /**
     * open edit bill activity. saves the bill data for filling it in the edit bill screen
     *
     * @param pos the position of the selected item in the recycler view
     */
    public void editBill(int pos) {
        BillModel bill = mList.get(pos);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Intent intent = new Intent(BillsActivity.this, EditBillActivity.class);

        //puts all the relevant data to show in edit bill screen
        intent.putExtra("id", bill.getId());
        intent.putExtra("type", bill.getType().toString());
        intent.putExtra("amount", bill.getAmount());
        intent.putExtra("note", bill.getNote());
        if ((bill instanceof BillModelFixed))
            intent.putExtra("dayOfPayment", ((BillModelFixed) bill).getDayOfPayment());
        if ((bill instanceof billModelNotFixed)) {
            intent.putExtra("fromDate", formatter.format(((billModelNotFixed) bill).getFrom()));
            intent.putExtra("toDate", formatter.format(((billModelNotFixed) bill).getTo()));
        }

        startActivity(intent);
    }


    /**
     * fetching the bill list of the current user apartment number from the server
     *
     * @param apartmentNumber the current user apartment number. passed into the php script for fetching the right data
     */
    public void getBillsList(int apartmentNumber) {
        String url = null;
        try {
            url = "http://roomates.96.lt/GetBills.php?apartmentNumber=" + URLEncoder.encode(Integer.toString(apartmentNumber), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray billsArray = response.getJSONArray("result");
                            for (int i = 0; i < billsArray.length(); i++) {
                                JSONObject finalObject = billsArray.getJSONObject(i);//get the cuttent json object which is representaion of roomate object
                                BillModel billModel;
                                String type = finalObject.getString("billName");
                                if (type.equals("WATER") || type.equals("GAS") || type.equals("ELECTRICITY")) {
                                    billModel = new billModelNotFixed(finalObject);
                                } else if (type.equals("OTHER")) {
                                    billModel = new BillModel(finalObject);
                                } else {
                                    billModel = new BillModelFixed(finalObject);
                                }
                                mList.add(billModel);//adds the bill to the list of bills
                            }
                            mAdapter = new BillItemRecyclerAdapter(mList, mContext);
                            mBillslist.setAdapter(mAdapter);
                            if (mList.isEmpty()) {
                                mTvNoBills.setVisibility(View.VISIBLE);
                            }
                            mProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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





