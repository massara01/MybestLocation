package com.example.mybestlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener  {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialisation du fragment de la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    // Mettez en place votre logique pour sélectionner la position
                    // Par exemple, affichez un marqueur sur la carte et attendez que l'utilisateur la sélectionne
                    LatLng selectedLocation = new LatLng(latitude, longitude);
                    // Mettez en place votre logique pour gérer la sélection de la position

                    //pour zoomer
                    float zoomLevel = 10.0f; // Ajustez ce niveau de zoom selon vos besoins
                    LatLng myLocation = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
                }
            }
        };
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocation();
        centerMapOnMyLocation();

        // Configurez votre carte, ajoutez des marqueurs, etc.
        // Vous pouvez ajouter des marqueurs à des emplacements spécifiques ici
        mMap.setOnMapClickListener((GoogleMap.OnMapClickListener) this);
    }

    private void enableMyLocation() {
        if (checkPermission()) {
            //pour pointer sur ma position
            mMap.setMyLocationEnabled(true);
        }}


    //pour centrer sur ma position
    private void centerMapOnMyLocation() {
        if (checkPermission()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng myLocation = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                }
            });
        }}


    // Cette méthode est appelée lorsque l'utilisateur a sélectionné une position sur la carte
    private void onLocationSelected(LatLng selectedLocation) {
        // Envoyez les coordonnées sélectionnées à l'activité appelante (HomeFragment)
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedLocation", selectedLocation);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (checkPermission()) {
            fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
        } else {
            // Demandez la permission de localisation si elle n'a pas déjà été accordée
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20000); // Mettez à jour toutes les 10 secondes
        locationRequest.setFastestInterval(30000); // Intervalle de mise à jour le plus rapide
        return locationRequest;
    }

    private boolean checkPermission() {
        // Vérifiez si la permission de localisation est accordée
        // Si la permission n'est pas accordée, demandez-la à l'utilisateur
        return true; // Remplacez par votre propre logique de vérification de permission
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedLatitude", latLng.latitude);
        resultIntent.putExtra("selectedLongitude", latLng.longitude);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }
}