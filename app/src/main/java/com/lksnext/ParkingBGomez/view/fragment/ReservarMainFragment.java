package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarMainBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.HourItem;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.view.HourAdapter;
import com.lksnext.ParkingBGomez.view.HourItemDecoration;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

public class ReservarMainFragment extends Fragment{

    private FragmentReservarMainBinding binding;
    private MainViewModel mainViewModel;
    private final List<Integer> dayChipId = new ArrayList<>(Arrays.asList(R.id.day1, R.id.day2,
            R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7));
    private final List<Integer> dayTextId = new ArrayList<>(Arrays.asList(R.id.textView_day1,
            R.id.textView_day2, R.id.textView_day3, R.id.textView_day4, R.id.textView_day5,
            R.id.textView_day6, R.id.textView_day7));
    private final List<Integer> tipoPlazaChipId = new ArrayList<>(Arrays.asList(R.id.chip_car,
            R.id.chip_electric_car, R.id.chip_motorcycle, R.id.chip_accessible_car));

    private static final String LOG_TAG = "ReservarMainFragment";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentReservarMainBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.buttonReservarContinue.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_reservarMainFragment_to_reservarConfirm);
                });

        if (mainViewModel.getReservarState().getValue() == ReservarState.MODIFICAR) {
            mainViewModel.setSelectedHour(null);
            mainViewModel.setReservarState(ReservarState.RESERVAR);
        }

        restoreSelectedDateDayChip();
        restoreSelectedTipoPlaza();

        setDayChip();
        setDayText();

        setTipoPlazaChipsListener();

        loadAvailableHours();

        return binding.getRoot();
    }

    private void loadAvailableHours() {
        DataRepository dataRepository = DataRepository.getInstance();

        TipoPlaza tipoPlaza = mainViewModel.getSelectedTipoPlaza().getValue();
        LocalDate selectedDate = mainViewModel.getSelectedDate().getValue();

        LiveData<List<HourItem>> listLiveData = dataRepository.getAvailableHoursForDateAndTipoPlaza(selectedDate, tipoPlaza, getViewLifecycleOwner(), new Callback() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG, "loadAvailableHours: onSuccess");
            }

            @Override
            public void onFailure() {
                Snackbar.make(binding.getRoot(), "Error al cargar los horarios. Intentalo de nuevo más tarde", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        listLiveData.observe(getViewLifecycleOwner(), hours -> {
            if (binding != null) {
                RecyclerView recyclerView = binding.recyclerView;
                recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 5)); // 5 columns
                HourAdapter adapter = new HourAdapter(hours, mainViewModel);
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new HourItemDecoration(20, 5));
                binding.buttonReservarContinue.setEnabled(false);
                binding.progressIndicatorHorarios.hide();
                binding.progressIndicatorHorarios.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                // If the layout is reset and the user had selected an hour, enable the button
                if (mainViewModel.getSelectedHour().getValue() != null){
                    binding.buttonReservarContinue.setEnabled(true);
                }
            }
            listLiveData.removeObservers(getViewLifecycleOwner());
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ReservarState state = mainViewModel.getReservarState().getValue();
        if (state == ReservarState.RESERVADO){
            Snackbar.make(view, "Reserva realizada", BaseTransientBottomBar.LENGTH_LONG)
                    .setAction("Ver", v -> {
                                NavController navController = Navigation.findNavController(v);
                                navController.navigate(
                                        R.id.action_reservarMainFragment_to_reservasMainFragment);
                            })
                    .show();

            mainViewModel.setReservarState(ReservarState.RESERVAR);
        }

        mainViewModel.getSelectedHour().observe(getViewLifecycleOwner(),hour ->
            // This observer is used to enable the continue button when an hour is selected
            // Its lifecycle must be the same as the fragment
            binding.buttonReservarContinue.setEnabled(hour != null)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setDayChip(){
        setDayChipText();
    }

    /**
     * Set the text of the day chips in the date picker
     * The text is the day of the month
     */
    private void setDayChipText(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        for (int chip_id: dayChipId
             ) {
            final Chip chip = binding.datePicker.findViewById(chip_id);
            chip.setText(String.valueOf(day));
            chip.setTag(day); // Set the value to the chip
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            // Add a check change listener to handle the selection
            setDayChipListener(chip);
        }
    }

    private void setDayChipListener(Chip chip){
        chip.setOnClickListener(v -> {
            final Chip  clickedChip = (Chip) v;
            // Update LiveData values
            mainViewModel.setSelectedDateDay((Integer) clickedChip.getTag());
            mainViewModel.setSelectedDateDayChip(clickedChip.getId());

            // Reset the selected hour
            mainViewModel.setSelectedHour(null);

            // Disable the continue button until an hour is selected
            binding.buttonReservarContinue.setEnabled(false);

            DataRepository dataRepository = DataRepository.getInstance();
            TipoPlaza tipoPlaza = mainViewModel.getSelectedTipoPlaza().getValue();
            LocalDate selectedDate = mainViewModel.getSelectedDate().getValue();
            reloadAvailableHoursForDateAndTipoPlaza(dataRepository, selectedDate, tipoPlaza);
        });
    }

    /**
     * Set the text of the day text views in the date picker
     * The text is the day of the month
     */
    private void setDayText(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
        for (int text_id: dayTextId
             ) {
            final TextView text = binding.datePicker.findViewById(text_id);
            String dayOfWeek = calendar.getDisplayName(
                    Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            assert dayOfWeek != null;
            // Set the text to the first letter of the day of the week
            text.setText(dayOfWeek.substring(0, 1).toUpperCase());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void restoreSelectedDateDayChip() {
        // Set the initial selected date day chip
        if (mainViewModel.getSelectedDateDayChip().getValue() == null){
            mainViewModel.setSelectedDateDayChip(R.id.day1);
        }
        final Chip selectedChip = binding.datePicker.findViewById(mainViewModel.getSelectedDateDayChip().getValue());
        selectedChip.setChecked(true);
    }

    private void restoreSelectedTipoPlaza() {
        final TipoPlaza tipoPlaza = mainViewModel.getSelectedTipoPlaza().getValue();
        switch (Objects.requireNonNull(tipoPlaza)){
            case ESTANDAR:
                binding.chipCar.setChecked(true);
                break;
            case ELECTRICO:
                binding.chipElectricCar.setChecked(true);
                break;
            case MOTO:
                binding.chipMotorcycle.setChecked(true);
                break;
            case DISCAPACITADO:
                binding.chipAccessibleCar.setChecked(true);
                break;
        }
    }

    private void setTipoPlazaChipsListener() {
        DataRepository dataRepository = DataRepository.getInstance();

        tipoPlazaChipId.forEach(chipId -> {
            final Chip chip = binding.chipGroup.findViewById(chipId);
            chip.setOnClickListener(v -> {
                // Disable the continue button until an hour is selected
                binding.buttonReservarContinue.setEnabled(false);

                // Reset the selected hour
                mainViewModel.setSelectedHour(null);

                LocalDate selectedDate = mainViewModel.getSelectedDate().getValue();
                final Chip clickedChip = (Chip) v;
                final int id = clickedChip.getId();
                if (id == R.id.chip_car) {
                    mainViewModel.setSelectedTipoPlaza(TipoPlaza.ESTANDAR);
                } else if (id == R.id.chip_electric_car) {
                    mainViewModel.setSelectedTipoPlaza(TipoPlaza.ELECTRICO);
                } else if (id == R.id.chip_motorcycle) {
                    mainViewModel.setSelectedTipoPlaza(TipoPlaza.MOTO);
                } else if (id == R.id.chip_accessible_car) {
                    mainViewModel.setSelectedTipoPlaza(TipoPlaza.DISCAPACITADO);
                }
                TipoPlaza tipoPlaza = mainViewModel.getSelectedTipoPlaza().getValue();
                reloadAvailableHoursForDateAndTipoPlaza(dataRepository, selectedDate, tipoPlaza);
            });
        });
    }

    private void reloadAvailableHoursForDateAndTipoPlaza(DataRepository dataRepository, LocalDate date, TipoPlaza tipoPlaza) {
        Log.d(LOG_TAG, "reloadAvailableHoursForDateAndTipoPlaza: " + date + " " + tipoPlaza);
        binding.progressIndicatorHorarios.show();
        binding.progressIndicatorHorarios.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        LiveData<List<HourItem>> listLiveData = dataRepository.getAvailableHoursForDateAndTipoPlaza(date, tipoPlaza, getViewLifecycleOwner(), new Callback() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG, "reloadAvailableHoursForDateAndTipoPlaza: onSuccess");
            }

            @Override
            public void onFailure() {
                binding.progressIndicatorHorarios.hide();
                Snackbar.make(binding.getRoot(), "Error al cargar los horarios. Intentalo de nuevo más tarde", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });

        listLiveData.observe(getViewLifecycleOwner(), hours -> {
            if (binding != null) {
                HourAdapter adapter = (HourAdapter) binding.recyclerView.getAdapter();
                if (adapter != null){
                    adapter.updateData(hours);
                    adapter.notifyItemRangeChanged(0, hours.size());
                    binding.progressIndicatorHorarios.hide();
                    binding.progressIndicatorHorarios.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
            }
            listLiveData.removeObservers(getViewLifecycleOwner());
        });
    }
}
