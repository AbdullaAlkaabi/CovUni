package abdualla.com.covuninavigator;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MyContentProvider extends ContentProvider {

    private static  final String PROVIDER_NAME ="abdualla.com.provider.places";
    private static final String URL= "content://"+PROVIDER_NAME+"/places";
    static final Uri CONTENT_URI=Uri.parse(URL);
    static final String ID = "id";
    static final public String NAME = "name";
    static final public String ADDRESS = "address";
    static final public String LAT = "latitude";
    static final public String LNG = "longitude";

    private static final int PLACES=1;
    private static final int PLACES_ID=2;
    static int PLACES_NAME=3;
    static int PLACES_ADDRESS=4;
    static int PLACES_LAT=5;
    static int PLACES_LNG=6;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"places",PLACES);
        uriMatcher.addURI(PROVIDER_NAME,"places/#",PLACES_ID);
    }
    private static Context context;
    private static HashMap<String,String> PLACES_PROJECTION_MAP;
    private static SQLiteDatabase db;
    private static final String DATABASE_NAME = "places.db";
    private static final String PLACES_TABLE_NAME = "places";
    private static final String PLACES_COLUMN_ID = "id";
    public static final String PLACES_COLUMN_NAME = "name";
    public static final String PLACES_COLUMN_ADDRESS = "address";
    public static final String PLACES_COLUMN_LAT = "latitude";
    public static final String PLACES_COLUMN_LNG = "longitude";
    private static final int DATABASE_VERSION = 1;
    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "create table "+PLACES_TABLE_NAME+
                            "(id integer primary key autoincrement, name text,address text,latitude text, longitude text);"
            );
            Log.v("Data name",db.getPath());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+PLACES_TABLE_NAME);
            onCreate(db);
        }
    }


    @Override
    public boolean onCreate() {
        context=getContext();
        DatabaseHelper dbhelp=new DatabaseHelper(context);

        db=dbhelp.getWritableDatabase();
        return (db==null)? false:true;
    }

    public ArrayList<Place> getAll(){
        ArrayList<Place> items=new ArrayList<Place>();
        Cursor cursor = query(MyContentProvider.CONTENT_URI, null, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {
                Place temp=new Place(cursor.getString(1),cursor.getString(2),Float.parseFloat(cursor.getString(3)),Float.parseFloat(cursor.getString(4)));
                items.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PLACES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case 1:
                qb.setProjectionMap(PLACES_PROJECTION_MAP);
                break;

            case 2:
                qb.appendWhere( PLACES_COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || Objects.equals(sortOrder, "")){
            /**
             * By default sort on student names
             */
            sortOrder = NAME;
        }

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        Log.v("Da", context.toString());
       c.setNotificationUri(context.getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case 1:
                return "abdualla.com.navigator.cursor.dir/abdualla.com.navigator.places";

            /**
             * Get a particular student
             */
            case 2:
                return "abdualla.com.navigator.cursor.dir/abdualla.com.navigator.places";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {


        long rowID= db.insert(PLACES_TABLE_NAME,"",values);
        if(rowID>0){
            Uri _uri= ContentUris.withAppendedId(CONTENT_URI, rowID);
            context.getContentResolver().notifyChange(_uri, null);
            return _uri;
        }else {
            return null;
        }
//        throw new SQLException("Failed to add a record into " + uri);
    }

    public void insertPlace(String name, String address, String latitude, String longitude)
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(ADDRESS, address);
        contentValues.put(LAT, latitude);
        contentValues.put(LNG, longitude);
        insert(CONTENT_URI,contentValues);
    }    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count=0;

        switch (uriMatcher.match(uri)){
        case 1:
        count = db.delete(PLACES_TABLE_NAME, selection, selectionArgs);
        break;

        case 2:
        String id = uri.getPathSegments().get(1);
        count = db.delete( PLACES_TABLE_NAME,PLACES_COLUMN_ID +  " = " + id +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        break;

        default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    context.getContentResolver().notifyChange(uri, null);
    return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case 1:
                count = db.update(PLACES_TABLE_NAME, values, selection, selectionArgs);
                break;

            case 2:
                count = db.update(PLACES_TABLE_NAME, values, PLACES_COLUMN_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        context.getContentResolver().notifyChange(uri, null);
        return count;
    }
}
