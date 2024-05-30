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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
                getReservasOfUserAfterToday(dataRepository, activity);

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

    private static LiveData<List<Reserva>> getReservasOfUserAfterToday(DataRepository dataRepository, MainActivity activity) {
        return dataRepository.getReservasOfUserAfterToday("usuario", activity, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("ReservaCardAdapter", "Reservas of user after today fetched from db");
            }

            @Override
            public void onFailure() {
                Log.d("ReservaCardAdapter", "Failure :(");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
