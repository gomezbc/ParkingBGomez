package com.lksnext.ParkingBGomez.view;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.domain.HourItem;

import java.util.List;
import java.util.Optional;

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
        final HourItem hour = hours.get(position);

        // Setup the button properties based on the HourItem's state
        holder.button.setText(hour.getHour());
        holder.button.setEnabled(hour.isEnabled());

        if (hour.isEnabled()) {
            holder.button.setAlpha(1.0f);
        } else {
            holder.button.setAlpha(0.5f);
        }

        holder.button.setBackgroundResource(hour.isInMiddle() ?
                R.drawable.in_middle_timeslot : R.drawable.default_timeslot);

        holder.button.setSelected(hour.isSelected());

        holder.button.setOnClickListener(v -> {
            final int newSelectedPosition = holder.getAdapterPosition();
            final HourItem newSelectedHourItem = hours.get(newSelectedPosition);

            Optional<HourItem> isAnyHourSelected = hours.stream()
                    .filter(HourItem::isSelected)
                    .findAny();
            final int selectedCount = (int) hours.stream().filter(HourItem::isSelected).count();

            if (newSelectedHourItem.isSelected() && selectedCount == 1) {
                // Si ya esta seleccionado deseleccionar
                setPositionAsSelected(newSelectedPosition);
                clearItemsInRange(0, hours.size());
                return;
            }

            if (isAnyHourSelected.isPresent()) {
                // Si ya hay alguno seleccionado
                final int previousSelectedPosition = hours.indexOf(isAnyHourSelected.get());
                if (newSelectedPosition > previousSelectedPosition && selectedCount < 2) {
                    // Si el nuevo seleccionado es posterior al anterior seleccionado y no hay 2 seleccionados
                    final List<HourItem> subList =
                            hours.subList(previousSelectedPosition + 1, newSelectedPosition);
                    if (!subList.isEmpty()){
                        // La seleccionada no es la siguiente
                        Optional<HourItem> isDisabledInTheMiddle = subList.stream()
                                .filter(h -> !h.isEnabled())
                                .findAny();
                        if (isDisabledInTheMiddle.isPresent()) {
                            // Si hay un deshabilitado en medio poner el nuevo como el unico seleccionado y quitar middles
                            final int disabledPosition = hours.indexOf(isDisabledInTheMiddle.get());
                            clearItemsInRange(previousSelectedPosition, disabledPosition);
                            setPositionAsSelected(newSelectedPosition);
                            // Poner en middle los siguientes al newSelected hasta algun disabled si hay
                            final List<HourItem> subList3 =
                                    hours.subList(newSelectedPosition + 1, hours.size());
                            Optional<HourItem> isDisabledInTheMiddle2 = subList3.stream()
                                    .filter(h -> !h.isEnabled())
                                    .findAny();
                            if (isDisabledInTheMiddle2.isPresent()) {
                                final int disabledPosition2 = hours.indexOf(isDisabledInTheMiddle2.get());
                                setItemsInRangeAsInMiddle(newSelectedPosition + 1, disabledPosition2);
                            }else {
                                setItemsInRangeAsInMiddle(newSelectedPosition + 1, hours.size());
                            }
                        }else {
                            // Si no hay ninguno deshabilitado en medio poner en middle todos los siguientes
                            setItemsInRangeAsInMiddle(previousSelectedPosition + 1, newSelectedPosition);
                            setPositionAsSelected(newSelectedPosition);
                            // Quitar middle de los siguientes
                            clearItemsInRange(newSelectedPosition + 1, hours.size());
                        }
                    }else {
                        // La seleccionada es la siguiente
                        setPositionAsSelected(newSelectedPosition);
                        // quitar middle todos los siguientes
                        clearItemsInRange(newSelectedPosition + 1, hours.size());
                    }
                }else {
                    // Si el nuevo seleccionado es anterior al anterior seleccionado o si ya hay 2 seleccionados
                    // quitar los middles
                    clearItemsInRange(0, hours.size());

                    Optional<HourItem> isDisabledInTheMiddle = hours
                            .subList(newSelectedPosition, hours.size())
                            .stream()
                            .filter(h -> !h.isEnabled())
                            .findAny();

                    if (isDisabledInTheMiddle.isPresent()) {
                        // Si hay un deshabilitado en medio poner el nuevo como el unico seleccionado y poner middles hasta el deshabilitado
                        final int disabledPosition = hours.indexOf(isDisabledInTheMiddle.get());
                        setItemsInRangeAsInMiddle(newSelectedPosition, disabledPosition);
                        setPositionAsSelected(newSelectedPosition);
                    }else {
                        // Si no hay ninguno deshabilitado en medio poner en middle todos los siguientes
                        setItemsInRangeAsInMiddle(newSelectedPosition + 1, hours.size());
                        setPositionAsSelected(newSelectedPosition);
                    }
                }
            }else {
                // Si no hay ninguno seleccionado
                final List<HourItem> subList =
                        hours.subList(newSelectedPosition + 1, hours.size());
                Optional<HourItem> isDisabledInTheMiddle = subList.stream()
                        .filter(h -> !h.isEnabled())
                        .findAny();
                if (isDisabledInTheMiddle.isPresent()) {
                    // Si hay un deshabilitado en medio poner en middle todos los anteriores hasta el deshabilitado
                    final int disabledPosition = hours.indexOf(isDisabledInTheMiddle.get());
                    setItemsInRangeAsInMiddle(newSelectedPosition + 1, disabledPosition);
                }else {
                    // Si no hay ninguno deshabilitado en medio poner en middle todos los siguientes
                    setItemsInRangeAsInMiddle(newSelectedPosition + 1, hours.size());
                }
                setPositionAsSelected(newSelectedPosition);
            }
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

    private void clearItemsInRange(int start, int end) {
        if (start < 0 || end > hours.size() || start >= end) {
            return;
        }
        List<HourItem> subList = hours.subList(start, end);
        subList.forEach(h -> {
            h.setInMiddle(false);
            h.setSelected(false);
        });
        notifyItemRangeChanged(start, subList.size());
    }

    private void setItemsInRangeAsInMiddle(int start, int end) {
        if (start < 0 || end > hours.size() || start >= end) {
            return;
        }
        List<HourItem> subList = hours.subList(start, end);
        subList.forEach(h -> {
            h.setInMiddle(true);
            h.setSelected(false);
        });
        notifyItemRangeChanged(start, subList.size());
    }

    private void setPositionAsSelected(int position) {
        if (position < 0 || position >= hours.size()) {
            return;
        }
        final HourItem hourItem = hours.get(position);
        hourItem.setSelected(true);
        hourItem.setInMiddle(false);
        notifyItemChanged(position);
    }
}