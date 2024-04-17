package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentReservarMainBinding;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

public class ReservarMainFragment extends Fragment{

    private FragmentReservarMainBinding binding;
    private final List<Integer> day_chip_id = new ArrayList<>(Arrays.asList(R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7));

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentReservarMainBinding.inflate(inflater, container, false);

        setDay_chip_text();

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
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        for (int chip_id: day_chip_id
             ) {
            Chip chip = binding.datePicker.findViewById(chip_id);
            chip.setText(String.valueOf(day));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            // Add a check change listener to handle the selection
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Chip chip = (Chip) v;
                    chip.setChecked(true);
                    Log.d("Chip", "Chip clicked"+chip.getText());
                }
            });
        }
    }
}
