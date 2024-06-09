package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentReservasMainBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;
import com.lksnext.ParkingBGomez.view.adapter.ReservasByDayAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaItemDecoration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReservasMainFragment extends Fragment{

    private FragmentReservasMainBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentReservasMainBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.recyclerViewReservas;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL,false));

        fetchAndSetReservasFromDB(recyclerView, binding.progressBarReservas);
    }

    private void fetchAndSetReservasFromDB(RecyclerView recyclerView, LinearProgressIndicator progressBar) {
        MainActivity activity = (MainActivity) getActivity();

        DataRepository dataRepository = DataRepository.getInstance();

        if (activity != null) {
            LiveData<Map<LocalDate, List<Reserva>>> liveData =
                    getReservasByDayByUser(dataRepository, activity);

            // Is executed when the LiveData object is updated with db data
            liveData.observe(getViewLifecycleOwner(), dbReservasByDay -> {
                progressBar.hide();
                if (dbReservasByDay != null) {
                    setReservasFromDB(recyclerView, dbReservasByDay, activity);
                }
            });
        }
    }

    private void setReservasFromDB(RecyclerView recyclerView, Map<LocalDate,
            List<Reserva>> dbReservasByDay, MainActivity activity) {

        // Get the current date
        LocalDate today = LocalDate.now();

        // Add a null reserva to all days of the past month to the map
        LocalDate date = today.withDayOfMonth(1);
        while (date.isBefore(today) || date.isEqual(today)) {
            dbReservasByDay.putIfAbsent(date, new ArrayList<>());
            date = date.plusDays(1);
        }

        // Order by date
        dbReservasByDay = new TreeMap<>(dbReservasByDay).descendingMap();

        ReservasByDayAdapter adapter = new ReservasByDayAdapter(dbReservasByDay, activity.getSupportFragmentManager());

        recyclerView.addItemDecoration(new ReservaItemDecoration(10));
        recyclerView.setAdapter(adapter);
    }

    private static LiveData<Map<LocalDate, List<Reserva>>>
    getReservasByDayByUser(DataRepository dataRepository, MainActivity activity) {
        // Return the LiveData object with the reservas and handle the success and failure cases
        return dataRepository.getReservasByDayByUser("usuario", activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("getReservasByDayByUser", "Success.");
            }

            @Override
            public void onFailure() {
                Log.d("getReservasByDayByUser", "Error getting documents.");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
