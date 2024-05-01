package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarConfirmBinding;


public class ReservarConfirm extends Fragment {

    FragmentReservarConfirmBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReservarConfirmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbarReservarConfirm.setNavigationOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_reservarConfirm_to_reservarMainFragment));
    }
}