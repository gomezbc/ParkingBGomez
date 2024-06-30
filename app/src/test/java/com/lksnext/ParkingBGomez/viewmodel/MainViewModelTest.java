package com.lksnext.ParkingBGomez.viewmodel;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.lksnext.ParkingBGomez.LiveDataTestUtil;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.enums.ReservarState;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;

public class MainViewModelTest {

    private MainViewModel viewModel;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        viewModel = new MainViewModel();
    }

    @Test
    public void testSelectedDate() throws InterruptedException {
        LocalDate date = LocalDate.of(2024, 6, 1);
        viewModel.setSelectedDate(date);
        LocalDate result = LiveDataTestUtil.getOrAwaitValue(viewModel.getSelectedDate());
        assertEquals(date, result);
    }

    @Test
    public void testSelectedDateDay() throws InterruptedException {
        viewModel.setSelectedDate(LocalDate.of(2024, 6, 1));
        viewModel.setSelectedDateDay(15);
        LocalDate result = LiveDataTestUtil.getOrAwaitValue(viewModel.getSelectedDate());
        assertEquals(15, result.getDayOfMonth());
    }

    @Test
    public void testSelectedDateDayChip() throws InterruptedException {
        viewModel.setSelectedDateDayChip(10);
        Integer result = LiveDataTestUtil.getOrAwaitValue(viewModel.getSelectedDateDayChip());
        assertEquals(Integer.valueOf(10), result);
    }

    @Test
    public void testSelectedTipoPlaza() throws InterruptedException {
        viewModel.setSelectedTipoPlaza(TipoPlaza.ESTANDAR);
        TipoPlaza result = LiveDataTestUtil.getOrAwaitValue(viewModel.getSelectedTipoPlaza());
        assertEquals(TipoPlaza.ESTANDAR, result);
    }

    @Test
    public void testSelectedHour() throws InterruptedException {
        Hora hora = new Hora(10, 30);
        viewModel.setSelectedHour(hora);
        Hora result = LiveDataTestUtil.getOrAwaitValue(viewModel.getSelectedHour());
        assertEquals(hora, result);
    }

    @Test
    public void testReservarState() throws InterruptedException {
        viewModel.setReservarState(ReservarState.RESERVAR);
        ReservarState result = LiveDataTestUtil.getOrAwaitValue(viewModel.getReservarState());
        assertEquals(ReservarState.RESERVAR, result);
    }

    @Test
    public void testNewSelectedDateDay() throws InterruptedException {
        viewModel.setNewSelectedDateDay(20);
        LocalDate result = LiveDataTestUtil.getOrAwaitValue(viewModel.getNewSelectedDate());
        assertEquals(20, result.getDayOfMonth());
    }

    @Test
    public void testNewSelectedDateDayChip() throws InterruptedException {
        viewModel.setNewSelectedDateDayChip(12);
        Integer result = LiveDataTestUtil.getOrAwaitValue(viewModel.getNewSelectedDateDayChip());
        assertEquals(Integer.valueOf(12), result);
    }

    @Test
    public void testNewSelectedTipoPlaza() throws InterruptedException {
        viewModel.setNewSelectedTipoPlaza(TipoPlaza.ELECTRICO);
        TipoPlaza result = LiveDataTestUtil.getOrAwaitValue(viewModel.getNewSelectedTipoPlaza());
        assertEquals(TipoPlaza.ELECTRICO, result);
    }
}
