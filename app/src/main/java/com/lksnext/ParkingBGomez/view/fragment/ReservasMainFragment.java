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
import java.util.HashMap;
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
        reservasByDay = new HashMap<>();

        RecyclerView recyclerView = binding.recyclerViewReservas;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL,false));

        mainViewModel.getReservasList().observe(getViewLifecycleOwner(), reservas -> {
            for (Reserva reserva : reservas) {
                LocalDate localDate = reserva.fecha().toLocalDate();
                if (!reservasByDay.containsKey(localDate)) {
                    reservasByDay.put(localDate, List.of(reserva));
                } else {
                    List<Reserva> reservasForDate = new ArrayList<>(reservasByDay.get(localDate));
                    reservasForDate.add(reserva);
                    reservasByDay.put(localDate, reservasForDate);
                }
            }

        });
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
