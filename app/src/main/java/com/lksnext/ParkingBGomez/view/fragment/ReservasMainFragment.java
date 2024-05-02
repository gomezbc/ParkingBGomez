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
import com.lksnext.ParkingBGomez.view.adapter.ReservasAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaItemDecoration;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

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

        ReservasAdapter adapter = new ReservasAdapter();
        mainViewModel.getReservasList().observe(getViewLifecycleOwner(), adapter::submitList);
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
