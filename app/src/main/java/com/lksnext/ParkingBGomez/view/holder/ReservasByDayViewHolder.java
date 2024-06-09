package com.lksnext.ParkingBGomez.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.view.adapter.ReservasAdapter;
import com.lksnext.ParkingBGomez.view.decoration.ReservaItemDecoration;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ReservasByDayViewHolder extends RecyclerView.ViewHolder {

    private final FragmentManager fragmentManager;

    public ReservasByDayViewHolder(@NonNull View itemView, @NonNull FragmentManager fragmentManager) {
        super(itemView);
        this.fragmentManager = fragmentManager;
    }

    public void bind(LocalDate localDate, List<Reserva> reservas) {
        TextView dateTextView = itemView.findViewById(R.id.reserva_group_day);

        setFormattedDate(localDate, dateTextView);

        RecyclerView recyclerView = itemView.findViewById(R.id.reservas_by_day);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL,false));
        recyclerView.setNestedScrollingEnabled(false);

        ReservasAdapter adapter = new ReservasAdapter(this.fragmentManager);

        adapter.submitList(reservas);
        recyclerView.addItemDecoration(new ReservaItemDecoration(25));
        recyclerView.setAdapter(adapter);

        boolean isRecyclerViewEmpty = Objects.requireNonNull(recyclerView.getAdapter())
                .getItemCount() == 0;
        if (isRecyclerViewEmpty) {
            itemView.findViewById(R.id.default_reserva).setVisibility(View.VISIBLE);
        }
    }

    private static void setFormattedDate(LocalDate localDate, TextView dateTextView) {
        LocalDate today = LocalDate.now();
        boolean isToday = today.isEqual(localDate);
        boolean isYesterday = today.minusDays(1).isEqual(localDate);
        boolean isTomorrow = today.plusDays(1).isEqual(localDate);

        Context context = dateTextView.getContext();
        String formattedDate;
        if (isToday) {
            formattedDate = context.getString(R.string.today);
        } else if (isYesterday) {
            formattedDate = context.getString(R.string.yesterday);
        } else if (isTomorrow) {
            formattedDate = context.getString(R.string.tomorrow);
        } else {
            // Get the day of the week and month name
            String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            String monthName = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());

            // Format the date string
            formattedDate = String.format(Locale.getDefault(), "%s, %d %s", dayOfWeek, localDate.getDayOfMonth(), monthName);
        }

        dateTextView.setText(formattedDate);
    }
}
