package com.example.prusak.happyhourtrail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prusak.happyhourtrail.models.Beer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prusak on 2018-01-07.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener {


    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    protected static Location mLastLocation;
    static Marker m;
    static boolean isAccepted = false;
    static public Map<String, String> pubs= new HashMap<>();
    static public Map<String, ArrayList<Beer>> pubBeers= new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("mapa", ("jestesmy w mapie"));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        getPubLocactions();
        getPubMenu();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (mMap != null) {
            Geocoder coder = new Geocoder(this);
            List<Address> address;
            for (Map.Entry<String, String> entry : pubs.entrySet()){

                try {
                    address = coder.getFromLocationName(entry.getValue(),5);
                    if (address!=null) {
                        Address location=address.get(0);
                        MarkerOptions a = new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title(entry.getKey())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.beer));

                        mMap.addMarker(a);
                        Log.w("mapa", "lokacja ok : " + entry.getValue());

                    }else {
                        Log.w("mapa", "problem z lokacja : " + entry.getValue());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker arg0) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(MapActivity.this,
                            Popup.class);
                    i.putExtra("nazwa", arg0.getTitle());
                    startActivity(i);
                    //here pass your data in intent

                }
            });

                mMap.setMyLocationEnabled(true);

//            if (mLastLocation != null) {
//                MarkerOptions a = new MarkerOptions()
//                        .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//                m = mMap.addMarker(a);
//
//            }

//
//            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//
//                @Override
//                public void onMyLocationChange(Location arg0) {
//                    // TODO Auto-generated method stub
//
////                    mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
//                    Log.i("marker", ("marker to") + m);
//                    m.setPosition(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
////                    if(marker!=null){
////                        marker.setPosition(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
////                        animateMarker(marker,new LatLng(arg0.getLatitude(), arg0.getLongitude()),false);
////                    }
//                  //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 15.5f));
//
//
//                }
//            });

        }
    }

    public boolean checkLocationPermission() {


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                isAccepted = true;
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return isAccepted;
        } else {
            return true;
        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (checkLocationPermission()) {
            Log.i("sprawdzam uprawnienia", "start permission checked");

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                Log.i(" lokacja", "mam lokacje");

            }


            Log.i("map", "start checking location " + mLastLocation);

            if (mLastLocation != null) {
                Log.i("map", "start location not null");
                if (mMap != null) {
                    Log.i("map", "moja lokacja przetwarzaj");
//                    MarkerOptions a = new MarkerOptions()
//                            .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//                    m = mMap.addMarker(a);
//                    m.setPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

//                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("It's Me!"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15.5f));

                }
//                startIntentService();

            }
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void getPubLocactions(){


        mDatabase.child("pubs").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        pubs.put(String.valueOf(dsp.child("nazwa").getValue(String.class)),String.valueOf(dsp.child("lokalizacja").getValue(String.class)));

                }
                Log.i("mapa", " pubs locations"+ pubs.toString() );

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs location", databaseError.toException());
            }
        });

        Log.i("mapa", " pubs locations"+ pubs.toString() );


    }
    public void getPubMenu(){


        mDatabase.child("pubs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        ArrayList<Beer> beers = new ArrayList<>();
                        for(DataSnapshot beerDSP : dsp.child("beers").getChildren()){
                            beers.add(beerDSP.getValue(Beer.class));

                        }
                            pubBeers.put(String.valueOf(dsp.child("nazwa").getValue(String.class)), beers);
                }
                Log.i("mapa", " pubs beers"+ pubBeers.toString() );

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("mapa", "onCancelled pubs beers", databaseError.toException());
            }
        });

//        Log.i("mapa", " pubs beers"+ pubBeers.toString() );


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker!=m){

        }



        return false;
    }














    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        TextView nameView;
        TextView addressView;
        ImageView imageView;
        private Context context;

        public CustomInfoWindowAdapter(Context context) {
            view = getLayoutInflater().inflate(R.layout.custom_info_window,
                    null);
            this.context = context;
        }

        @Override
        public View getInfoContents(Marker marker) {
//
//            if (MapActivity.this.marker != null
//                    && MapActivity.this.marker.isInfoWindowShown()) {
//                MapActivity.this.marker.hideInfoWindow();
//                MapActivity.this.marker.showInfoWindow();
//            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
//            MainActivity.this.marker = marker;
//

            addressView = (TextView) view.findViewById(R.id.address);
            nameView = (TextView) view.findViewById(R.id.nazwaPubu);
            imageView = (ImageView) view.findViewById(R.id.logo);
                    imageView.setImageDrawable(getDrawable(R.drawable.beer));
            String address;
            for (Map.Entry<String, String> entry : pubs.entrySet()) {
                if (marker.getTitle().equals(entry.getKey())) {
                    address = entry.getValue();
                    addressView.setText(address);
                    nameView.setText(entry.getKey());


                }
            }



            return view;
        }


//                    new ArrayList<Beer>()

//            if (marker.getId() != null && markers != null && markers.size() > 0) {
//                if ( markers.get(marker.getId()) != null &&
//                        markers.get(marker.getId()) != null) {
//                    url = markers.get(marker.getId());
//                }
//            }
//            final ImageView image = ((ImageView) view.findViewById(R.id.badge));

//            if (url != null && !url.equalsIgnoreCase("null")
//                    && !url.equalsIgnoreCase("")) {
//                imageLoader.displayImage(url, image, options,
//                        new SimpleImageLoadingListener() {
//                            @Override
//                            public void onLoadingComplete(String imageUri,
//                                                          View view, Bitmap loadedImage) {
//                                super.onLoadingComplete(imageUri, view,
//                                        loadedImage);
//                                getInfoContents(marker);
//                            }
//                        });
//            } else {
//                image.setImageResource(R.drawable.ic_launcher);
//            }

//            final String title = marker.getTitle();
//            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
//            if (title != null) {
//                titleUi.setText(title);
//            } else {
//                titleUi.setText("");
//            }
//
//            final String snippet = marker.getSnippet();
//            final TextView snippetUi = ((TextView) view
//                    .findViewById(R.id.snippet));
//            if (snippet != null) {
//                snippetUi.setText(snippet);
//            } else {
//                snippetUi.setText("");
//            }


    }

    }






















