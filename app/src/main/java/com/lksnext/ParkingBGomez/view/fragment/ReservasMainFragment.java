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
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Plaza;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.view.adapter.ReservasAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaItemDecoration;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservasMainFragment extends Fragment{

    private FragmentReservasMainBinding binding;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentReservasMainBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = binding.recyclerViewReservas;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL,false));

        List<Reserva> reservas = List.of(
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1))),
                new Reserva(LocalDateTime.now(), "usuario", 1L, new Plaza(1L, TipoPlaza.ESTANDAR), new Hora(LocalTime.now(), LocalTime.now().plusHours(1)))
        );
        mainViewModel.setReservasList(reservas);
        ReservasAdapter adapter = new ReservasAdapter();
        mainViewModel.getReservasList().observe(getViewLifecycleOwner(), adapter::submitList);
        recyclerView.addItemDecoration(new ReservaItemDecoration(20));
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
