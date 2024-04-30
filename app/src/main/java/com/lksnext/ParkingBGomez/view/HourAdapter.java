package com.lksnext.ParkingBGomez.view;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.HourItem;

import java.util.List;

public class HourAdapter extends RecyclerView.Adapter<HourViewHolder> {
    private final List<HourItem> hours;

    public HourAdapter(List<HourItem> hours) {
        this.hours = hours;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hour, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        HourItem hour = hours.get(position);

        // Setup the button properties based on the HourItem's state
        holder.button.setText(hour.getHour());
        holder.button.setEnabled(hour.isEnabled());

        if (hour.isEnabled()) {
            holder.button.setAlpha(1.0f);
        } else {
            holder.button.setAlpha(0.5f);
        }

        holder.button.setRotationY(hour.isInMiddle() ? 180f : 0f);

        holder.button.setSelected(hour.isSelected());

        holder.button.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            HourItem hourItem = hours.get(currentPosition);
            hourItem.setSelected(!hourItem.isSelected());

            hours.set(currentPosition, hourItem);
            notifyItemChanged(currentPosition);
        });
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    @Override
    public long getItemId(int position) {
        return hours.get(position).getHour().hashCode();
    }
}