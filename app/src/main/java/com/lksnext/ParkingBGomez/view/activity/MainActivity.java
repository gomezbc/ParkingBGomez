package com.lksnext.ParkingBGomez.view.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.ParkingBGomez.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();

        setContentView(binding.getRoot());

    }

    @NonNull
    public FirebaseFirestore getDb() {
        return db;
    }
}