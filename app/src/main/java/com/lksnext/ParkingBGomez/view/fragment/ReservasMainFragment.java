package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.ParkingBGomez.databinding.FragmentReservasMainBinding;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.view.adapter.ReservasByDayAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaItemDecoration;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservasMainFragment extends Fragment{

    private FragmentReservasMainBinding binding;
    private MainViewModel mainViewModel;
    private Map<LocalDate, List<Reserva>> reservasByDay;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentReservasMainBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        reservasByDay = mainViewModel.getReservasByDay().getValue();

        RecyclerView recyclerView = binding.recyclerViewReservas;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL,false));

        mainViewModel.getReservasByDay().observe(getViewLifecycleOwner(), newReservasByDay ->
            reservasByDay.putAll(newReservasByDay)
        );

        final Reserva reserva = new Reserva(null, null, -1L, null, null);

        // Get the current date
        LocalDate today = LocalDate.now();

        // Add a null reserva to all days of the past month to the map
        LocalDate date = today.withDayOfMonth(1);
        while (date.isBefore(today) || date.isEqual(today)) {
            reservasByDay.putIfAbsent(date, new ArrayList<>(List.of(reserva)));
            date = date.plusDays(1);
        }

        ReservasByDayAdapter adapter = new ReservasByDayAdapter(reservasByDay);

        recyclerView.addItemDecoration(new ReservaItemDecoration(20));
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
