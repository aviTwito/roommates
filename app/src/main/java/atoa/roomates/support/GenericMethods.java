package atoa.roomates.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import atoa.roomates.models.RoomateModel;
import atoa.roomates.activities.PhoneValidationActivity;

/**
 * Created by Avi on 8/8/2016.
 */
public class GenericMethods {
    private static GenericMethods mInstance;
    private Context mContext;
    private RoomateModel roomateModel;
    private Gson gson;
    SharedPreferences preferences;

    private GenericMethods(Context context){
        this.mContext = context;
        preferences = mContext.getSharedPreferences("appData" , 0);
    }

    public static synchronized GenericMethods getInstance(Context context){
        if(mInstance == null){
            mInstance = new GenericMethods(context);
        }
        return mInstance;
    }

    /**
     * @return model which represents the current user
     */
    public RoomateModel getUserData(){
        String json = preferences.getString("USER" , "");
        gson = new Gson();
        roomateModel = gson.fromJson(json, RoomateModel.class);
        roomateModel.setPhoneNumber(preferences.getString(PhoneValidationActivity.PHONE_NUMBER, ""));
        return roomateModel;
    }


    /**
     * receiving a bitmap object and encoding it into a String
     * @param bitmap to be encoded into a string
     * @return encoded string of the given butmap
     */
    public String encodeImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    /**
     * recriving a String which is encoded Bitmap and decoding it cank to bitmap
     * @param encodedImage string to be decoded into a bitmap
     * @return decoded bitmap of the given String
     */
    public Bitmap decodeImage(String encodedImage){
        byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bitmap;
    }


    /**
     * sets the given recycler view settings
     * @param recycler holds the data and differents activity
     */
    public void setRecyclerSetting(RecyclerView recycler){
        recycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
    }

    /**
     * called when setting image inside image views in the app.
     * rotates the picture to be shown in the right way
     * @param orientation the scale if the orientation
     * @param srcBitmap the bitmap to be rotated
     * @return the rotated bitmap
     */
    public static Bitmap setProperOrientation(int orientation, Bitmap srcBitmap) {
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
                    srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }
        return srcBitmap;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (bmRotated != bitmap) {
                bitmap.recycle();
                bitmap = null;
            }
            //bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
