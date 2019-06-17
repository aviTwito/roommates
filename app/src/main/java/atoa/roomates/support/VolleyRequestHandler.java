package atoa.roomates.support;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Avi on 7/11/2016.
 */
public class VolleyRequestHandler {
    private static VolleyRequestHandler mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;


    private VolleyRequestHandler(Context context){
        mContext = context;
        requestQueue = getRequestQueue();

    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleyRequestHandler getInstance(Context context){
        if(mInstance == null){
            mInstance = new VolleyRequestHandler(context);
        }
        return mInstance;
    }

    public<T> void addToRquestQueue(Request<T> request){
        requestQueue.add(request);
    }
}
