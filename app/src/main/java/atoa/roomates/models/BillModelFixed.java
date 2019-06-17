package atoa.roomates.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import atoa.roomates.support.BillType;

/**
 * Created by or on 29/05/2016.
 */
public class BillModelFixed extends BillModel {
    private int dayOfPayment;

    public int getDayOfPayment() {
        return dayOfPayment;
    }

    public void setDayOfPayment(int dayOfPayment) {
        this.dayOfPayment = dayOfPayment;
    }

    public BillModelFixed(BillType t, double amount ,String note,int dayOfPayment){
        super(t, amount , note);
        this.dayOfPayment = dayOfPayment;
    }

    public BillModelFixed(JSONObject object) throws JSONException, ParseException {
        super(object);
        this.dayOfPayment = object.getInt("dayOfPayment");
    }
}
