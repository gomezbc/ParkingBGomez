package com.lksnext.ParkingBGomez;

import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        LocalDateTime curentLocalDateTime = LocalDateTime.now();
        Map<LocalDate, List<Reserva>> reservas = LiveDataTestUtil.getOrAwaitValue(mainViewModel.getReservasByDay());
        assertNull(reservas.get(curentLocalDateTime.toLocalDate()));

        Reserva reserva = new Reserva(
                curentLocalDateTime,
                "usuario",
                1L,
                null,
                null);

        mainViewModel.addReserva(reserva);

        reservas = LiveDataTestUtil.getOrAwaitValue(mainViewModel.getReservasByDay());
        assertEquals(1,
                Objects.requireNonNull(reservas.get(curentLocalDateTime.toLocalDate())).size());
    }
}