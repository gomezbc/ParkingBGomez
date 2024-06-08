package com.lksnext.ParkingBGomez.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.Plaza;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DataRepository {

    private static final String RESERVAS_COLLECTION = "reservas";
    private static final String PLAZAS_COLLECTION = "plazas";
    private static final String TIPO_PLAZA = "tipoPlaza";
    private static final String USUARIO = "usuario";

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
                .whereEqualTo(USUARIO, user)
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
                        });
                        liveData.setValue(reservasByDay);
                        callback.onSuccess();
                }).addOnFailureListener(e -> callback.onSuccess());
        return liveData;
    }


    public LiveData<List<Reserva>> getReservasOfUserAfterToday(String user, MainActivity activity, Callback callback){
        MutableLiveData<List<Reserva>> liveData = new MutableLiveData<>();
        List<Reserva> reservas = new ArrayList<>();
        long startOfTodayEpoch = TimeUtils.getStartOfTodayEpoch();

        activity.getDb().collection(RESERVAS_COLLECTION)
                .whereEqualTo(USUARIO, user)
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

    public LiveData<Plaza> getPlazaNotReservadaByTipoPlaza(TipoPlaza tipoPlaza, Hora hora, MainActivity activity, Callback callback){
        CollectionReference plazasCollection = activity.getDb().collection(PLAZAS_COLLECTION);
        CollectionReference reservasCollection = activity.getDb().collection(RESERVAS_COLLECTION);

        MutableLiveData<Plaza> liveData = new MutableLiveData<>();
        LocalDateTime inicioLocalDateTime = TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio());

        MutableLiveData<List<Long>> plazasIdInThatHoursLiveData = new MutableLiveData<>();
        List<Long> plazasIdInThatHours = new ArrayList<>();

        // La fecha no se puede comparar en la query, ya que es un string
        reservasCollection
                .get()
                .addOnSuccessListener(documentReference -> {
                            documentReference.toObjects(Reserva.class).forEach(reserva -> {
                                LocalDateTime fecha = TimeUtils.convertStringToDateLocalTime(reserva.getFecha());
                                // Coge las reservas que esten en la hora que se quiere reservar
                                if (reserva.getHora().getHoraInicio() <= hora.getHoraInicio() ||
                                        reserva.getHora().getHoraFin() >= hora.getHoraFin() &&
                                                (fecha.getDayOfYear() == inicioLocalDateTime.getDayOfYear())){
                                    plazasIdInThatHours.add(reserva.getPlaza().getId());
                                }
                                callback.onSuccess();
                            });
                            plazasIdInThatHoursLiveData.setValue(plazasIdInThatHours);
                        }
                        )
                .addOnFailureListener(e -> callback.onFailure());

        plazasIdInThatHoursLiveData.observe(activity, plazas -> {
            if (plazas.isEmpty()){
                liveData.setValue(null);
            }else {
                // Gets the first plaza with a documentId not in the list of plazasIdInThatHours
                plazasCollection.whereNotIn(FieldPath.documentId(),
                                plazas.stream().map(String::valueOf).collect(Collectors.toList()))
                        .whereEqualTo(TIPO_PLAZA, tipoPlaza)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(documentReference -> {
                            if (!documentReference.isEmpty()){
                                liveData.setValue(documentReference.toObjects(Plaza.class).get(0));
                            }
                            callback.onSuccess();
                        }).addOnFailureListener(e -> callback.onFailure());
            }
        });

        return liveData;
    }

    public LiveData<Integer> getPlazasLibresByTipoPlaza(TipoPlaza tipoPlaza, long horaActual ,MainActivity activity, Callback callback){
        CollectionReference plazasCollection = activity.getDb().collection(PLAZAS_COLLECTION);
        CollectionReference reservasCollection = activity.getDb().collection(RESERVAS_COLLECTION);

        LocalDateTime todayLocalDateTime = LocalDateTime.now(TimeUtils.ZONE_ID);

        MutableLiveData<Integer> plazasLibre = new MutableLiveData<>();

        AtomicInteger totalPlazas = new AtomicInteger(0);

        MutableLiveData<List<Long>> plazasIdInThatHoursLiveData = new MutableLiveData<>();
        List<Long> plazasIdInThatHours = new ArrayList<>();

        // La fecha no se puede comparar en la query, ya que es un string
        // La hora tampoco se puede comparar ya que es un subdocumento
        reservasCollection
                .get()
                .addOnSuccessListener(documentReference -> {
                            documentReference.toObjects(Reserva.class).forEach(reserva -> {
                                LocalDateTime fecha = TimeUtils.convertStringToDateLocalTime(reserva.getFecha());
                                // Coge las reservas que esten en la hora que se quiere reservar
                                if (reserva.getHora().getHoraInicio() <= horaActual &&
                                        horaActual <= reserva.getHora().getHoraFin() &&
                                        (fecha.getDayOfYear() == todayLocalDateTime.getDayOfYear())){
                                    plazasIdInThatHours.add(reserva.getPlaza().getId());
                                }
                                callback.onSuccess();
                            });
                            plazasIdInThatHoursLiveData.setValue(plazasIdInThatHours);
                        }
                )
                .addOnFailureListener(e -> callback.onFailure());

        plazasIdInThatHoursLiveData.observe(activity, plazas -> {
            if (plazas.isEmpty()){
                plazasLibre.setValue(0);
            }else {
                // Gets the first plaza with a documentId not in the list of plazasIdInThatHours
                plazasCollection.whereNotIn(FieldPath.documentId(),
                                plazas.stream().map(String::valueOf).collect(Collectors.toList()))
                        .whereEqualTo(TIPO_PLAZA, tipoPlaza)
                        .get()
                        .addOnSuccessListener(documentReference -> {
                            documentReference.toObjects(Plaza.class)
                                    .forEach(plaza -> totalPlazas.getAndIncrement());

                            plazasLibre.setValue(totalPlazas.get());
                            callback.onSuccess();
                        }).addOnFailureListener(e -> callback.onFailure());

            }
        });

        return plazasLibre;
    }

    public LiveData<Integer> getTotalPlazasByTipoPlaza(TipoPlaza tipoPlaza, MainActivity activity, Callback callback){
        MutableLiveData<Integer> totalPlazas = new MutableLiveData<>();
        activity.getDb().collection(PLAZAS_COLLECTION)
                .whereEqualTo(TIPO_PLAZA, tipoPlaza)
                .get()
                .addOnSuccessListener(documentReference -> {
                    totalPlazas.setValue(documentReference.size());
                    callback.onSuccess();
                }).addOnFailureListener(e -> callback.onFailure());
        return totalPlazas;
    }

}