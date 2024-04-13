package com.lksnext.ParkingBGomez;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A host (typically an {@code Activity}} that can display fragments and knows how to respond to
 * navigation events.
 */
public interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */
    void navigateTo(@NonNull Fragment fragment, boolean addToBackstack);
}
