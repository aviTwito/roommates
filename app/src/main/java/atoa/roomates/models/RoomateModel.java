package atoa.roomates.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by or on 12/04/2016.
 */
public class RoomateModel{


    private String name,lastName,phoneNumber,profilePicture;
    private int apartmentNumber;

    public int getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(int apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public RoomateModel(JSONObject object) {
        try {
            this.name = object.getString("firstName");
            this.lastName = object.getString("lastName");
            this.apartmentNumber = Integer.parseInt(object.get("apartmentNumber").toString());
            this.profilePicture = "http://"+object.getString("image");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public RoomateModel(String name, String lastName, String phoneNumber){
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String  getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String  profilePicture) {
        this.profilePicture = profilePicture;
    }
}
