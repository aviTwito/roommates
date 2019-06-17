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
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import atoa.roomates.models.RoomateModel;
import atoa.roomates.R;
import atoa.roomates.support.VolleyRequestHandler;


/**
 * class to represents the login screen
 * responsible for user login. takes the user input and checks it against the app data base
 */
public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "http://roomates.96.lt/Login.php";
    public static final String USER_IS_LOGGED_IN = "loggedIn";

    //UI
    Context mContext;
    Button mBtnLogin;
    ProgressBar mProgressBar;
    ScrollView mLoginLayout;
    EditText mEtPhone, mEtpassword;
    TextView mRegestraion, mAppName;
    Typeface mFont;

    //DATA
    SharedPreferences mPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferences = this.getSharedPreferences("appData",0);
        boolean userIsLoggedIn = mPreferences.getBoolean(USER_IS_LOGGED_IN,false);
        if(userIsLoggedIn){
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();
        mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/noot-aj.ttf");

        //Initializing Views

        mProgressBar = (ProgressBar) findViewById(R.id.logging);
        mLoginLayout = (ScrollView) findViewById(R.id.inputLayout);
        mPreferences = getSharedPreferences("appData", 0);
        mBtnLogin = (Button) findViewById(R.id.login);
        mRegestraion = (TextView) findViewById(R.id.registration);
        mAppName = (TextView) findViewById(R.id.appName);
        mAppName.setTypeface(mFont);

        mEtPhone = (EditText) findViewById(R.id.Phone);
        mEtpassword = (EditText) findViewById(R.id.etPassword);

        //when click on registriont open new activite
        mRegestraion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistraionActivity.class));
            }
        });
    }


    /**
     * called when user click on log in button
     *
     * @param view is the log in button which inherits from View
     */
    public void signIn(View view) {
        //showing erorr massege to the user in case of blank field
        if (mEtPhone.getText().toString().equals("") || mEtpassword.getText().toString().equals("")) {
            Toast.makeText(this, R.string.error_userName_and_password_is_required, Toast.LENGTH_SHORT).show();
        } else {
            //new SignInActivity(this).execute(etPhone.getText().toString() , etpassword.getText().toString());
            signIn(mEtPhone.getText().toString(), md5(mEtpassword.getText().toString()).substring(0, 15));
            mLoginLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mRegestraion.setVisibility(View.GONE);
            //
        }
    }


    /**
     * responsible for user log in. calling a php script which check whether the user exists in the data base and e password matches
     *
     * @param phoneNumber the user phone number
     * @param password    the user password
     */
    public void signIn(String phoneNumber, String password) {
        String url = LOGIN_URL;
        try {
            url += "?phone=" + URLEncoder.encode(phoneNumber, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            String query_result = response.getString("query_result");
                            switch (query_result) {
                                case "SUCCESS": // user name and password exists and mathces and user associated with apartment
                                    SharedPreferences.Editor editor = mPreferences.edit();
                                    RoomateModel roomateModel = new RoomateModel(response);
                                    Gson gson = new Gson();
                                    String json = gson.toJson(roomateModel);
                                    editor.putString("USER", json);
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    editor.putBoolean(USER_IS_LOGGED_IN, true);
                                    editor.apply();
                                    startActivity(intent);
                                    break;

                                case "FAILURE":  //user name or password are incorrect
                                    mLoginLayout.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), R.string.error_wrong_userName_or_password, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    break;
                            }

                        } catch (JSONException e) { //this exception is caught
                            e.printStackTrace();
                            mLoginLayout.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            Log.e("e", e.toString() + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoginLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

        });
        VolleyRequestHandler.getInstance(mContext).addToRquestQueue(jsonObjectRequest);
    }


    /**
     * function md5 encryption for passwords
     *
     * @param password
     * @return passwordEncrypted
     */
    private static final String md5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}