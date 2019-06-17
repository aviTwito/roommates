package atoa.roomates.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import atoa.roomates.models.BillModel;
import atoa.roomates.models.BillModelFixed;
import atoa.roomates.models.billModelNotFixed;

/**
 * Created by Avi on 5/29/2016.
 */
public class InsertNewBill extends AsyncTask<String, Void, Void> {

    private Context context;
    String userPhone;
    BillModel bill;
    int apartmentNumber;
    double amountPayed;
    SharedPreferences preferences;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public InsertNewBill(Context context , BillModel bill , int apartmentNumber , double amountPayed , String userPhone) {
        this.context = context;
        this.bill = bill;
        this.apartmentNumber = apartmentNumber;
        this.amountPayed = amountPayed;
        this.userPhone = userPhone;
    }


    @Override
    protected Void doInBackground(String... arg0) {
        String link;
        String data;

        try {
            //building the query from user input
            data = "?apartmentNumber=" + URLEncoder.encode(Integer.toString(apartmentNumber), "UTF-8");
            data += "&amount=" + URLEncoder.encode(Double.toString(bill.getAmount()), "UTF-8");
            data += "&amountPayed=" + URLEncoder.encode(Double.toString(amountPayed), "UTF-8");
            if(bill instanceof billModelNotFixed) {
                data += "&startDate=" + URLEncoder.encode((formatter.format(((billModelNotFixed) bill).getFrom())) , "UTF-8");
                data += "&toDate=" + URLEncoder.encode((formatter.format(((billModelNotFixed) bill).getTo())) , "UTF-8");
            }
            else if(bill instanceof BillModelFixed){
                data += "&startDate=" + "&toDate=";
                data += "&dayOfPayment=" + URLEncoder.encode(Integer.toString(((BillModelFixed) bill).getDayOfPayment()), "UTF-8");
            }
            else {
                data += "&startDate="+"&toDate="+"&dayOfPayment=";
            }
            data += "&notes=" + URLEncoder.encode(bill.getNote().toString(), "UTF-8");
            data += "&name=" + URLEncoder.encode(bill.getType().toString() , "UTF-8");
            data += "&phone=" + URLEncoder.encode(userPhone , "UTF-8");
            //link to the query stored on the server, data responsible for query parameters
            link = "http://roomates.96.lt/AddBill.php" + data;
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            con.getResponseCode();
            return null;
        } catch (Exception e) {
            return null;
        }

    }


    public void onPostExecute(Void v) {
        preferences = context.getSharedPreferences("appData" , 0);
        String phone = preferences.getString("phoneNumber", "");
        new UpdateBillAmountPayed(context,amountPayed,apartmentNumber,phone).execute();

    }


}
