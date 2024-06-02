package com.lksnext.ParkingBGomez.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataRepository {

    private static final String RESERVAS_COLLECTION = "reservas";

    private static DataRepository instance;
    private DataRepository(){

    }

    //Creación de la instancia en caso de que no exista.
    public static synchronized DataRepository getInstance(){
        if (instance == null){
            instance = new DataRepository();
        }
        return instance;
    }

    //Petición del login.
    public void login(String email, String pass, Callback callback){
        try {
            //Realizar petición
            callback.onSuccess();
        } catch (Exception e){
            callback.onFailure();
        }
    }

    public void saveReserva(Reserva reserva, MainActivity activity, Callback callback){
        activity.getDb().collection(RESERVAS_COLLECTION).document(reserva.getUuid())
                .set(reserva)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public LiveData<Map<LocalDate, List<Reserva>>> getReservasByDayByUser(String user, MainActivity activity, Callback callback){
        MutableLiveData<Map<LocalDate, List<Reserva>>> liveData = new MutableLiveData<>();
        Map<LocalDate, List<Reserva>> reservasByDay = new HashMap<>();
        final LocalDateTime firstDayOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        activity.getDb().collection(RESERVAS_COLLECTION)
                .whereEqualTo("usuario", user)
                .get()
                .addOnSuccessListener(documentReference -> {
                        documentReference.toObjects(Reserva.class).forEach(reserva -> {
                            final LocalDate date = TimeUtils.convertStringToDateLocalTime(reserva.getFecha()).toLocalDate();
                            reservasByDay.putIfAbsent(date, new ArrayList<>());
                            if (reservasByDay.get(date) != null
                                    && !Objects.requireNonNull(reservasByDay.get(date)).contains(reserva)
                                    && TimeUtils.convertStringToDateLocalTime(reserva.getFecha())
                                    .isAfter(firstDayOfMonth)){

                                Objects.requireNonNull(reservasByDay.get(date)).add(reserva);
                            }
                            liveData.setValue(reservasByDay);
                        });
                        callback.onSuccess();
                }).addOnFailureListener(e -> callback.onSuccess());
        return liveData;
    }


    public LiveData<List<Reserva>> getReservasOfUserAfterToday(String user, MainActivity activity, Callback callback){
        MutableLiveData<List<Reserva>> liveData = new MutableLiveData<>();
        List<Reserva> reservas = new ArrayList<>();
        long startOfTodayEpoch = TimeUtils.getStartOfTodayEpoch();

        activity.getDb().collection(RESERVAS_COLLECTION)
                .whereEqualTo("usuario", user)
                .get()
                .addOnSuccessListener(documentReference -> {
                    documentReference.toObjects(Reserva.class).forEach(reserva -> {
                        long fechaEpoch = TimeUtils.convertLocalDateTimeStringToEpoch(reserva.getFecha());
                        if (fechaEpoch > startOfTodayEpoch && !reservas.contains(reserva)) {
                            reservas.add(reserva);
                        }
                    });
                    liveData.setValue(reservas);
                    callback.onSuccess();
                }).addOnFailureListener(e -> callback.onSuccess());
        return liveData;
    }
}