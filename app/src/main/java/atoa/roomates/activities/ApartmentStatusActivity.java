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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import atoa.roomates.models.RoomateModel;
import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;
import atoa.roomates.support.VolleyRequestHandler;

/**
 * class to represent the apartment status screen
 * shows the user the apartment bills status and a pie chart which shows the amount payed by each user
 */
public class ApartmentStatusActivity extends Fragment {

    /**
     * holds link to a php script on the server side for fetching data into the pie chart.
     * apartment number is added when the fragment is created with the current user apartment number
     */
    final private String PIE_CHART_DATA_URL = "http://roomates.96.lt/GetUserAmoutPayed.php?apartmentNumber=";

    /**
     * holds link to a php script on the server side for fetching data into the bills status text view
     * apartment number is added when the fragment is created with the current user apartment number
     */
    final private String APARTMENT_STATUS_URL ="http://roomates.96.lt/GetApartmentStatus.php?apartmentNumber=";

    PieChart mPieChartData;
    public ProgressBar mProgressBar;
    RoomateModel mRoomateModel;
    TextView mBillState, mShopingCartState;
    Context mContext;
    int[] col = new int[]{-230845,-9435976,-3652577,-15517183,-13455068,-4937904,-16662350,-3293676};


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.apartment_bill_status, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();
        View v = getView();

        mBillState = (TextView) v.findViewById(R.id.bills_state);
        mProgressBar = (ProgressBar)v.findViewById(R.id.progressIndicator);
        mShopingCartState = (TextView) v.findViewById(R.id.shoping_cart_state);


        mRoomateModel = GenericMethods.getInstance(mContext).getUserData();

        String apartmetStatusLink = APARTMENT_STATUS_URL+ mRoomateModel.getApartmentNumber();
        getApartmentStatus(apartmetStatusLink);

        mPieChartData = (PieChart) v.findViewById(R.id.chart);

        String pieChartDataLink = PIE_CHART_DATA_URL + mRoomateModel.getApartmentNumber();
        setPieChartData(pieChartDataLink);
        setPieChartPrefrences();


        Legend l = mPieChartData.getLegend();
        l.setEnabled(false);

    }


    /**
     * setting the pie chart visual preferences
     */
    public void setPieChartPrefrences() {
       // mPieChartData.setUsePercentValues(true);
        mPieChartData.setHoleColor(Color.rgb(235, 235, 235));
        mPieChartData.setDescription("");
        mPieChartData.setDrawCenterText(true);
        mPieChartData.setRotationAngle(0);
        mPieChartData.setRotationEnabled(true);
        mPieChartData.setCenterText(mContext.getString(R.string.pie_chart_center_text));
        mPieChartData.setCenterTextSize(25);
        mPieChartData.setCenterTextColor(Color.parseColor("#02438b"));
        mPieChartData.animateXY(1000, 1000);
        mPieChartData.setCenterTextSize(10f);
    }


    /**
     * called when the fragment is created, fetching data to the pie chart
     * @param link to run a php script which return Json array list where each object holds a roommate name and amount payed
     */
    public void setPieChartData(String link) {
        final int[] pos = {0};
        final ArrayList<Entry> yVals1 = new ArrayList<>();
        final ArrayList<String> xVals = new ArrayList<>();
        final ArrayList<Integer> colors = new ArrayList<>();
        final Random rand = new Random();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, link, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray roommatesArray = response.getJSONArray("result");
                    for (int i = 0; i < roommatesArray.length(); i++) {
                        JSONObject temp = roommatesArray.getJSONObject(i);
                        if(!temp.getString("firstName").matches("null")) {
                            yVals1.add(new Entry(((float) temp.getDouble("amountPayed")), pos[0]));
                            xVals.add(temp.getString("firstName"));
                        }
                    }
                    PieDataSet dataSet = new PieDataSet(yVals1, "");
                    dataSet.setSliceSpace(3f);
                    dataSet.setColors(col);
                    PieData data = new PieData(xVals, dataSet);
                    data.setValueTextSize(11f);
                    data.setValueTextColor(Color.parseColor("#FFFFFF"));
                    mPieChartData.setData(data);

                    for (Entry e: yVals1){
                        if (e.getVal()!=0){
                            mPieChartData.setVisibility(View.VISIBLE);
                        }
                    }

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




    /**
     * called when the fragment is created, fetching data to the bills state text view
     * @param link to run a php script which return Json object which holds the apartment bills data
     */
    public void getApartmentStatus(String link) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, link, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String billStatus = "";
                        String cartStatus = "";
                        try {
                            //returns the number of bills of the apartment
                            int BillsCount = response.getInt("billCount");

                            //returns the total bills sum
                            double totalBillSum = response.getDouble("billSum");

                            //returns number of roommates
                            int numberOfRoommates = response.getInt("RoomatesCount");

                            //calculates the amount each roommate needs to pay
                            double amountToPayPerRoommate = totalBillSum / numberOfRoommates;

                            //returns the total amount payed untill now
                            double totalAmountPayed = response.getDouble("amountPayed");

                            //calculates the amount left to pay
                            double amountToPay = totalBillSum - totalAmountPayed;

                            String itemsCount = Integer.toString(response.getInt("itemsCount"));


                            billStatus += mContext.getString(R.string.apartment_has) + " " + BillsCount + " " + mContext.getString(R.string.bills);
                            billStatus += "\n";
                            billStatus += mContext.getString(R.string.bills_amount) + " " + totalBillSum;
                            billStatus += "\n";
                            billStatus += mContext.getString(R.string.payed_until_now) + " " + totalAmountPayed + " " + mContext.getString(R.string.NIS);
                            billStatus += "\n";
                            billStatus += mContext.getString(R.string.left_to_pay) + " " + amountToPay + " " + mContext.getString(R.string.NIS);
                            billStatus += "\n";
                            billStatus += mContext.getString(R.string.total_sum_per_roommate) + " " + amountToPayPerRoommate + " " + mContext.getString(R.string.NIS);
                            mBillState.setText(billStatus);

                            cartStatus = mContext.getString(R.string.apartment_cars_has) + " " + itemsCount + " " + mContext.getString(R.string.products);
                            mShopingCartState.setText(cartStatus);
                            mProgressBar.setVisibility(View.GONE);
                            mBillState.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mBillState.setVisibility(View.GONE);
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
