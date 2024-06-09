package com.lksnext.ParkingBGomez.view.fragment;

import android.annotation.SuppressLint;
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
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;
import com.lksnext.ParkingBGomez.view.adapter.ReservaCardAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaCardDecoration;

import java.util.List;

public class InicioMainFragment extends Fragment{

    public static final String INICIO_MAIN_FRAGMENT = "InicioMainFragment";
    public static final String PLAZAS_INTENTALO_DE_NUEVO_MAS_TARDE = "La aplicación no ha podido cargar las plazas libres. Intentalo de nuevo más tarde.";
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

        binding.nuevaReservaExtendedFab.setOnClickListener(l -> {
            NavController navController = Navigation.findNavController(binding.getRoot());
            navController.navigate(R.id.action_inicioMainFragment_to_reservarMainFragment);
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



    @SuppressLint("SetTextI18n")
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

            fetchAndSetAvailableCarSlots(view, dataRepository, activity);
            fetchAndSetAvailableElectricCarSlots(view, dataRepository, activity);
            fetchAndSetAvailableAccessibleCarSlots(view, dataRepository, activity);
            fetchAndSetMotorCycleSlots(view, dataRepository, activity);
        }
    }

    private void fetchAndSetReservasFromDB(RecyclerView recyclerView, DataRepository dataRepository, MainActivity activity) {
        LiveData<List<Reserva>> liveData =
                getActiveReservasOfUser(recyclerView, dataRepository, activity);

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
        if (dbReservasOfUser.isEmpty()) {
            binding.fallbackCard.findViewById(R.id.progressIndicator).setVisibility(View.GONE);
            binding.fallbackCard.findViewById(R.id.fallbackText).setVisibility(View.VISIBLE);
        }else {
            binding.fallbackCard.setVisibility(View.GONE);
        }
    }

    private static LiveData<List<Reserva>> getActiveReservasOfUser(View view, DataRepository dataRepository, MainActivity activity) {
        return dataRepository.getActiveReservasOfUser("usuario", activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("ReservaCardAdapter", "Reservas of user after today fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        "La aplicación no ha podido cargar tus proximas reservas. Intentalo de nuevo más tarde.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    private void fetchAndSetAvailableCarSlots(@NonNull View view, DataRepository dataRepository, MainActivity activity) {
        LiveData<Integer> availableCarSlotsNumber = dataRepository.getPlazasLibresByTipoPlaza(TipoPlaza.ESTANDAR, TimeUtils.getNowEpoch() , activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Plazas libres for car fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        PLAZAS_INTENTALO_DE_NUEVO_MAS_TARDE,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        LiveData<Integer>  totalTipoPlaza = dataRepository.getTotalPlazasByTipoPlaza(TipoPlaza.ESTANDAR, activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Total plazas for car fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        "La aplicación no ha podido cargar las plazas totales. Intentalo de nuevo más tarde.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        availableCarSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots ->
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                if (totalSlots != 0){
                    int availableCarSlotsPercentage;
                    if (availableCarSlots.equals(totalSlots)){
                        availableCarSlotsPercentage = 100;
                    }else {
                        availableCarSlotsPercentage = (availableCarSlots*100 / totalSlots*100) / 100;
                    }
                    binding.carSlotsAvailableIndicator.setProgressCompat(availableCarSlotsPercentage, true);
                    binding.carSlotsAvailableText.setText(availableCarSlots + " / " + totalSlots);
                }
            }));
    }

    private void fetchAndSetAvailableElectricCarSlots(@NonNull View view, DataRepository dataRepository, MainActivity activity) {
        LiveData<Integer> availableElectricCarSlotsNumber = dataRepository.getPlazasLibresByTipoPlaza(TipoPlaza.ELECTRICO, TimeUtils.getNowEpoch() , activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Plazas libres for electric car fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        PLAZAS_INTENTALO_DE_NUEVO_MAS_TARDE,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        LiveData<Integer>  totalTipoPlaza = dataRepository.getTotalPlazasByTipoPlaza(TipoPlaza.ELECTRICO, activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Total plazas for electric car fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        "La aplicación no ha podido cargar las plazas totales. Intentalo de nuevo más tarde.",
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        availableElectricCarSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots ->
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                if (totalSlots != 0){
                    int availableCarSlotsPercentage;
                    if (availableCarSlots.equals(totalSlots)){
                        availableCarSlotsPercentage = 100;
                    }else {
                        availableCarSlotsPercentage = (availableCarSlots*100 / totalSlots*100) / 100;
                    }
                    Log.d(INICIO_MAIN_FRAGMENT, "Available electric car slots percentage " + availableCarSlotsPercentage);
                    binding.electricCarSlotsAvailableIndicator.setProgressCompat(availableCarSlotsPercentage, true);
                    binding.electricCarSlotsAvailableText.setText(availableCarSlots + " / " + totalSlots);
                }
                Log.d(INICIO_MAIN_FRAGMENT, "Total plazas for electric car fetched from db " + totalSlots);
            }));
    }

    private void fetchAndSetAvailableAccessibleCarSlots(@NonNull View view, DataRepository dataRepository, MainActivity activity) {
        LiveData<Integer> availableAccessibleCarSlotsNumber = dataRepository.getPlazasLibresByTipoPlaza(TipoPlaza.DISCAPACITADO, TimeUtils.getNowEpoch() , activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Plazas libres for accessible car fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        PLAZAS_INTENTALO_DE_NUEVO_MAS_TARDE,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        LiveData<Integer>  totalTipoPlaza = dataRepository.getTotalPlazasByTipoPlaza(TipoPlaza.DISCAPACITADO, activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Total plazas for accessible car fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        PLAZAS_INTENTALO_DE_NUEVO_MAS_TARDE,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        availableAccessibleCarSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots ->
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                if (totalSlots != 0){
                    int availableCarSlotsPercentage;
                    if (availableCarSlots.equals(totalSlots)){
                        availableCarSlotsPercentage = 100;
                    }else {
                        availableCarSlotsPercentage = (availableCarSlots*100 / totalSlots*100) / 100;
                    }
                    binding.accessibleCarSlotsAvailableIndicator.setProgressCompat(availableCarSlotsPercentage, true);
                    binding.accessibleCarSlotsAvailableText.setText(availableCarSlots + " / " + totalSlots);
                }
            }));
    }

    private void fetchAndSetMotorCycleSlots(@NonNull View view, DataRepository dataRepository, MainActivity activity) {
        LiveData<Integer> availableMotorCycleSlotsNumber = dataRepository.getPlazasLibresByTipoPlaza(TipoPlaza.MOTO, TimeUtils.getNowEpoch() , activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Plazas libres for MotorCycle fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        PLAZAS_INTENTALO_DE_NUEVO_MAS_TARDE,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        LiveData<Integer>  totalTipoPlaza = dataRepository.getTotalPlazasByTipoPlaza(TipoPlaza.MOTO, activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(INICIO_MAIN_FRAGMENT, "Total plazas for car fetched from db");
            }

            @Override
            public void onFailure() {
                Snackbar.make(view,
                        PLAZAS_INTENTALO_DE_NUEVO_MAS_TARDE,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        availableMotorCycleSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots ->
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                if (totalSlots != 0){
                    int availableCarSlotsPercentage;
                    if (availableCarSlots.equals(totalSlots)){
                        availableCarSlotsPercentage = 100;
                    }else {
                        availableCarSlotsPercentage = (availableCarSlots*100 / totalSlots*100) / 100;
                    }
                    binding.motorcycleSlotsAvailableIndicator.setProgressCompat(availableCarSlotsPercentage, true);
                    binding.motorcycleSlotsAvailableText.setText(availableCarSlots + " / " + totalSlots);
                }
            }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
