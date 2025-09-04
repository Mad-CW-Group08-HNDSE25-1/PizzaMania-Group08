package com.androidapp.pizzamania;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        txtSearch = findViewById(R.id.search_bar);
        btnSearch = findViewById(R.id.btnConfirm);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null){
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
            if (searchedLatLang != null){
                Intent addBranchIntent = new Intent();
                addBranchIntent.putExtra("lat", searchedLatLang.latitude);
                addBranchIntent.putExtra("long", searchedLatLang.longitude);
                setResult(RESULT_OK, addBranchIntent);
                finish();
            }else{
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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng colombo = new LatLng(6.9271, 79.8612);
        mMap.addMarker(new MarkerOptions().position(colombo).title("Marker in colombo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, 10));

    }

    private void searchLocation(){
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLatLang, 15));
            }else {
                Toast.makeText(this, "Invalid Location", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
            Toast.makeText(this, "Error Finding Location", Toast.LENGTH_SHORT).show();
        }
    }
}