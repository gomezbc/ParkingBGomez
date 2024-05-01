package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarMainBinding;
import com.lksnext.ParkingBGomez.domain.HourItem;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.view.HourAdapter;
import com.lksnext.ParkingBGomez.view.HourItemDecoration;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservarMainFragment extends Fragment{

    private FragmentReservarMainBinding binding;
    private MainViewModel mainViewModel;
    private final List<Integer> day_chip_id = new ArrayList<>(Arrays.asList(R.id.day1, R.id.day2,
            R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7));
    private final List<Integer> day_text_id = new ArrayList<>(Arrays.asList(R.id.textView_day1,
            R.id.textView_day2, R.id.textView_day3, R.id.textView_day4, R.id.textView_day5,
            R.id.textView_day6, R.id.textView_day7));
    private final List<Integer> tipoPlaza_chip_id = new ArrayList<>(Arrays.asList(R.id.chip_car,
            R.id.chip_electric_car, R.id.chip_motorcycle, R.id.chip_accessible_car));
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentReservarMainBinding.inflate(inflater, container, false);

        binding.buttonReservarContinue.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_reservarMainFragment_to_reservarConfirm));

        restoreSelectedDateDayChip();
        restoreSelectedTipoPlaza();

        setDay_chip();
        setDay_text();


        setTipoPlazaChipsListener();

        executorService.execute(() -> {
            // Your background task here that fetch data
            List<HourItem> hours = Arrays.asList(
                    new HourItem("07:00", true),
                    new HourItem("07:30", true),
                    new HourItem("08:00", true),
                    new HourItem("08:30", true),
                    new HourItem("09:00", true),
                    new HourItem("09:30", true),
                    new HourItem("10:00", true),
                    new HourItem("10:30", true),
                    new HourItem("11:00", true),
                    new HourItem("11:30", false),
                    new HourItem("12:00", true),
                    new HourItem("12:30", true),
                    new HourItem("13:00", true),
                    new HourItem("13:30", false),
                    new HourItem("14:00", false),
                    new HourItem("14:30", true),
                    new HourItem("15:00", true),
                    new HourItem("15:30", true),
                    new HourItem("16:00", true),
                    new HourItem("16:30", true),
                    new HourItem("17:00", true),
                    new HourItem("17:30", true),
                    new HourItem("18:00", true),
                    new HourItem("18:30", true),
                    new HourItem("19:00", true),
                    new HourItem("19:30", true),
                    new HourItem("20:00", true));
            // Update the UI on the UI thread
            requireActivity().runOnUiThread(() -> {
                if (binding != null) {
                    RecyclerView recyclerView = binding.recyclerView;
                    recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 5)); // 5 columns
                    HourAdapter adapter = new HourAdapter(hours, mainViewModel);
                    recyclerView.setAdapter(adapter);
                    recyclerView.addItemDecoration(new HourItemDecoration(20, 5));
                }
            });
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executorService.shutdown();
        binding = null;
    }

    private void setDay_chip(){
        setDay_chip_text();
    }

    /**
     * Set the text of the day chips in the date picker
     * The text is the day of the month
     */
    private void setDay_chip_text(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        for (int chip_id: day_chip_id
             ) {
            final Chip chip = binding.datePicker.findViewById(chip_id);
            chip.setText(String.valueOf(day));
            chip.setTag(day); // Set the value to the chip
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            // Add a check change listener to handle the selection
            setDay_chip_listener(chip);
        }
    }

    private void setDay_chip_listener(Chip chip){
        chip.setOnClickListener(v -> {
            final Chip  clickedChip = (Chip) v;
            // Update LiveData values
            mainViewModel.setSelectedDateDay((Integer) clickedChip.getTag());
            mainViewModel.setSelectedDateDayChip(clickedChip.getId());
        });
    }

    /**
     * Set the text of the day text views in the date picker
     * The text is the day of the month
     */
    private void setDay_text(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()));
        for (int text_id: day_text_id
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
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
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
        tipoPlaza_chip_id.forEach(chip_id -> {
            final Chip chip = binding.chipGroup.findViewById(chip_id);
            chip.setOnClickListener(v -> {
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
            });
        });
    }
}
