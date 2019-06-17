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

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import atoa.roomates.models.BillModel;
import atoa.roomates.models.BillModelFixed;
import atoa.roomates.models.RoomateModel;
import atoa.roomates.models.billModelNotFixed;
import atoa.roomates.network.InsertNewBill;
import atoa.roomates.R;
import atoa.roomates.support.BillType;
import atoa.roomates.support.GenericMethods;


/**
 *  class to represents the add bill screen
 *  resposbile for adding new bill to the app data base
 */
public class AddBillActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    //UI
    private EditText mFromDateEtxt, mToDateEtxt, mTotalAmout, mAmoutPayed, mBillNote;
    String msSelectedDay;
    private Button mBtnAmountPayed, mTopImage;
    private Spinner mDaysOfMonth;
    private CheckBox mCbNotPayed;
    LinearLayout mAmountPayedLayout;


    //DATA
    private Bundle mBundle;
    RoomateModel mRoomateModel;

    //used for letting the user pick dates from calendar
    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mToDatePickerDialog;

    //represent the bill type in order to set the layout accordingly
    private BillType mType;

    private SimpleDateFormat mDateFormatter;

    private Calendar mCalander;
    TextView mToolBarTitle;
    Typeface mFont;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mRoomateModel = GenericMethods.getInstance(this).getUserData();

        mDateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        //gets the id of the selected button in previous activity
        mBundle = getIntent().getExtras();
        int id = mBundle.getInt("id");
        setContent(id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/noot-aj.ttf");
        mToolBarTitle.setTypeface(mFont);

        mTopImage = (Button) findViewById(R.id.btnInternet);
        mTotalAmout = (EditText) findViewById(R.id.totalAmount);
        mBtnAmountPayed = (Button) findViewById(R.id.btnAmountPayed);
        mAmoutPayed = (EditText) findViewById(R.id.amoutPayed);
        mBillNote = (EditText) (findViewById(R.id.note));
        mAmountPayedLayout = (LinearLayout) findViewById(R.id.amountPayedLayout);
        mCbNotPayed = (CheckBox) findViewById(R.id.cbNotPayed);


        mCbNotPayed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCbNotPayed.isChecked()) {
                    mAmountPayedLayout.setVisibility(View.GONE);
                } else {
                    mAmountPayedLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        setButtonImage(id);
        mBtnAmountPayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAmoutPayed.setText(mBtnAmountPayed.getText().toString());
            }
        });
        mTotalAmout.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //set the text on the amount payed button the the same text as the bill amout text
                //for user convveinice in case he payed all the bill amount
                mBtnAmountPayed.setText(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return true;
    }

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
     * called if the user chose one of the following bill types:Electricity\WATER\GAS
     * initializing 2 Date picker dialogs to let the user choose the from date and the to date of the bill
     */
    private void setDateTimeField() {
        mFromDateEtxt.setOnClickListener(this);
        mToDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        mFromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalander = Calendar.getInstance();
                mCalander.set(year, monthOfYear, dayOfMonth);
                mFromDateEtxt.setText(mDateFormatter.format(mCalander.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mToDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalander = Calendar.getInstance();
                mCalander.set(year, monthOfYear, dayOfMonth);
                mToDateEtxt.setText(mDateFormatter.format(mCalander.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onClick(View view) {
        if (view == mFromDateEtxt) {
            mFromDatePickerDialog.show();
        } else if (view == mToDateEtxt) {
            mToDatePickerDialog.show();
        }
    }


    /**
     * called when the activity is created
     * sets top layout image in the layour according to the selected bill in previous screen
     * @param id the id of the button the user pressed in previous activity
     */
    public void setButtonImage(int id) {
        Drawable top;
        switch (id) {
            case R.id.btnArnona:
                top = ContextCompat.getDrawable(this, R.drawable.bill_converted);
                mTopImage.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                mTopImage.setText(R.string.arnona);
                mType = BillType.ARNONA;
                break;
            case R.id.btnOther:
                top = ContextCompat.getDrawable(this, R.drawable.notepad_converted);
                mTopImage.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                mTopImage.setText(R.string.other);
                mType = BillType.OTHER;
                break;
            case R.id.btnGas:
                top = ContextCompat.getDrawable(this, R.drawable.gas_converted);
                mTopImage.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                mTopImage.setText(R.string.gas);
                mType = BillType.GAS;
                break;
            case R.id.btnInternet:
                top = ContextCompat.getDrawable(this, R.drawable.computer_converted);
                mTopImage.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                mTopImage.setText(R.string.internet);
                mType = BillType.INTERNET;
                break;
            case R.id.btnTv:
                top = ContextCompat.getDrawable(this, R.drawable.hdtv_converted);
                mTopImage.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                mTopImage.setText(R.string.television);
                mType = BillType.TV;
                break;
            case R.id.btnWater:
                top = ContextCompat.getDrawable(this, R.drawable.water_converted);
                mTopImage.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                mTopImage.setText(R.string.water);
                mType = BillType.WATER;
                break;
            case R.id.btnElctricity:
                top = ContextCompat.getDrawable(this, R.drawable.electrical_converted);
                mTopImage.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
                mTopImage.setText(R.string.electricity);
                mType = BillType.ELECTRICITY;
                break;
        }
    }


    /**
     * called when user click on add new bill button
     * creating new bill with the user input data and passing it to insertNewBill() class which responsible for the network operation
     * @param view is the add new bill button
     */
    public void btnAddNewBill_OnClick(View view) {
        BillModel bill;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fdate, tDate;
        double amountPayed = 0;
        //check if user input is vallid
        if (userInputIsValid()) {
            //checks if the user entered amount payed.
            //if he did it will be initialized with the user input
            //else it will be 0
            if (!mAmoutPayed.getText().toString().matches("")) {
                amountPayed = Double.parseDouble(mAmoutPayed.getText().toString()); //initialize 'amount payed' with the user input
            }
            if (mType == BillType.WATER || mType == BillType.GAS || mType == BillType.ELECTRICITY) {
                try {
                    fdate = formatter.parse(mFromDateEtxt.getText().toString());
                    //((billModelNotFixed) bill).setFrom(date);
                    tDate = formatter.parse(mToDateEtxt.getText().toString());
                    //((billModelNotFixed) bill).setTo(date);
                    bill = new billModelNotFixed(mType, Double.parseDouble(mTotalAmout.getText().toString()), mBillNote.getText().toString(), fdate, tDate);

                    new InsertNewBill(this, bill, mRoomateModel.getApartmentNumber(), amountPayed, mRoomateModel.getPhoneNumber()).execute();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (mType == BillType.ARNONA || mType == BillType.INTERNET || mType == BillType.TV) {
                bill = new BillModelFixed(mType, Double.parseDouble(mTotalAmout.getText().toString()), mBillNote.getText().toString(), Integer.parseInt(msSelectedDay));
                new InsertNewBill(this, bill, mRoomateModel.getApartmentNumber(), amountPayed, mRoomateModel.getPhoneNumber()).execute();
            } else {
                bill = new BillModel(mType, Double.parseDouble(mTotalAmout.getText().toString()), mBillNote.getText().toString());
                new InsertNewBill(this, bill, mRoomateModel.getApartmentNumber(), amountPayed, mRoomateModel.getPhoneNumber()).execute();
            }

            Intent intent = new Intent(this, BillsActivity.class);
            this.startActivity(intent);
            new atoa.roomates.firebase.SendNotification().sendMessage(mRoomateModel.getName()+ " "+ getString(R.string.bill_added), mRoomateModel.getApartmentNumber(), mRoomateModel.getPhoneNumber(), getApplicationContext());

        }
    }


    /**
     * setting the layout for the activity according to selected bill in previous activity
     * @param id the image button id which is determined by the bill type
     */
    private void setContent(int id) {
        if (id == R.id.btnWater || id == R.id.btnElctricity || id == R.id.btnGas) {
            setContentView(R.layout.new_bill_not_fixed);
            mFromDateEtxt = (EditText) findViewById(R.id.etxt_fromdate);
            mFromDateEtxt.setInputType(InputType.TYPE_NULL);

            mToDateEtxt = (EditText) findViewById(R.id.etxt_todate);
            mToDateEtxt.setInputType(InputType.TYPE_NULL);
            setDateTimeField();
        } else if (id == R.id.btnOther) {
            setContentView(R.layout.new_bill_other);
        } else {
            setContentView(R.layout.new_bill_fixed);
            mDaysOfMonth = (Spinner) findViewById(R.id.daysOfMonth);
            mDaysOfMonth.setOnItemSelectedListener(this);
        }
    }


    /**
     * called when add new bill button is clicked
     * checks if the user input is valid before inserting new bill to data base
     * @return true if the user input is valid and false otherwise
     */
    public boolean userInputIsValid() {

        double totalAmount;
        double amountPayed;
        //checks if user entered bill total amount
        if (mTotalAmout.getText().toString().matches("")) {
            Toast.makeText(this, R.string.error_bill_amont_is_required, Toast.LENGTH_SHORT).show();
            return false;
        } else if (Double.parseDouble(mTotalAmout.getText().toString()) <= 0) {
            Toast.makeText(this, R.string.error_bill_amont_greater_then_zero, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            totalAmount = Double.parseDouble(mTotalAmout.getText().toString()); //inizialize 'total amount' with the user input
            //checks if user entered amount payed
            if (!mAmoutPayed.getText().toString().matches("")) {
                amountPayed = Double.parseDouble(mAmoutPayed.getText().toString()); //inizialize 'total amount' with the user input
                //checks if the user's amount payed is greater then the bill's amount
                if (amountPayed > totalAmount) {
                    Toast.makeText(this, R.string.error_amont_payed_lower_then_bill_amount, Toast.LENGTH_SHORT).show();
                    return false;
                }
                //checks if the user's amount payed is greater then 0
                if (amountPayed <= 0) {
                    Toast.makeText(this, R.string.error_amount_payed_lower_then_0, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (mType == BillType.WATER || mType == BillType.GAS || mType == BillType.ELECTRICITY) {
                if (mFromDateEtxt.getText().toString().matches("")) {
                    Toast.makeText(this, getString(R.string.error_enter_from_date), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (mToDateEtxt.getText().toString().matches("")) {
                    Toast.makeText(this, getString(R.string.error_enter_to_date), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        msSelectedDay = mDaysOfMonth.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
