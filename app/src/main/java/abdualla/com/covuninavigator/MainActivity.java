package abdualla.com.covuninavigator;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

import abdulla.com.routing.Route;
import abdulla.com.routing.RouteException;
import abdulla.com.routing.Routing;
import abdulla.com.routing.RoutingListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,RoutingListener {
    private static GoogleMap map;
    Button update;
    String searchValue;
    private GoogleApiClient mGoogleapi;
    private static Place source_place;
    private static Place dest_place;
    private MyContentProvider cp = new MyContentProvider();

    boolean fact=false;
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("source", source_place);
        outState.putParcelable("dest", dest_place);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Place> places=new ArrayList<>();
        places= cp.getAll();

        mGoogleapi = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton direcitonsButton=(FloatingActionButton)findViewById(R.id.directions);
        direcitonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getFragmentManager();
                directions getdirections = new directions();
                getdirections.show(fm, "Get Directions");
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            Bundle bundle = getIntent().getBundleExtra("bundle");
            Float lat = bundle.getFloat("lat");
            Float lng = bundle.getFloat("lng");
            String name = bundle.getString("name");
            map.clear();
            Marker newmarker = map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .title(name));
            Log.v("I am marking", "marker yes");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No Places Selected", Toast.LENGTH_LONG).show();
        }

            if (map != null) {
                Marker coventry = map.addMarker(new MarkerOptions().position(new LatLng(52.4081, -1.5106))
                        .title("Coventry"));
                 map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.4081, -1.5106), 14.0f));
                        Iterator itr = places.iterator();
                        while (itr.hasNext()) {
                            Place temp = (Place) itr.next();
                            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(temp.getLat(), temp.getLng()))
                                    .title(temp.getName()));
                        }


                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location mylocation = locationManager.getLastKnownLocation(provider);
                try {
                    LatLng myloc_latlng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                    Location mloc=new Location(String.valueOf(new LatLng(52.4081, -1.5106)));

                            float radius=500;
                    float distance=mylocation.distanceTo(mloc);
                    if(distance>radius){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.4081, -1.5106), 14.0f));
                    }else {
                        map.moveCamera(CameraUpdateFactory.newLatLng(myloc_latlng));
                    }
                    map.animateCamera(CameraUpdateFactory.zoomTo(14));
                    map.addMarker(new MarkerOptions().position(new LatLng(mylocation.getLatitude(), mylocation.getLongitude())).title("You are here!").snippet("Consider yourself located"));

                } catch (Exception ex) {

                }

            }


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private static void updatemarkers(Place source, Place dest){
        map.clear();
        Marker sourcemark= map.addMarker(new MarkerOptions().position(new LatLng(source.getLat(),source.getLng())).title("You are here!").snippet("Consider yourself located"));
        Marker destmark= map.addMarker(new MarkerOptions().position(new LatLng(dest.getLat(), dest.getLng())).title("Destination").snippet("That's Your Endpoint"));

        Routing mRoute=new Routing.Builder().travelMode().withListener(new MainActivity()).waypoints(
                (new LatLng(source.getLat(),source.getLng())), (new LatLng(dest.getLat(),dest.getLng()))).build();
        mRoute.execute();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(source.getLat(), source.getLng()), 14.0f));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onConnected(Bundle bundle) {
    super.onStart();
        mGoogleapi.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleapi.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Connection Not Possible",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        Route shrtRoute= route.get(shortestRouteIndex);
        map.addPolyline(shrtRoute.getPolyOptions().geodesic(true));
//        Toast.makeText(getBaseContext(),"Route is Added",Toast.LENGTH_LONG).show();
        }

    @Override
    public void onRoutingCancelled() {

    }

    public static class directions extends DialogFragment {
        ArrayList<Place> list;

        public directions() {
            // Required empty public constructor
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            MyContentProvider cp=new MyContentProvider();
            setRetainInstance(true);
            list=cp.getAll();
            final MyAdapter adapter=new MyAdapter(getActivity(), list);
            View view=inflater.inflate(R.layout.fragment_directions, container, false);
            final AutoCompleteTextView source=(AutoCompleteTextView)view.findViewById(R.id.sourceet);
            final AutoCompleteTextView destination=(AutoCompleteTextView)view.findViewById(R.id.destet);
            source.setAdapter(adapter);
            destination.setAdapter(adapter);
            Button getbutton=(Button)view.findViewById(R.id.getButton);
            Button cancelbutton=(Button)view.findViewById(R.id.cancelButton);

            source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        source_place = (Place) adapter.getItem(position);
//                        source_place = list.get(source.getListSelection());

                        source.setText(source_place.getName());
                    } catch (Exception e) {
                        Log.v("Source Input", "Early click");
                    }
                }
            });

            destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        dest_place = (Place) adapter.getItem(position);
//                        source_place = list.get(source.getListSelection());

                        destination.setText(dest_place.getName());
                    } catch (Exception e) {
                        Log.v("Source Input", "Early click");
                    }
                }

            });
            getbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Log.v("Name of source",source_place.getName());
                        Log.v("Name of dest", dest_place.getName());
                        updatemarkers(source_place, dest_place);
                        dismiss();
                    }catch (Exception e){
                        Toast.makeText(getActivity(),"Please Check source/destination",Toast.LENGTH_LONG).show();
                    }

                }
            });

            cancelbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return view;
        }


     }

}
