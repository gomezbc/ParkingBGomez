package com.lksnext.ParkingBGomez.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentInicioMainBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.adapter.ReservaCardAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaCardDecoration;

import java.util.List;
import java.util.Locale;

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
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.reservasMainFragment);
        });

        binding.nuevaReservaExtendedFab.setOnClickListener(l -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.reservarMainFragment);
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

        fetchAndSetReservasFromDB(recyclerView);

        fetchAndSetAvailableCarSlots(view);
        fetchAndSetAvailableElectricCarSlots(view);
        fetchAndSetAvailableAccessibleCarSlots(view);
        fetchAndSetMotorCycleSlots(view);
    }

    private void fetchAndSetReservasFromDB(RecyclerView recyclerView) {
        LiveData<List<Reserva>> liveData =
                getActiveReservasOfUser(recyclerView);

        // Is executed when the LiveData object is updated with db data
        liveData.observe(getViewLifecycleOwner(), dbReservasOfUser -> {
            if (dbReservasOfUser != null) {
                setRecyclerViewWithReservasFromDB(recyclerView, dbReservasOfUser);
            }
            liveData.removeObservers(getViewLifecycleOwner());
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

    private static LiveData<List<Reserva>> getActiveReservasOfUser(View view) {
        return DataRepository.getInstance().getActiveReservasOfUser(DataRepository.getInstance().getCurrentUser().getUid(), new Callback() {
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

    private void fetchAndSetAvailableCarSlots(@NonNull View view) {
        LiveData<Integer> availableCarSlotsNumber = DataRepository.getInstance().getPlazasLibresByTipoPlaza(TipoPlaza.ESTANDAR, TimeUtils.getNowEpoch(), getViewLifecycleOwner(), new Callback() {
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

        LiveData<Integer> totalTipoPlaza = DataRepository.getInstance().getTotalPlazasByTipoPlaza(TipoPlaza.ESTANDAR, new Callback() {
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

        availableCarSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots -> {
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                setAvailableSlotsPercentage(totalSlots, availableCarSlots, binding.carSlotsAvailableIndicator, binding.carSlotsAvailableText);
                totalTipoPlaza.removeObservers(getViewLifecycleOwner());
            });
            availableCarSlotsNumber.removeObservers(getViewLifecycleOwner());
        });

    }

    private void fetchAndSetAvailableElectricCarSlots(@NonNull View view) {
        LiveData<Integer> availableElectricCarSlotsNumber = DataRepository.getInstance().getPlazasLibresByTipoPlaza(TipoPlaza.ELECTRICO, TimeUtils.getNowEpoch(), getViewLifecycleOwner(), new Callback() {
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

        LiveData<Integer>  totalTipoPlaza = DataRepository.getInstance().getTotalPlazasByTipoPlaza(TipoPlaza.ELECTRICO, new Callback() {
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

        availableElectricCarSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots -> {
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                setAvailableSlotsPercentage(totalSlots, availableCarSlots, binding.electricCarSlotsAvailableIndicator, binding.electricCarSlotsAvailableText);
                Log.d(INICIO_MAIN_FRAGMENT, "Total plazas for electric car fetched from db " + totalSlots);
                totalTipoPlaza.removeObservers(getViewLifecycleOwner());
            });
            availableElectricCarSlotsNumber.removeObservers(getViewLifecycleOwner());
        });
    }

    private void fetchAndSetAvailableAccessibleCarSlots(@NonNull View view) {
        LiveData<Integer> availableAccessibleCarSlotsNumber = DataRepository.getInstance().getPlazasLibresByTipoPlaza(TipoPlaza.DISCAPACITADO, TimeUtils.getNowEpoch(), getViewLifecycleOwner(), new Callback() {
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

        LiveData<Integer>  totalTipoPlaza = DataRepository.getInstance().getTotalPlazasByTipoPlaza(TipoPlaza.DISCAPACITADO, new Callback() {
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

        availableAccessibleCarSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots -> {
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                setAvailableSlotsPercentage(totalSlots, availableCarSlots, binding.accessibleCarSlotsAvailableIndicator, binding.accessibleCarSlotsAvailableText);
                availableAccessibleCarSlotsNumber.removeObservers(getViewLifecycleOwner());
            });
            totalTipoPlaza.removeObservers(getViewLifecycleOwner());
        });
    }

    private void fetchAndSetMotorCycleSlots(@NonNull View view) {
        LiveData<Integer> availableMotorCycleSlotsNumber = DataRepository.getInstance().getPlazasLibresByTipoPlaza(TipoPlaza.MOTO, TimeUtils.getNowEpoch(), getViewLifecycleOwner(), new Callback() {
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

        LiveData<Integer>  totalTipoPlaza = DataRepository.getInstance().getTotalPlazasByTipoPlaza(TipoPlaza.MOTO, new Callback() {
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

        availableMotorCycleSlotsNumber.observe(getViewLifecycleOwner(), availableCarSlots -> {
            totalTipoPlaza.observe(getViewLifecycleOwner(), totalSlots -> {
                setAvailableSlotsPercentage(totalSlots, availableCarSlots, binding.motorcycleSlotsAvailableIndicator, binding.motorcycleSlotsAvailableText);
                totalTipoPlaza.removeObservers(getViewLifecycleOwner());
            });
            availableMotorCycleSlotsNumber.removeObservers(getViewLifecycleOwner());
        });
    }

    private void setAvailableSlotsPercentage(Integer totalSlots, Integer availableCarSlots,
                                             CircularProgressIndicator circularProgressIndicator, TextView textView) {
        if (totalSlots != 0) {
            int availableCarSlotsPercentage;
            if (availableCarSlots.equals(totalSlots)) {
                availableCarSlotsPercentage = 100;
            } else {
                availableCarSlotsPercentage = (availableCarSlots * 100 / totalSlots * 100) / 100;
            }
            circularProgressIndicator.setProgressCompat(availableCarSlotsPercentage, true);
            textView.setText(String.format(Locale.getDefault(), "%s / %s", availableCarSlots, totalSlots));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
