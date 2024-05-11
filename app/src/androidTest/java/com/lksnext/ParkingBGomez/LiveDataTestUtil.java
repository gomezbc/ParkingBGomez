package com.lksnext.ParkingBGomez;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Helper class that observes for a pre-specified amount of time a livedata value to be changed.
 */
public class LiveDataTestUtil {

    /**
     * Function that waits for a LiveData value to be changed
     * @param liveData The LiveData variable to observe
     * @param <T> The LiveData value type
     * @return The new value of the LiveData
     * @throws InterruptedException Thrown when the latch wait is interrupted by the timeout
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        //Create a observer to observe for LiveData value change
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        //Observe the LiveData forever. Latch timeout will trigger.
        liveData.observeForever(observer);
        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }
        //noinspection unchecked
        return (T) data[0];
    }
}