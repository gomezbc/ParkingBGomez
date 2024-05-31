package com.lksnext.ParkingBGomez.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentInicioMainBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;
import com.lksnext.ParkingBGomez.view.adapter.ReservaCardAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaCardDecoration;

import java.util.List;

public class InicioMainFragment extends Fragment{

    private FragmentInicioMainBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentInicioMainBinding.inflate(inflater, container, false);

        binding.verTodasReservas.setOnClickListener(l -> {
            NavController navController = Navigation.findNavController(binding.getRoot());
            navController.navigate(R.id.action_to_reservas_by_ver_todas_text);
        });

        binding.buttonZuatzuMaps.setOnClickListener(l -> {
            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse("https://plus.codes/7XWV+PX,San%20Sebastian");

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent);


        });

        return binding.getRoot();

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = this.binding.reservasCardRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL,false));
        recyclerView.addItemDecoration(new ReservaCardDecoration(16));

        MainActivity activity = (MainActivity) getActivity();

        DataRepository dataRepository = DataRepository.getInstance();

        if (activity != null) {
            fetchAndSetReservasFromDB(recyclerView, dataRepository, activity);
        }
    }

    private void fetchAndSetReservasFromDB(RecyclerView recyclerView, DataRepository dataRepository, MainActivity activity) {
        LiveData<List<Reserva>> liveData =
                getReservasOfUserAfterToday(recyclerView, dataRepository, activity);

        // Is executed when the LiveData object is updated with db data
        liveData.observe(getViewLifecycleOwner(), dbReservasOfUser -> {
            if (dbReservasOfUser != null) {
                setRecyclerViewWithReservasFromDB(recyclerView, dbReservasOfUser);
            }
        });
    }

    private void setRecyclerViewWithReservasFromDB(RecyclerView recyclerView, List<Reserva> dbReservasOfUser) {
        ReservaCardAdapter adapter = new ReservaCardAdapter();
        recyclerView.setAdapter(adapter);
        adapter.submitList(dbReservasOfUser);
    }

    private static LiveData<List<Reserva>> getReservasOfUserAfterToday(View view, DataRepository dataRepository, MainActivity activity) {
        return dataRepository.getReservasOfUserAfterToday("usuario", activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("ReservaCardAdapter", "Reservas of user after today fetched from db");
            }

            @Override
            public void onFailure() {
                Log.d("ReservaCardAdapter", "Failure :(");
                Snackbar.make(view,
                        "La aplicación no ha podido cargar tus proximas reservas. Intentalo de nuevo más tarde.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
