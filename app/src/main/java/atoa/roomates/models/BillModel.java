package atoa.roomates.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import atoa.roomates.support.BillType;

/**
 * Created by or on 25/05/2016.
 */
public class BillModel {

    private double amount;
    private BillType type;
    private String note;
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public double getAmount() {
        return amount;
    }

    public BillModel() {
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public BillType getType() {
        return type;
    }

    public void setType(BillType type) {
        this.type = type;
    }

    public BillModel(BillType t, double amount, String note) {
        this.amount = amount;
        type = t;
        this.note = note;

    }

    public BillModel(JSONObject object) throws JSONException, ParseException {

        this.id = object.getInt("id");
        this.amount = object.getDouble("billAmount");
        switch (object.getString("billName")) {
            case "WATER":
                this.type = BillType.WATER;
                break;
            case "ARNONA":
                this.type = BillType.ARNONA;
                break;
            case "ELECTRICITY":
                this.type = BillType.ELECTRICITY;
                break;
            case "GAS":
                this.type = BillType.GAS;
                break;
            case "TV":
                this.type = BillType.TV;
                break;
            case "INTERNET":
                this.type = BillType.INTERNET;
                break;
            case "OTHER":
                this.type = BillType.OTHER;
        }
        this.note = object.getString("notes");

    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
