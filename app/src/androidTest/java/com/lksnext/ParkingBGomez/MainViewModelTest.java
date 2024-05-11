package com.lksnext.ParkingBGomez;

import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

public class MainViewModelTest {

    private MainViewModel mainViewModel;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        mainViewModel = new MainViewModel();
    }

    @Test
    public void testAddReserva() throws InterruptedException {
        List<Reserva> reservas = LiveDataTestUtil.getOrAwaitValue(mainViewModel.getReservasList());
        assertEquals(0, reservas.size());

        mainViewModel.addReserva(null);

        reservas = LiveDataTestUtil.getOrAwaitValue(mainViewModel.getReservasList());
        assertEquals(1, reservas.size());
    }
}