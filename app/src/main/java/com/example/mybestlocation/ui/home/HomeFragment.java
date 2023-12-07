package com.example.mybestlocation.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog ;
import androidx.core.app.ActivityCompat ;
import androidx.core.content.ContextCompat ;
import androidx.fragment.app.Fragment ;
import androidx.lifecycle.ViewModelProvider ;

import com.example.mybestlocation.JSONParser;
import com.example.mybestlocation.MainActivity;
import com.example.mybestlocation.MapsActivity;
import com.example.mybestlocation.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private EditText longitudeEditText;
    private EditText latitudeEditText;
    private EditText descriptionEditText;
    private Button addButton ,mapButton;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LatLng selectedLocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        longitudeEditText = binding.logtitudeEd;
        latitudeEditText = binding.latitudeEd;
        descriptionEditText = binding.descEd;
        addButton = binding.newBtn;
        mapButton = binding.mapBtn;

        addButton.setOnClickListener(v -> {
            String longitude = longitudeEditText.getText().toString();
            String latitude = latitudeEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            // Perform validation on longitude, latitude, and description as needed.

            // Call an AsyncTask or perform an HTTP request to add the position to the server.
            Log.i("ttt",""+getActivity());
            Ajout addPositionTask = new Ajout(getActivity());
            addPositionTask.execute();
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ouvrez l'activité Maps pour choisir une position
                Intent mapIntent = new Intent(requireContext(), MapsActivity.class);
                startActivityForResult(mapIntent, 1);
            }
        });



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();

                    // Update the EditText fields with real-time location
                    latitudeEditText.setText(String.valueOf(latitude));
                    longitudeEditText.setText(String.valueOf(longitude));
                }
            }
        };

        requestLocationUpdates();

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                double selectedLatitude = data.getDoubleExtra("selectedLatitude", 0.0);
                double selectedLongitude = data.getDoubleExtra("selectedLongitude", 0.0);

                // Mettez à jour les champs de latitude et de longitude avec les valeurs sélectionnées.
                latitudeEditText.setText(String.valueOf(selectedLatitude));
                longitudeEditText.setText(String.valueOf(selectedLongitude));
            }
        }
    }



    class  Ajout extends AsyncTask{
        Context con;
        AlertDialog alert;
        public Ajout(Context con) {
            this.con =con;
        }

        @Override
        protected void onPreExecute() {
            // Show a progress dialog or loading indicator if needed.
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(con);
            dialog.setTitle("Insertion....");
            dialog.setMessage("Veuillez patientez....");
            alert = dialog.create();
            alert.show();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String url = "http://172.20.10.5/servicephp/add_position.php";
            JSONParser jsonParser=new JSONParser();
            HashMap<String,String> parms = new HashMap<String,String>();
            parms.put("longitude",longitudeEditText.getText().toString());
            parms.put("latitude",latitudeEditText.getText().toString());
            parms.put("description",descriptionEditText.getText().toString());
            JSONObject response = jsonParser.makeHttpRequest(url,"GET",parms);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

        }
    }



    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20000); // Update every 10 seconds
        locationRequest.setFastestInterval(30000); // Fastest update interval
        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            Toast.makeText(requireContext(), "Location permission is required.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}