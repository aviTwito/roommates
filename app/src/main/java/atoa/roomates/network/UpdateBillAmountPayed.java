package atoa.roomates.network;

import android.content.Context;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Avi on 6/1/2016.
 */
public class UpdateBillAmountPayed extends AsyncTask<String, Void, Void> {

    private Context context;
    double amount;
    int apartmentNumber;
    String phoneNumber;


    public UpdateBillAmountPayed(Context context , Double amount , int apartmentNumber , String phoneNumber) {
        this.context = context;
        this.amount = amount;
        this.apartmentNumber = apartmentNumber;
        this.phoneNumber = phoneNumber;

    }


    @Override
    protected Void doInBackground(String... arg0) {
        String link;
        String data;
        try {
            //building the query from user input
            data = "?amount=" + URLEncoder.encode((Double.toString(amount)) , "UTF-8");
            data += "&apartmentNumber=" + URLEncoder.encode(Integer.toString(apartmentNumber), "UTF-8");
            data +="&phoneNumber=" + URLEncoder.encode(phoneNumber, "UTF-8");
            //link to the query stored on the server, data responsible for query parameters
            link = "http://roomates.96.lt/UpdateBillAmountPayed.php" + data;
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.connect();
            con.getResponseCode();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
