package com.lksnext.ParkingBGomez.view.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lksnext.ParkingBGomez.R;

public class MapsFragment extends Fragment {

    private final OnMapReadyCallback callback = googleMap -> {
        LatLng sanSebastian = new LatLng(43.3124, -1.9839); // Coordinates for San Sebastian
        float zoomLevel = 12.0f; // Set desired zoom level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanSebastian, zoomLevel));

        LatLng parqueEmpresarialZuatzu = new LatLng(43.2980445, -2.0072874); // Coordinates for Parque Empresarial de Zuatzu
        googleMap.addMarker(new MarkerOptions().position(parqueEmpresarialZuatzu).title("Parking del Parque Empresarial de Zuatzu"));
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }
}