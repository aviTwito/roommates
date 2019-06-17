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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import atoa.roomates.adapters.ShopItemRecyclerViewAdapter;
import atoa.roomates.models.RoomateModel;
import atoa.roomates.models.ShopListItemModel;
import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;
import atoa.roomates.support.VolleyRequestHandler;
import atoa.roomates.support.SimpleDividerItemDecoration;

/**
 * activity which the user can see the apartment shopping list and add\delete products to the list
 * the data is retried from remote data base by a php script on the
 */
public class ShopCartActivity extends AppCompatActivity {
    private static final String DELETE_ITEM_URL = "http://roomates.96.lt/DeleteCartItem.php";
    private static final String ADD_ITEM_URL = "http://roomates.96.lt/AddNewItem.php";
    private static final String ITEM_NAME = "name";
    private static final String APARTMENT_NUMBER = "apartmentNumber";

    RoomateModel mRoomateModel;
    RecyclerView mCartItems;
    ArrayList<ShopListItemModel> mLlist = new ArrayList<ShopListItemModel>(); //list to suplly the carts items to the recycler view
    ShopItemRecyclerViewAdapter mAdapter;

    Context mContext;
    Typeface mFont;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_carts_list);
        mContext = this;
        mRoomateModel = GenericMethods.getInstance(mContext).getUserData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/noot-aj.ttf");
        TextView toolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolBarTitle.setTypeface(mFont);

        mCartItems = (RecyclerView) findViewById(R.id.newListItem);

        GenericMethods.getInstance(this).setRecyclerSetting(mCartItems);
        mCartItems.addItemDecoration(new SimpleDividerItemDecoration(this));
        getShopingList();
    }

    /**
     * called when the activity is created
     * inflating menu xml file to the tool bar
     *
     * @param menu xml file to represent the items on the tool bar
     * @return true if thr menu was inflated
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return true;
    }


    /**
     * called when the + button is pressed
     * adds item to the data base and to the adapter
     *
     * @param view  "+" image button
     */
    public void addItem(View view) {
        ShopListItemModel item;
        final EditText etName = (EditText) findViewById(R.id.etEddItem);

        final String itemName = etName.getText().toString(); //initialize itemName with the input of the user

        //checks if the user input only contains hebrew letters
        if (itemName.matches(getString(R.string.chars_range))) {
            item = new ShopListItemModel(itemName); //creates shop cart item model
            //adds the item to the adapter for immediate response to the user
            mAdapter.addItem(item);
            //adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            mAdapter.notifyDataSetChanged();
            //add the item to the table through the network volley operation
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_ITEM_URL, new Response.Listener<String>() {
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
                    params.put(ITEM_NAME, itemName);
                    params.put(APARTMENT_NUMBER, Integer.toString(mRoomateModel.getApartmentNumber()));
                    return params;
                }
            };
            VolleyRequestHandler.getInstance(mContext).addToRquestQueue(stringRequest);

            //set the product name field to blank after item has been added
            etName.setText("");
        } else {
            Toast.makeText(this, R.string.errer_product_name_only_charactes, Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * called when the delete button on item view is pressed
     * delete the selected item from the data base
     *
     * @param position the position of the selected item from the recycler view
     */
    public void deleteItem(int position) {
        ShopListItemModel tmp = mLlist.get(position);
        final String itemName = tmp.getName(); //gets the item name to remove
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_ITEM_URL, new Response.Listener<String>() {
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
                params.put("apartmentNumber", Integer.toString(mRoomateModel.getApartmentNumber()));
                params.put("itemName", itemName);
                return params;
            }
        };
        VolleyRequestHandler.getInstance(mContext).addToRquestQueue(stringRequest);
    }


    /**
     * fetching the data from the server into a recycle view
     */
    public void getShopingList() {
        String url = null;
        try {
            url = "http://roomates.96.lt/GetListItems.php?apartmentNumber=" + URLEncoder.encode(Integer.toString(mRoomateModel.getApartmentNumber()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray shopList = response.getJSONArray("result");
                            for (int i = 0; i < shopList.length(); i++) {
                                JSONObject listItem = shopList.getJSONObject(i);
                                String name = listItem.getString("name");

                                ShopListItemModel item = new ShopListItemModel(name);
                                mLlist.add(item);
                            }
                            mAdapter = new ShopItemRecyclerViewAdapter(mLlist, mContext);
                            mCartItems.setAdapter(mAdapter);
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
