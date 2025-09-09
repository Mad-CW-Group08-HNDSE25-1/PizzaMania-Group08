package com.androidapp.pizzamania;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.androidapp.pizzamania.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText txtSearch;
    private Button btnSearch;
    private LatLng searchedLatLang;
    private int locationPermissionId;
    private LatLng currentLatLang;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        txtSearch = findViewById(R.id.search_bar);
        btnSearch = findViewById(R.id.btnConfirm);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        txtSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if ((actionId == EditorInfo.IME_ACTION_SEARCH) || (keyEvent != null) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchLocation();
                return true;
            }

            return false;
        });

        btnSearch.setOnClickListener(v -> {

            if (searchedLatLang == null){
                searchLocation();
            }

            if (searchedLatLang != null) {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLatLang, 15));

                Toast.makeText(this, "Getting Location ...", Toast.LENGTH_SHORT).show();

                new android.os.Handler().postDelayed(() -> {
                    Intent addBranchIntent = new Intent();
                    addBranchIntent.putExtra("lat", searchedLatLang.latitude);
                    addBranchIntent.putExtra("long", searchedLatLang.longitude);
                    setResult(RESULT_OK, addBranchIntent);
                    finish();
                }, 5000);

            } else {
                Toast.makeText(this, "Please Select a Location", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){

            mMap.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(currentLocation -> {
                if (currentLocation != null){

                    currentLatLang = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLatLang).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLang, 10));
                }
            });
        }else {
            getPermission();
        }

    }

    private void searchLocation() {
        String enteredLocation = txtSearch.getText().toString().trim();
        if (enteredLocation.isEmpty()) {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "Location service is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            List<Address> addressList = geocoder.getFromLocationName(enteredLocation + ", Sri Lanka", 1);

            if ((addressList != null) && (!addressList.isEmpty())) {
                Address address = addressList.get(0);
                searchedLatLang = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(searchedLatLang).title(enteredLocation));

                LatLngBounds bounds = new LatLngBounds.Builder().include(currentLatLang).include(searchedLatLang).build();

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 2000, null);
                currentLatLang = searchedLatLang;
            } else {
                Toast.makeText(this, "Invalid Location", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
            Toast.makeText(this, "Error Finding Location", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        }, locationPermissionId);
    }
}