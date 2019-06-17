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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.verification.Config;
import com.sinch.verification.IncorrectCodeException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import atoa.roomates.R;

/**
 * class represents the phone validation screen
 * responsible for validating the phone number by sending code in sms to the phone number which the user entered
 * and them asking him to enter the received code and validating it
 */
public class PhoneValidationActivity extends AppCompatActivity {

    //DATA
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PHONE_NUMBER_VALIDATED = "phoneNumberIsValidated";
    private static final String APPLICATION_KEY = "6339e851-c4e4-4231-8f22-d9316f7aff61";
    private Verification mVerification;
    private String mCountryAreaCode;
    private SharedPreferences mPreferences;

    //UI
    TextView mTryAgain;
    Button mSumbit;
    EditText mPhoneNumberInput, mCodeInput;
    LinearLayout mInputLayout;
    TextView mEnterPhoneNumebr;
    ProgressBar mProgressBar;


    Typeface mFont;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_validation);

        mPreferences = getSharedPreferences("appData" , 0);

        mSumbit = (Button)findViewById(R.id.smsVerificationButton);
        mPhoneNumberInput = (EditText)findViewById(R.id.phoneNumber);
        mProgressBar = (ProgressBar)findViewById(R.id.progressIndicator);
        mEnterPhoneNumebr = (TextView)findViewById(R.id.enterPhoneNumber);
        mInputLayout = (LinearLayout)findViewById(R.id.inputContainer);
        mCodeInput = (EditText)findViewById(R.id.codeInput);
        mTryAgain = (TextView)findViewById(R.id.smsNotRecived);

        mTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputLayout.setVisibility(View.GONE);
                mSumbit.setVisibility(View.VISIBLE);
                mPhoneNumberInput.setVisibility(View.VISIBLE);
                mEnterPhoneNumebr.setVisibility(View.VISIBLE);
            }
        });

        mPhoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    mSumbit.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //gets font from asstes folder
        mFont = Typeface.createFromAsset(getAssets() , "fonts/noot-aj.ttf");

        //gets the area code of the user`s country
        mCountryAreaCode = com.sinch.verification.PhoneNumberUtils.getDefaultCountryIso(this);

    }

    /**
     * called when the validation button is pressed
     * calling a method to start the validation process if the input of the user is valid (String which is lenth 10 and only contains numbers)
     * @param view the validation button
     */
    public void sumbitButton_OnCLick(View view){
        if(mPhoneNumberInput.getText().toString().matches("[0-9]+")
                && mPhoneNumberInput.getText().toString().length() == 10)
        {
            startValidion(getE164Number());
            mProgressBar.setVisibility(View.VISIBLE);
            mSumbit.setVisibility(View.GONE);
            mPhoneNumberInput.setVisibility(View.GONE);
            mEnterPhoneNumebr.setVisibility(View.GONE);
//            InputMethodManager inputManager =
//                    (InputMethodManager) this.
//                            getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputManager.hideSoftInputFromWindow(
//                    this.getCurrentFocus().getWindowToken(),
//                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        else{
            mPhoneNumberInput.setError(getString(R.string.error_illegal_phone_number));
        }
    }


    /**
     * called from 'sumbitButton_OnCLick' method
     * starts the verification process
     * @param phoneNumber the user unput
     */
    private void startValidion(String phoneNumber) {
        Config config = SinchVerification.config().applicationKey(APPLICATION_KEY).context(getApplicationContext()).build();

        VerificationListener listener = new MyVerificationListener();
        try {

            mVerification = SinchVerification.createSmsVerification(config, phoneNumber, listener);
            mVerification.initiate();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * called when the verify button is pressed
     * verifying the user input code with the code the user received by SMS message
     * @param view verify button
     */
    public void btnValidate_OnClick(View view) {
        String code = mCodeInput.getText().toString();
        if(code == null || code.equals("") || code.length() != 4){
            mCodeInput.setError(getString(R.string.code_must_be_4_digits));
        }
        else {
            mCodeInput.setText("");
            mCodeInput.clearFocus();
            mVerification.verify(code);
            mInputLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }


    /**
     * inner class which listen to the process of the verification and implement different methods which invoke
     * on different stages of the verfication process
     */
    class MyVerificationListener implements VerificationListener {


        /**
         * the the verification progress has started
         */
        @Override
        public void onInitiated() {

        }


        /**
         * the verification process failed to start
         * @param exception
         */
        @Override
        public void onInitiationFailed(Exception exception) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), R.string.error_something_went_wrong, Toast.LENGTH_SHORT).show();
        }

        /**
         * the phone number has been verified
         */
        @Override
        public void onVerified() {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), R.string.verification_successful, Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(PHONE_NUMBER_VALIDATED, true);
            editor.putString(PHONE_NUMBER, mPhoneNumberInput.getText().toString());
            editor.apply();

            //after user phone number is verified user is redirected to registration screen
            Intent intent = new Intent(getApplicationContext(), RegistraionActivity.class);
            startActivity(intent);
        }


        /**
         * called when the number verification is failed
         * @param exception the exception occurred on the verification process
         */
        @Override
        public void onVerificationFailed(Exception exception) {
            //if the user input does not match the recived code
            if (exception instanceof IncorrectCodeException) {
                Toast.makeText(getApplicationContext(), R.string.error_wrong_code, Toast.LENGTH_SHORT).show();
            }

            mProgressBar.setVisibility(View.GONE);
            mInputLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * converting the phone number entred by the user into 'E164' format
     * @return  phone number string in 'E164' format (+9725XXXXXXXX)
     */
    private String getE164Number() {
        return PhoneNumberUtils.formatNumberToE164(mPhoneNumberInput.getText().toString(), mCountryAreaCode);
    }
}
