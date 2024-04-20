package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarMainBinding;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

public class ReservarMainFragment extends Fragment{

    private FragmentReservarMainBinding binding;
    private MainViewModel mainViewModel;
    private final List<Integer> day_chip_id = new ArrayList<>(Arrays.asList(R.id.day1, R.id.day2,
            R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7));
    private final List<Integer> day_text_id = new ArrayList<>(Arrays.asList(R.id.textView_day1,
            R.id.textView_day2, R.id.textView_day3, R.id.textView_day4, R.id.textView_day5,
            R.id.textView_day6, R.id.textView_day7));

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentReservarMainBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setDay_chip_text();
        setDay_text();

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
            chip.setOnClickListener(v -> {
                final Chip  clickedChip = (Chip) v;
                mainViewModel.setSelectedDateDay((Integer) clickedChip.getTag());
            });
        }
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
}
