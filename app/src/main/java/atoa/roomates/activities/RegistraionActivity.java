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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;
import atoa.roomates.support.VolleyRequestHandler;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * class to represent the registration screen
 * respomsible for adding the user to the app data base
 */
public class RegistraionActivity extends AppCompatActivity {



    //DATA
    SharedPreferences preferences;
    final int CAMERA_REQUEST = 1100;
    final int GALLERY_REQUEST = 2200;

    ExifInterface mExif;
    CameraPhoto mCameraPhoto;
    GalleryPhoto mGalleryPhoto;
    Bitmap mProfilePicture;

    public static final String REGISTER_URL = "http://roomates.96.lt/Register.php";
    public static final String USER_PROFILE_PICTURE = "photoPath";

    public static final String FIRST_USERNAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String PASSWORD = "password";
    public static final String PHONE_NUMBER = "phonenumber";
    public static final String PROFILE_IMAGE = "image";
    private static final String IS_REGISTERED = "isRegistered";

    //UI
    private EditText mEtPassword, mEtFirstName, mEtLastName, mPhoneNumber;
    CircleImageView mIvProfilePic;
    ImageView mCamera;
    private int mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registraion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("appData", 0);

        mCameraPhoto = new CameraPhoto(getApplicationContext());
        mGalleryPhoto = new GalleryPhoto(getApplicationContext());

        //parameters required for registration
        mEtPassword = (EditText) findViewById(R.id.password);
        mEtFirstName = (EditText) findViewById(R.id.first_name);
        mEtLastName = (EditText) findViewById(R.id.last_name);
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        mPhoneNumber.setText(preferences.getString(PhoneValidationActivity.PHONE_NUMBER, ""));
        mIvProfilePic = (CircleImageView) findViewById(R.id.profilePicture);
        mCamera = (ImageView) findViewById(R.id.camera);
        mIvProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }


    /**
     * called when register button is clicked
     * if the user input is valid calling registerUser() which handle user registration
     *
     * @param view register button which inherits from View
     */
    public void signup(View view) {

        if (inputIsValid()) {
            registerUser();
            this.startActivity(new Intent(this, LoginActivity.class));
        }

    }

    /**
     * called from signup()
     * checks if the user input is valid
     *
     * @return true if the user input is valid and false otherwise
     */
    public boolean inputIsValid() {
        if (mEtFirstName.getText().toString().length() == 0) {
            mEtFirstName.setError(getString(R.string.cant_be_empty));
            return false;
        } else if (mEtLastName.getText().toString().length() == 0) {
            mEtLastName.setError(getString(R.string.cant_be_empty));
            return false;
        } else if (mEtPassword.getText().toString().length() < 6) {
            mEtPassword.setError(getString(R.string.passwerd_cant_be_lower_then_6_chars));
            return false;
        } else if (mEtFirstName.getText().toString().length() < 2) {
            mEtFirstName.setError(getString(R.string.firstName_too_short));
            return false;
        } else if (mEtLastName.getText().toString().length() < 2) {
            mEtLastName.setError(getString(R.string.lastName_too_short));
            return false;
        }
        return true;

    }

    /**
     * called from signup() in case the user input is valid
     * passing the user details into php script which handles the user registration
     */
    public void registerUser() {
        final String password = mEtPassword.getText().toString();
        final String Fname = mEtFirstName.getText().toString();
        final String Lname = mEtLastName.getText().toString();
        final String phone = preferences.getString(PhoneValidationActivity.PHONE_NUMBER, ""); //retrieve the user verified number
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
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
                params.put(FIRST_USERNAME, Fname);
                params.put(LAST_NAME, Lname);
                params.put(PASSWORD, password);
                params.put(PHONE_NUMBER, phone);
                params.put(IS_REGISTERED , "1");
                params.put("Token", FirebaseInstanceId.getInstance().getToken());
                if (mProfilePicture !=null) {
                    params.put(PROFILE_IMAGE, ImageBase64.encode(mProfilePicture)); //the code line where the app crushes
                }
                else{
                    params.put(PROFILE_IMAGE, ImageBase64.encode(BitmapFactory.decodeResource(getResources(), R.drawable.defult_icon)));
                }
                return params;
            }
        };
        VolleyRequestHandler.getInstance(getApplicationContext()).addToRquestQueue(stringRequest);
    }


    /**
     * called when the user click on the CircleImageView object in the activity
     * letting the user choose a profile photo from galley/camera
     */
    public void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_gallery),
                getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistraionActivity.this);
        builder.setTitle(getString(R.string.how_roommates_see_you));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    try {
                        startActivityForResult(mCameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (item == 1) {
                    startActivityForResult(mGalleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
                } else if (item == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String photoPath;
        SharedPreferences.Editor editor = preferences.edit();
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                photoPath = mCameraPhoto.getPhotoPath();
                editor.putString(USER_PROFILE_PICTURE, photoPath);
                editor.apply();
                try {
                    mExif = new ExifInterface(photoPath);
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    mOrientation = mExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    mProfilePicture = GenericMethods.getInstance(this).rotateBitmap(bitmap, mOrientation);
                    mIvProfilePic.setImageBitmap(mProfilePicture);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            if (requestCode == GALLERY_REQUEST) {
                mGalleryPhoto.setPhotoUri(data.getData());
                photoPath = mGalleryPhoto.getPath();
                editor.putString(USER_PROFILE_PICTURE, photoPath);
                editor.apply();
                try {
                    mExif = new ExifInterface(photoPath);
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    mOrientation = mExif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    mProfilePicture = GenericMethods.getInstance(this).rotateBitmap(bitmap, mOrientation);
                    mIvProfilePic.setImageBitmap(mProfilePicture);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

            mIvProfilePic.setBorderWidth(1);
            mIvProfilePic.setBorderColor(000000);
            mCamera.setVisibility(View.GONE);
        }
    }


    public void login(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, R.string.press_to_exit,
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
