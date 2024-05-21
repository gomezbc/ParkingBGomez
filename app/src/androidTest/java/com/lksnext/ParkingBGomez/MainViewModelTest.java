package com.lksnext.ParkingBGomez;

import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Rule;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

public class MainViewModelTest {

    private MainViewModel mainViewModel;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        mainViewModel = new MainViewModel();
    }

}