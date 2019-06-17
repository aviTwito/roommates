package atoa.roomates.support;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import atoa.roomates.activities.HomeActivity;
import atoa.roomates.activities.LoginActivity;
import atoa.roomates.activities.PhoneValidationActivity;


/**
 * This class is the first class that open when the app start
 */
public class AppStartScreen extends Activity {
    //Data
    SharedPreferences preferences;
    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("appData" , 0);
        //boolean object that indicate if the phone was verify
        boolean phoneNumberIsValidated =  preferences.getBoolean(PhoneValidationActivity.PHONE_NUMBER_VALIDATED, false);

//        checks if the user has validated his phone number.
//        the user cant proceed to any activity before validating his number
        if (phoneNumberIsValidated){

            boolean logged = preferences.getBoolean(LoginActivity.USER_IS_LOGGED_IN, false);
            if (logged) {
                intent = new Intent(this,HomeActivity.class);
            } else {
                intent = new Intent(this,LoginActivity.class);
            }
        }
        else{
            intent = new Intent(this , PhoneValidationActivity.class);
        }
//        Intent intent = new Intent(this, RegistraionActivity.class);
        startActivity(intent);
        finish();
    }
}
