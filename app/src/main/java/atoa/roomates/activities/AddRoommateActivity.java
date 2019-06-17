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
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;
import atoa.roomates.support.VolleyRequestHandler;
import atoa.roomates.models.RoomateModel;


/**
 * class to represent the add roommate screen
 * the class lets the user select contact from his contact list and adds them to the app daat base
 */
public class AddRoommateActivity extends Activity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private static final String URL_LINK = "http://roomates.96.lt/AddRoommats.php";
    private static final String APARTMENT_NUMBER = "apartmentNum";
    private static final String PHONE_NUMBER = "phone";
//    private static final String FALSE = "'false'";

    //DATA
    MyAdapter mAdapter;
    ArrayList<Contact> mContactList = new ArrayList<>();
    int id = 0;
    //UI
    Button mBtnSelect;
    Button mBtnSkip;
    RoomateModel mRoomateModel;
    SearchView mSearchView;
    Typeface mTypeface;
    TextView mTvChooseContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_roomate);
        mRoomateModel = GenericMethods.getInstance(getApplicationContext()).getUserData();

        getAllContacts(this.getContentResolver());
        ListView lv = (ListView) findViewById(R.id.lv);
        mAdapter = new MyAdapter(mContactList);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(this);
        // adding
        mBtnSelect = (Button) findViewById(R.id.addSelectedContacts);
        mBtnSkip = (Button) findViewById(R.id.skip);
        mTvChooseContacts = (TextView) findViewById(R.id.tvChooseContacts);
        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/noot-aj.ttf");
        mTvChooseContacts.setTypeface(mTypeface);
        mBtnSelect.getBackground().setAlpha(40);
        mBtnSkip.getBackground().setAlpha(40);
        mBtnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 0; i < mAdapter.mCheckStates.size(); i++) {
                    if (mAdapter.mCheckStates.valueAt(i)) {
                        Contact contact = (mAdapter.contactsList.get(mAdapter.mCheckStates.keyAt(i)));
                        addRoommate(Integer.toString(mRoomateModel.getApartmentNumber()), contact.getPhoneNumber());
                    }
                }
                Intent intent = new Intent(getApplicationContext(), RoomatesActivity.class);
                startActivity(intent);
            }
        });
        lv.requestFocus();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mAdapter.toggle((int) arg3);
    }


    /**
     * retrieves user`s contact list in order to let him add his roommates
     * @param cr  provides the application access to the content model.
     */
    public void getAllContacts(ContentResolver cr) {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if(phones != null) {
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mContactList.add(new Contact(name, modifyNumberIntoValidNumber(phoneNumber), id));
                id++;
            }
            phones.close();
        }

    }

    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, Filterable {
        private SparseBooleanArray mCheckStates;
        LayoutInflater inflater;
        CheckBox cb;
        ValueFilter filter;
        ArrayList<Contact> contactsList;
        ArrayList<Contact> contactsListCopy;

        MyAdapter(ArrayList<Contact> contactsList) {
            mCheckStates = new SparseBooleanArray(contactsList.size());
            inflater = (LayoutInflater) AddRoommateActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.contactsList = contactsList;
            this.contactsListCopy = this.contactsList;
            getFilter();
        }

        @Override
        public int getCount() {
            return contactsListCopy.size();
        }

        @Override
        public Object getItem(int position) {
            return contactsListCopy.get(position).getId();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (convertView == null)
                vi = inflater.inflate(R.layout.contact_list_item, null);
            TextView tv = (TextView) vi.findViewById(R.id.textView3);

            cb = (CheckBox) vi.findViewById(R.id.checkBox);
            Contact contact = contactsListCopy.get(position);
            tv.setText(contact.getName());
            cb.setTag(contact.getId());
            cb.setChecked(mCheckStates.get(contact.getId(), false));
            cb.setOnCheckedChangeListener(this);

            return vi;
        }

        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
            //return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
            notifyDataSetChanged();
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            mCheckStates.put((Integer) buttonView.getTag(), isChecked);
        }


        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new ValueFilter();
            }
            return filter;

        }


        /**
         * inner class responsible for filtering the contacts list on user search
         */
        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    ArrayList<Contact> temp = new ArrayList<>();
                    for (int i = 0; i < mContactList.size(); i++) {
                        Contact c = mContactList.get(i);
                        if ((c.getName())
                                .contains(constraint.toString())) {
                            temp.add(c);
                        }
                    }
                    results.count = temp.size();
                    results.values = temp;
                } else {
                    results.count = mContactList.size();
                    results.values = mContactList;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactsListCopy = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();

            }
        }


    }




    /**
     * modifies the number into correct string
     * example for phone number which is not correct "+531 52-466-6423"
     * @param phoneNumber the selected contact phone number
     * @return the modified phone number
     */
    public String modifyNumberIntoValidNumber(String phoneNumber) {

        //in case the number starts with "+" - removes it
        if (phoneNumber.startsWith("+")) {
            phoneNumber = phoneNumber.substring(4, phoneNumber.length());
        }

        //in case the number does not starts with "0" - add "0" at position 0
        //when phone number starts with "+" it does not has "0" at the beginning
        if (!phoneNumber.startsWith("0")) {
            phoneNumber = "0" + phoneNumber;
        }

        //in case the number has "-" - removes it
        phoneNumber = phoneNumber.replaceAll("-", "");

        //in case the number has " " - removes it
        phoneNumber = phoneNumber.replaceAll(" ", "");
        return phoneNumber;
    }


    /**
     * add the selected contacts to the 'Users' table in the app data base
     * @param apartmentNumber the user apartment number
     * @param phoneNumber the selected contact phone number
     */
    public void addRoommate(final String apartmentNumber, final String phoneNumber) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LINK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APARTMENT_NUMBER, apartmentNumber);
                params.put(PHONE_NUMBER, phoneNumber);
                return params;
            }
        };
        VolleyRequestHandler.getInstance(getApplicationContext()).addToRquestQueue(stringRequest);
        new atoa.roomates.firebase.SendNotification().sendMessage(mRoomateModel.getName()+" " + getString(R.string.added_new_roommate), mRoomateModel.getApartmentNumber(), mRoomateModel.getPhoneNumber(), getApplicationContext());

    }


    /**
     * inner class for contact representation.
     * the user choose roommates from his contact list
     */
    public static class Contact {
        int id;
        private String phoneNumber;
        private String name;

        public Contact(String name, String phoneNumber, int id) {
            this.phoneNumber = phoneNumber;
            this.name = name;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

    }


}