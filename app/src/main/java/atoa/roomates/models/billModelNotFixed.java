package atoa.roomates.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import atoa.roomates.support.BillType;

/**

 * Created by or on 29/05/2016.
 */
public class billModelNotFixed extends BillModel {
    Date from;
    Date to;

    public billModelNotFixed(BillType t, double amount,String note ,Date fromDate, Date toDate){
        super(t,amount , note);
        this.from = fromDate;
        this.to = toDate;
    }

    public billModelNotFixed(JSONObject object) throws JSONException, ParseException {
        super(object);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        this.setTo(formatter.parse(object.getString("toDate")));
        this.setFrom(formatter.parse(object.getString("fromDate")));
    }


    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
