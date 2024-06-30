package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentModifyReservaMainBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.HourItem;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.adapter.HourAdapter;
import com.lksnext.ParkingBGomez.view.decoration.HourItemDecoration;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ModifyReservaMainFragment extends Fragment{

    private FragmentModifyReservaMainBinding binding;
    private MainViewModel mainViewModel;
    private final List<Integer> dayChipId = new ArrayList<>(Arrays.asList(R.id.day1, R.id.day2,
            R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7));
    private final List<Integer> dayTextId = new ArrayList<>(Arrays.asList(R.id.textView_day1,
            R.id.textView_day2, R.id.textView_day3, R.id.textView_day4, R.id.textView_day5,
            R.id.textView_day6, R.id.textView_day7));
    private final List<Integer> tipoPlazaChipId = new ArrayList<>(Arrays.asList(R.id.chip_car,
            R.id.chip_electric_car, R.id.chip_motorcycle, R.id.chip_accessible_car));

    private static final String LOG_TAG = "ModifyReservaMainFragment";
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentModifyReservaMainBinding.inflate(inflater, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mainViewModel.setReservarState(ReservarState.MODIFICAR);

        setDayChip();
        setDayText();
        setTipoPlazaChipsListener();

        String reservaUuid = ModifyReservaMainFragmentArgs.fromBundle(getArguments()).getReservaUuid();

        LiveData<Reserva> reservaLiveData = DataRepository.getInstance().getReservaByUuid(reservaUuid, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG, "onSuccess: getReserva");
            }

            @Override
            public void onFailure() {
                Toast.makeText(binding.getRoot().getContext(), R.string.error_loading_reservas, Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonReservarContinue.setOnClickListener(v -> {
            ModifyReservaMainFragmentDirections.ActionModifyReservaMainFragmentToModifyReservarConfirm action =
                    ModifyReservaMainFragmentDirections.actionModifyReservaMainFragmentToModifyReservarConfirm(reservaUuid);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(action);
        });

        binding.toolbarReservarConfirm.setNavigationOnClickListener(v -> {
            mainViewModel.setSelectedHour(null);
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        reservaLiveData.observe(getViewLifecycleOwner(), reserva -> {
            if (reserva != null) {
                enableChips();
                setNewReservaState(reserva);
                restoreSelectedDateDayChip();
                restoreSelectedTipoPlaza();
                loadAvailableHours();
            }
            reservaLiveData.removeObservers(getViewLifecycleOwner());
        });

        return binding.getRoot();
    }

    private void setNewReservaState(Reserva reserva) {
        LocalDate selectedDate =
                TimeUtils.convertTimestampToLocalDateTime(reserva.getFecha()).toLocalDate();
        mainViewModel.setNewSelectedDateDay(selectedDate.getDayOfMonth());
        mainViewModel.setNewSelectedTipoPlaza(reserva.getPlaza().getTipoPlaza());
        mainViewModel.setSelectedHour(reserva.getHora());
        int[] chipId = {R.id.day1};
        dayChipId.forEach(id -> {
            final Chip chip = binding.datePicker.findViewById(id);
            if ((int) chip.getTag() == selectedDate.getDayOfMonth()) {
                chipId[0] = id;
            }
        });
        mainViewModel.setNewSelectedDateDayChip(chipId[0]);
    }

    private void enableChips() {
        binding.day1.setEnabled(true);
        binding.day2.setEnabled(true);
        binding.day3.setEnabled(true);
        binding.day4.setEnabled(true);
        binding.day5.setEnabled(true);
        binding.day6.setEnabled(true);
        binding.day7.setEnabled(true);
        binding.chipCar.setEnabled(true);
        binding.chipElectricCar.setEnabled(true);
        binding.chipMotorcycle.setEnabled(true);
        binding.chipAccessibleCar.setEnabled(true);
    }

    private void loadAvailableHours() {
        DataRepository dataRepository = DataRepository.getInstance();

        TipoPlaza tipoPlaza = mainViewModel.getNewSelectedTipoPlaza().getValue();
        LocalDate selectedDate = mainViewModel.getNewSelectedDate().getValue();

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
                binding.progressIndicatorHorarios.hide();
                binding.progressIndicatorHorarios.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
            listLiveData.removeObservers(getViewLifecycleOwner());
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            mainViewModel.setNewSelectedDateDay((Integer) clickedChip.getTag());
            mainViewModel.setNewSelectedDateDayChip(clickedChip.getId());

            // Reset the selected hour
            mainViewModel.setSelectedHour(null);

            // Disable the continue button until an hour is selected
            binding.buttonReservarContinue.setEnabled(false);

            TipoPlaza tipoPlaza = mainViewModel.getNewSelectedTipoPlaza().getValue();
            LocalDate selectedDate = mainViewModel.getNewSelectedDate().getValue();
            reloadAvailableHoursForDateAndTipoPlaza(selectedDate, tipoPlaza);
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
            text.setText(dayOfWeek.substring(0, 1).toUpperCase(Locale.getDefault()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void restoreSelectedDateDayChip() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        // Set the initial selected date day chip
        if (mainViewModel.getNewSelectedDateDayChip().getValue() == null){
            mainViewModel.setNewSelectedDateDayChip(R.id.day1);
        }
        final Chip selectedChip = binding.datePicker.findViewById(mainViewModel.getNewSelectedDateDayChip().getValue());
        selectedChip.setChecked(true);
    }

    private void restoreSelectedTipoPlaza() {
        final TipoPlaza tipoPlaza = mainViewModel.getNewSelectedTipoPlaza().getValue();
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
        tipoPlazaChipId.forEach(chipId -> {
            final Chip chip = binding.chipGroup.findViewById(chipId);
            chip.setOnClickListener(v -> {
                // Disable the continue button until an hour is selected
                binding.buttonReservarContinue.setEnabled(false);

                // Reset the selected hour
                mainViewModel.setSelectedHour(null);

                LocalDate selectedDate = mainViewModel.getNewSelectedDate().getValue();
                final Chip clickedChip = (Chip) v;
                final int id = clickedChip.getId();
                if (id == R.id.chip_car) {
                    mainViewModel.setNewSelectedTipoPlaza(TipoPlaza.ESTANDAR);
                } else if (id == R.id.chip_electric_car) {
                    mainViewModel.setNewSelectedTipoPlaza(TipoPlaza.ELECTRICO);
                } else if (id == R.id.chip_motorcycle) {
                    mainViewModel.setNewSelectedTipoPlaza(TipoPlaza.MOTO);
                } else if (id == R.id.chip_accessible_car) {
                    mainViewModel.setNewSelectedTipoPlaza(TipoPlaza.DISCAPACITADO);
                }
                TipoPlaza tipoPlaza = mainViewModel.getNewSelectedTipoPlaza().getValue();
                reloadAvailableHoursForDateAndTipoPlaza(selectedDate, tipoPlaza);
            });
        });
    }

    private void reloadAvailableHoursForDateAndTipoPlaza(LocalDate date, TipoPlaza tipoPlaza) {
        Log.d(LOG_TAG, "reloadAvailableHoursForDateAndTipoPlaza: " + date + " " + tipoPlaza);
        binding.progressIndicatorHorarios.show();
        binding.progressIndicatorHorarios.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        LiveData<List<HourItem>> listLiveData = DataRepository.getInstance().getAvailableHoursForDateAndTipoPlaza(date, tipoPlaza, getViewLifecycleOwner(), new Callback() {
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
                HourAdapter adapter = new HourAdapter(hours, mainViewModel);
                binding.recyclerView.setAdapter(adapter);
                binding.progressIndicatorHorarios.hide();
                binding.progressIndicatorHorarios.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
            listLiveData.removeObservers(getViewLifecycleOwner());
        });
    }
}
