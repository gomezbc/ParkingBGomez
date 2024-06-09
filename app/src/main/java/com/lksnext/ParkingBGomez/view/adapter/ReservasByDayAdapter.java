package com.lksnext.ParkingBGomez.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.domain.ReservationsRefreshListener;
import com.lksnext.ParkingBGomez.view.holder.ReservasByDayViewHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReservasByDayAdapter extends RecyclerView.Adapter<ReservasByDayViewHolder> {

    private final Map<LocalDate, List<Reserva>> reservasByDay;
    private final FragmentManager fragmentManager;
    private final ReservationsRefreshListener refreshListener;

    public ReservasByDayAdapter(Map<LocalDate, List<Reserva>> reservasByDay, @NonNull FragmentManager fragmentManager, @NonNull ReservationsRefreshListener refreshListener) {
        this.reservasByDay = reservasByDay;
        this.fragmentManager = fragmentManager;
        this.refreshListener = refreshListener;
    }

    @NonNull
    @Override
    public ReservasByDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reserva_day_group, parent, false);
        return new ReservasByDayViewHolder(view, fragmentManager, refreshListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasByDayViewHolder holder, int position) {
        // Get the key and value for the item at the given position
        LocalDate localDate = (LocalDate) reservasByDay.keySet().toArray()[position];
        List<Reserva> reservas = reservasByDay.get(localDate);
        holder.bind(localDate, reservas);
    }

    @Override
    public int getItemCount() {
        return reservasByDay.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Get the key for the item at the given position
        Object key = reservasByDay.keySet().toArray()[position];

        // Return a unique integer for the key
        return key.hashCode();
    }
}
