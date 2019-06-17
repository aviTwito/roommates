package atoa.roomates.firebase;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import atoa.roomates.support.VolleyRequestHandler;

/**
 * Created by or on 16/08/2016.
 */
public class SendNotification {

    public static final String SEND_NOTIFICATION_URL = "http://roomates.96.lt/PushNotification.php";
    private static final String MESSAGE = "message";
    private static final String APARTMENT_NUMBER = "apartmentNumber";
    private static final String USER_PHONE_NUMBER = "phone";

    public void sendMessage(final String message , final int apartmentNumber , final String phone, Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_NOTIFICATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(RegistraionActivityScreen.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(MESSAGE, message);
                params.put(APARTMENT_NUMBER, String.valueOf(apartmentNumber));
                params.put(USER_PHONE_NUMBER, phone);
                return params;
            }
        };
        VolleyRequestHandler.getInstance(context).addToRquestQueue(stringRequest);
    }
}
