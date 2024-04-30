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
                newSelectedHourItem.setSelected(false);
                notifyItemChanged(newSelectedPosition);
                hours.forEach(h -> h.setInMiddle(false));
                notifyItemRangeChanged(0, hours.size());
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
                        Optional<HourItem> isDisabledInTheMiddle = subList.stream()
                                .filter(h -> !h.isEnabled())
                                .findAny();
                        if (isDisabledInTheMiddle.isPresent()) {
                            // Si hay un deshabilitado en medio poner el nuevo como el unico seleccionado y quitar middles
                            final int disabledPosition = hours.indexOf(isDisabledInTheMiddle.get());
                            final List<HourItem> subList2 =
                                    hours.subList(previousSelectedPosition, disabledPosition);
                            subList2.forEach(h -> {
                                h.setInMiddle(false);
                                h.setSelected(false);
                            });
                            notifyItemRangeChanged(previousSelectedPosition, subList2.size());
                            newSelectedHourItem.setSelected(true);
                            newSelectedHourItem.setInMiddle(false);
                            notifyItemChanged(newSelectedPosition);
                            // Poner en middle los siguientes al newSelected hasta algun disabled si hay
                            final List<HourItem> subList3 =
                                    hours.subList(newSelectedPosition + 1, hours.size());
                            Optional<HourItem> isDisabledInTheMiddle2 = subList3.stream()
                                    .filter(h -> !h.isEnabled())
                                    .findAny();
                            if (isDisabledInTheMiddle2.isPresent()) {
                                final int disabledPosition2 = hours.indexOf(isDisabledInTheMiddle2.get());
                                final List<HourItem> subList4 =
                                        hours.subList(newSelectedPosition + 1, disabledPosition2);
                                subList4.forEach(h -> h.setInMiddle(true));
                                notifyItemRangeChanged(newSelectedPosition + 1, subList4.size());
                            }else {
                                subList3.forEach(h -> h.setInMiddle(true));
                                notifyItemRangeChanged(newSelectedPosition + 1, subList3.size());
                            }
                        }else {
                            // Si no hay ninguno deshabilitado en medio poner en middle todos los siguientes
                            subList.forEach(h -> h.setInMiddle(true));
                            notifyItemRangeChanged(previousSelectedPosition + 1, subList.size());
                            newSelectedHourItem.setSelected(true);
                            newSelectedHourItem.setInMiddle(false);
                            notifyItemChanged(newSelectedPosition);
                            // Quitar middle de los siguientes
                            final List<HourItem> subList3 =
                                    hours.subList(newSelectedPosition + 1, hours.size());
                            subList3.forEach(h -> h.setInMiddle(false));
                            notifyItemRangeChanged(newSelectedPosition + 1, subList3.size());
                        }
                    }else {
                        // La seleccionada es la siguiente
                        newSelectedHourItem.setSelected(true);
                        newSelectedHourItem.setInMiddle(false);
                        notifyItemChanged(newSelectedPosition);
                    }
                }else {
                    // Si el nuevo seleccionado es anterior al anterior seleccionado o si ya hay 2 seleccionados
                    hours.forEach(h -> {
                        h.setInMiddle(false);
                        h.setSelected(false);
                    });
                    notifyItemRangeChanged(0, hours.size());
                    // poner en middle todos los siguientes
                    Optional<HourItem> isDisabledInTheMiddle = hours
                            .subList(newSelectedPosition, hours.size())
                            .stream()
                            .filter(h -> !h.isEnabled())
                            .findAny();

                    if (isDisabledInTheMiddle.isPresent()) {
                        // Si hay un deshabilitado en medio poner el nuevo como el unico seleccionado y quitar middles
                        final int disabledPosition = hours.indexOf(isDisabledInTheMiddle.get());
                        final List<HourItem> subList2 =
                                hours.subList(newSelectedPosition, disabledPosition);
                        subList2.forEach(h -> {
                            h.setInMiddle(true);
                            h.setSelected(false);
                        });
                        notifyItemRangeChanged(newSelectedPosition, subList2.size());
                        newSelectedHourItem.setSelected(true);
                        newSelectedHourItem.setInMiddle(false);
                        notifyItemChanged(newSelectedPosition);
                    }else {
                        // Si no hay ninguno deshabilitado en medio poner en middle todos los siguientes
                        final List<HourItem> subList =
                                hours.subList(newSelectedPosition + 1, hours.size());
                        subList.forEach(h -> h.setInMiddle(true));
                        notifyItemRangeChanged(newSelectedPosition, subList.size());
                        newSelectedHourItem.setSelected(true);
                        notifyItemChanged(newSelectedPosition);
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
                    final List<HourItem> subList2 =
                            hours.subList(newSelectedPosition + 1, disabledPosition);
                    if (!subList2.isEmpty()){
                        subList2.forEach(h -> h.setInMiddle(true));
                        notifyItemRangeChanged(newSelectedPosition + 1, subList2.size());
                    }
                }else {
                    // Si no hay ninguno deshabilitado en medio poner en middle todos los siguientes
                    subList.forEach(h -> h.setInMiddle(true));
                    notifyItemRangeChanged(newSelectedPosition + 1, hours.size());
                }
                newSelectedHourItem.setSelected(true);
                notifyItemChanged(newSelectedPosition);
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
}