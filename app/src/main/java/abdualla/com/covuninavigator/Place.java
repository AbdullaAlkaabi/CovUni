package abdualla.com.covuninavigator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to store details of a place of interest
 */
public class Place implements Parcelable {
    String name;
    private String address;
    private float lng;
    private float lat;

    public Place(String name, String address, float lat, float lng) {
        this.name=name;
        this.address=address;
        this.lng=lng;
        this.lat=lat;
    }

    private Place(Parcel in) {
        name = in.readString();
        address = in.readString();
        lng = in.readFloat();
        lat = in.readFloat();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    void place(String name,String address,float lng,float lat){
        this.name=name;
        this.address=address;
        this.lng=lng;
        this.lat=lat;
    }

    public Place(){

    }
    void setName(String name){
        this.name=name;
    }
    String getName(){
        return this.name;
    }
    void setAddress(String address){
        this.address=address;
    }
    String getAddress(){
        return this.address;
    }
    void setLng(float lng){
        this.lng=lng;
    }
    float getLng(){
        return this.lng;
    }
    void setLat(float lat){
        this.lat=lat;
    }
    float getLat(){
        return this.lat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeFloat(lng);
        dest.writeFloat(lat);
    }
}
