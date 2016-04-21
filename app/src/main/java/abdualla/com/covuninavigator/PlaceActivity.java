package abdualla.com.covuninavigator;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;

//Places List
public class PlaceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final MyContentProvider cp = new MyContentProvider();
    private ListView placeslist;
    private EditText searchbar;
    private static MyAdapter listAdapter;
    private static final ArrayList<Place> list=new ArrayList<Place>();
    private static final String PROVIDER_NAME = "abdualla.com.provider.places";
    private static final String URL = "content://" + PROVIDER_NAME + "/places";
    private static final Uri CONTENT_URI = Uri.parse(URL);

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Search Places");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                AddPlaceFragement addPlaceFragement = new AddPlaceFragement();
                addPlaceFragement.show(fm, "Add Place");
            }
        });
        initiateDB();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        placeslist = (ListView) findViewById(R.id.list);
        searchbar = (EditText) findViewById(R.id.searchplaces);


        listAdapter = new MyAdapter(getApplicationContext(), list);
        placeslist.setAdapter(listAdapter);

        final Intent intent = new Intent(this, MainActivity.class);
        placeslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Click is", String.valueOf(position));
                Place place = list.get(position);
                Bundle bundle = new Bundle();
                bundle.putFloat("lat", place.getLat());
                bundle.putFloat("lng", place.getLng());
                bundle.putString("name", place.getName());

                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerForContextMenu(placeslist);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select an Option");
        menu.add(0, v.getId(), 0, "Show on Map");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //  info.position will give the index of selected item
        int IndexSelected = info.position;

        if (item.getTitle() == "Delete") {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, IndexSelected);
            cp.delete(uri, null, null);
            list.remove(IndexSelected);
            listAdapter.notifyDataSetChanged();

        } else if (item.getTitle() == "Show on Map") {
            Place place = list.get(IndexSelected);
            Bundle bundle = new Bundle();
            bundle.putFloat("lat", place.getLat());
            bundle.putFloat("lng", place.getLng());
            bundle.putString("name", place.getName());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);

        }
        return super.onContextItemSelected(item);

    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Place Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://abdualla.com.covuninavigator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    private void initiateDB() {

        if (list.size() ==0) {
            //Un Comment to Build Database for First time run
            cp.insertPlace("Alan Berry", "Coventry, West Midlands CV1 5FB", "52.407981", "-1.505527");
            cp.insertPlace("Coventry University College", "113A Gosftord Street Coventry CV15FB", "52.407439", "-1.500221");
            cp.insertPlace("Bugatti", "Cox Street Coventry CV1 5FB", "52.407479", "-1.503390");
            cp.insertPlace("Charles Ward", "Cox Street Coventry CV1 5FB", "52.408623", "-1.504856");
            cp.insertPlace("Engineering & Computing Building", "Gulson Rd, Coventry, West Midlands CV1 2JH", "52.405607", "-1.499454");
            cp.insertPlace("Ellen Terry", "Jordan Well,Coventry CV1 5RW", "52.406883", "-1.504703");
            cp.insertPlace("Frederick Lanchester Library", "Frederick Lanchester Building, Coventry University, Coventry, West Midlands CV1 5DD", "52.406056", "-1.500545");
            cp.insertPlace("George Elliot", "Coventry CV15LW", "52.408217", "-1.504890");
            cp.insertPlace("Graham Sutherland", "Gosford Street, Coventry CV1", "52.407212", "-1.502934");
            cp.insertPlace("The Hub", "Gosford Street, Coventry, West Midlands CV1 5QP", "52.407741", "-1.504792");
            cp.insertPlace("Jaguar Building", "Gosford Street, Coventry, West Midlands CV1 5PJ", "52.407450", "-1.500545");
            cp.insertPlace("James Starley", "Cox Street, Coventry, West Midlands CV1 5PH", "52.407892", "-1.504065");
            cp.insertPlace("Maurice Foss", "Maurice Foss Building, Coventry, West Midlands CV1 5PH", "52.408047", "-1.503346");
            cp.insertPlace("Multi-Storey Car Park", "Gosford St, Coventry, West Midlands CV1 5DD", "52.406288", "-1.499699");
            cp.insertPlace("Priory Building", "Priory Street, Coventry", "52.407351", "-1.503692");
            cp.insertPlace("Richard Crossman", "Much Park Street,Coventry, West Midlands CV1HF", "52.406666", "-1.505438");
            cp.insertPlace("Sir John Laing Building", "Much Park Street,Coventry, West Midlands CV1HF", "52.405879", "-1.505003");
            cp.insertPlace("Sir William Lyons", "Gosford Street, Coventry CV1", "52.407476", "-1.499722");
            cp.insertPlace("Student Centre", "Gulson Rd, Coventry, West Midlands CV1 2JH", "52.404984", "-1.500694");
            cp.insertPlace("Whitefriars", "Coventry CV1 2DS", "52.405255", "-1.501582");
            cp.insertPlace("William Morris", "Cox Street, Coventry, West Midlands CV1 5PH", "52.407892", "-1.504065");
            cp.insertPlace("Sports Centre", "Whitefriars Lane, Coventry, West Midlands CV1 2DS", "52.405973", "-1.504225");
            cp.insertPlace("Coventry City Council", "Earl Street, Coventry, West Midlands CV1 5RR", "52.407355", "-1.508035");
            initList();
            //Un Comment to Build Database for First time run
        }
    }


    private void initList() {
        Cursor cursor = cp.query(MyContentProvider.CONTENT_URI, null, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {
                Place temp=new Place(cursor.getString(1),cursor.getString(2),Float.parseFloat(cursor.getString(3)),Float.parseFloat(cursor.getString(4)));
                list.add(temp);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Place Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://abdualla.com.covuninavigator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
 private static void updatelist(Place place){
     ContentValues values= new ContentValues();
     values.put(MyContentProvider.NAME,place.getName());
     values.put(MyContentProvider.ADDRESS,place.getAddress());
     values.put(MyContentProvider.LAT,place.getLat());
     values.put(MyContentProvider.LNG,place.getLng());
    cp.insert(CONTENT_URI, values);
    list.add(place);
    listAdapter.notifyDataSetChanged();

}

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action

            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.places) {
            Intent intent=new Intent(this,PlaceActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static public class AddPlaceFragement extends DialogFragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;
        EditText nameInput;
        EditText addressInput;
        EditText latitudeInput;
        EditText longitudeInput;
        View vi;

        Button saveplace_button;
        Button cancelplace_button;
        String name;
        String address;
        String latitude;
        String longitude;
        public AddPlaceFragement() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            setRetainInstance(true);
            vi=inflater.inflate(R.layout.fragment_add_place_fragement, container, false);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
            saveplace_button=(Button)vi.findViewById(R.id.saveplace_button);
            cancelplace_button=(Button)vi.findViewById(R.id.cancelplace_button);
            nameInput=(EditText)vi.findViewById(R.id.nameInput);
            addressInput=(EditText)vi.findViewById(R.id.addressInput);
            latitudeInput=(EditText)vi.findViewById(R.id.latitudeInput);
            longitudeInput=(EditText)vi.findViewById(R.id.longitudeInput);

            saveplace_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name= nameInput.getText().toString();
                    address=addressInput.getText().toString();
                    latitude=latitudeInput.getText().toString();
                    longitude=longitudeInput.getText().toString();

                    if((name == null) || (address == null) || (latitude == null)){
                        Toast.makeText(getActivity(),"Place not added : Missing Information",Toast.LENGTH_LONG).show();
                    }
                    else{
                     Place temp=new Place(name,address,Float.parseFloat(latitude),Float.parseFloat(longitude));
                        updatelist(temp);
                        Log.v("Data entered is ", "name:" + name + " address:" + address + " lat:" + latitude + " lng:" + longitude);

                        dismiss();


                    }
                }
            });
            cancelplace_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            // Inflate the layout for this fragment
            return vi;
        }



    }



}



