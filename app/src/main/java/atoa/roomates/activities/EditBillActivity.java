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
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import atoa.roomates.network.EditExistingBill;
import atoa.roomates.R;
import atoa.roomates.support.BillType;
import atoa.roomates.support.GenericMethods;

/**
 * class to represent the edit bill screen where user can edit existing bills
 */
public class EditBillActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    //UI
    private EditText mFromDateEtxt, mToDateEtxt, mTotalAmout, mBillNote, mAmoutPayed;
    private Button mBtnAmountPayed,mBtnEditClick,mIbHeaderImage;
    private Spinner mSpinneDaysOfMonth;

    //DATA
    RoomateModel mRoomateModel;
    BillType mType;
    String mSelectedPayDay;
    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mToDatePickerDialog;
    private Calendar mNewDate;
    private SimpleDateFormat mDateFormatter;
    Typeface mFont;
    TextView mToolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mDateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        mRoomateModel = GenericMethods.getInstance(this).getUserData();

        setBillType(getIntent().getStringExtra("type"));
        setContent(mType);
        mTotalAmout = (EditText) findViewById(R.id.totalAmount);
        mTotalAmout.setText(Double.toString(getIntent().getDoubleExtra("amount", 0)));
        mBillNote = (EditText) (findViewById(R.id.note));
        mBillNote.setText(getIntent().getStringExtra("note"));
        mAmoutPayed = (EditText) findViewById(R.id.amoutPayed);
        mBtnAmountPayed = (Button) findViewById(R.id.btnAmountPayed);
        mBtnAmountPayed.setText(Double.toString(getIntent().getDoubleExtra("amount", 0)));
    }

    private void setBillType(String billType) {
        switch (billType) {
            case "WATER":
                mType = BillType.WATER;
                break;
            case "GAS":
                mType = BillType.GAS;
                break;
            case "ELECTRICITY":
                mType = BillType.ELECTRICITY;
                break;
            case "INTERNET":
                mType = BillType.INTERNET;
                break;
            case "TV":
                mType = BillType.TV;
                break;
            case "ARNONA":
                mType = BillType.ARNONA;
                break;
            case "OTHER":
                mType = BillType.OTHER;
                break;
        }
    }



    private void setContent(BillType type) {
        if (type == BillType.OTHER) {
            setContentView(R.layout.new_bill_other);
            setToolBar();
        } else if (type == BillType.WATER || type == BillType.GAS || type == BillType.ELECTRICITY) {
            setContentView(R.layout.new_bill_not_fixed);
            setToolBar();
            mFromDateEtxt = (EditText) findViewById(R.id.etxt_fromdate);
            mFromDateEtxt.setInputType(InputType.TYPE_NULL);

            mToDateEtxt = (EditText) findViewById(R.id.etxt_todate);
            mToDateEtxt.setInputType(InputType.TYPE_NULL);
            setDateTimeField();

            //sets the date text of the 'fromDate edit text' and 'toDate edit text' according to the selected bill
            mFromDateEtxt.setText(getIntent().getStringExtra("fromDate"));
            mToDateEtxt.setText(getIntent().getStringExtra("toDate"));

        } else {
            setContentView(R.layout.new_bill_fixed);
            setToolBar();
            mSpinneDaysOfMonth = (Spinner) findViewById(R.id.daysOfMonth);
            mSpinneDaysOfMonth.setOnItemSelectedListener(this);

            //sets the selection of the spinner according to the selected bills
            int index = getIntent().getIntExtra("dayOfPayment", 0);
            index--;
            mSpinneDaysOfMonth.setSelection(index);
        }

    }

    public void setToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //changes the tool bar font
        mToolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/noot-aj.ttf");
        mToolBarTitle.setTypeface(mFont);
        mBtnEditClick = (Button)findViewById(R.id.btnAddNewBill);
        mBtnEditClick.setText(R.string.update);
        mIbHeaderImage = (Button)findViewById(R.id.btnInternet);
        mIbHeaderImage.setVisibility(View.GONE);
    }

    private void setDateTimeField() {
        mFromDateEtxt.setOnClickListener(this);
        mToDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        mFromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mNewDate = Calendar.getInstance();
                mNewDate.set(year, monthOfYear, dayOfMonth);
                mFromDateEtxt.setText(mDateFormatter.format(mNewDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mToDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mNewDate = Calendar.getInstance();
                mNewDate.set(year, monthOfYear, dayOfMonth);
                mToDateEtxt.setText(mDateFormatter.format(mNewDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View v) {
        if (v == mFromDateEtxt) {
            mFromDatePickerDialog.show();
        } else if (v == mToDateEtxt) {
            mToDatePickerDialog.show();
        }
    }

    public void btnAddNewBill_OnClick(View view) {
        BillModel bill;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fdate, tDate;
        double amountPayed = 0;

        if (userInputIsValid()) {
            if (!mAmoutPayed.getText().toString().matches("")) {
                amountPayed = Double.parseDouble(mAmoutPayed.getText().toString());
            }

            //updating the bill in case of the bill object is 'BillModelNotFixed'
            if (mType == BillType.WATER || mType == BillType.GAS || mType == BillType.ELECTRICITY) {
                try {
                    fdate = formatter.parse(mFromDateEtxt.getText().toString());
                    //((billModelNotFixed) bill).setFrom(date);
                    tDate = formatter.parse(mToDateEtxt.getText().toString());
                    //((billModelNotFixed) bill).setTo(date);
                    bill = new billModelNotFixed(mType, Double.parseDouble(mTotalAmout.getText().toString()), mBillNote.getText().toString(), fdate, tDate);
                    bill.setId(getIntent().getIntExtra("id", 0));
                    new EditExistingBill(this, bill, amountPayed, mRoomateModel.getPhoneNumber(), mRoomateModel.getApartmentNumber()).execute();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (mType == BillType.ARNONA || mType == BillType.INTERNET || mType == BillType.TV) {
                bill = new BillModelFixed(mType, Double.parseDouble(mTotalAmout.getText().toString()), mBillNote.getText().toString(), Integer.parseInt(mSelectedPayDay));
                bill.setId(getIntent().getIntExtra("id", 0));
                new EditExistingBill(this, bill, amountPayed, mRoomateModel.getPhoneNumber(), mRoomateModel.getApartmentNumber()).execute();
            } else {
                bill = new BillModel(mType, Double.parseDouble(mTotalAmout.getText().toString()), mBillNote.getText().toString());
                bill.setId(getIntent().getIntExtra("id", 0));
                new EditExistingBill(this, bill, amountPayed, mRoomateModel.getPhoneNumber(), mRoomateModel.getApartmentNumber()).execute();
            }

            Intent intent = new Intent(this, BillsActivity.class);
            this.startActivity(intent);
        }
    }


    /**
     * called when add new bill button is clicked
     * checks if the user input is valid before inserting new bill to data base
     *
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
        mSelectedPayDay = mSpinneDaysOfMonth.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
