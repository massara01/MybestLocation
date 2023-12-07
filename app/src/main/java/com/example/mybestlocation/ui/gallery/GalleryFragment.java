package com.example.mybestlocation.ui.gallery;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mybestlocation.JSONParser;
import com.example.mybestlocation.Position;
import com.example.mybestlocation.R;
import com.example.mybestlocation.databinding.FragmentGalleryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    ArrayList<Position> data = new ArrayList<Position>();

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        binding.loadBtn.setOnClickListener(v ->{
            //invocation httpConnection -> getall.php
            Telechargement t = new Telechargement(getActivity());
            t.execute();
        });
        return root;
    }


    class Telechargement extends AsyncTask
    {
        Context con ;
        AlertDialog alert ;
        Telechargement (Context con){
            this.con=con ;
        }
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder dialog = new AlertDialog.Builder(con);
            dialog.setTitle("Télechargement....");
            dialog.setMessage("Veuillez patientez....");
            alert=dialog.create();
            alert.show();
            //UIT
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //telechargement
            /**
             * ken mamchetich l 'ip heki 5ademha b 10.0.2.2ok?
             *
             */
            String url="http://172.20.10.5/servicephp/get_all_user.php";

            JSONObject response = JSONParser.makeRequest(url);
            try {
                int success = response.getInt("success");
                if(success==0){
                    String msg=response.getString("message");
                }else{
                    JSONArray tableau = response.getJSONArray("UnePosition");
                    for (int i=0;i<tableau.length();i++){
                        JSONObject ligne = tableau.getJSONObject(i);
                        int id = ligne.getInt("id");
                        String longitude = ligne.getString("longitude");
                        String latitude = ligne.getString("latitude");
                        String description = ligne.getString("description");

                        data.add(new Position(id , longitude,latitude,description));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //second : n'a pas access à l'ihm
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            ArrayAdapter ad = new ArrayAdapter(con , android.R.layout.simple_list_item_1,data);
            binding.favorisLv.setAdapter(ad);
            alert.dismiss();
            //UIT
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}