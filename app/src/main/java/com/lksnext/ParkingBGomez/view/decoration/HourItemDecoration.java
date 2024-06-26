package com.lksnext.ParkingBGomez.view.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HourItemDecoration extends RecyclerView.ItemDecoration{
    private final int space;
    private final int columnCount;

    public HourItemDecoration(int space, int columnCount) {
        this.space = space;
        this.columnCount = columnCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        // Add top margin only if the item is not in the first row
        if (position >= columnCount) {
            outRect.top = space;
        }

        // Add right margin only if the item is not in the right column
        if ((position + 1) % columnCount != 0) {
            outRect.right = space;
        }
    }
}
