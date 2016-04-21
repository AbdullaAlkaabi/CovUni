package abdualla.com.covuninavigator;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "places.db";
    public static final String PLACES_TABLE_NAME = "places";
    public static final String PLACES_COLUMN_ID = "id";
    public static final String PLACES_COLUMN_NAME = "name";
    public static final String PLACES_COLUMN_ADDRESS = "address";
    public static final String PLACES_COLUMN_LAT = "latitude";
    public static final String PLACES_COLUMN_LNG = "longitude";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table places " +
                        "(id integer primary key autoincrement, name text,address text,latitude text, longitude text)"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS places");
        onCreate(db);
    }

    public boolean insertPlace(String name, String address, String latitude, String longitude)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("address", address);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        db.insert("places", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from places where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PLACES_TABLE_NAME);
        return numRows;
    }

    public boolean updatePlace(Integer id,String name, String address, String latitude, String longitude)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("address", address);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteAll(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from places");
    }
    public Integer deletePlace (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("places",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Place> getAllplaces()
    {

        ArrayList<Place> array_list = new ArrayList<Place>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from places", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Place temp=new Place();
            temp.setName(res.getString(res.getColumnIndex(PLACES_COLUMN_NAME)));
            temp.setAddress(res.getString(res.getColumnIndex(PLACES_COLUMN_ADDRESS)));
            temp.setLat(Float.parseFloat(res.getString(res.getColumnIndex(PLACES_COLUMN_LAT))));
            temp.setLng(Float.parseFloat(res.getString(res.getColumnIndex(PLACES_COLUMN_LNG))));
            array_list.add(temp);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}