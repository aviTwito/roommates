package atoa.roomates.models;


import android.graphics.Bitmap;

public class NotesModel {

    private String userMessage, encodedImage;




    public NotesModel(){

    }

    public NotesModel(String encodedImage, String userMessage, String messageDate) {
        this.encodedImage = encodedImage;
        this.userMessage = userMessage;
        this.messageDate = messageDate;
    }

    public NotesModel(Bitmap bitmap ,String userMessage, String messageDate) {

        this.userMessage = userMessage;
        this.messageDate = messageDate;
    }


    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }


    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    private String messageDate;

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}
