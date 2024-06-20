package com.lksnext.ParkingBGomez.data;


import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Hora;
import com.lksnext.ParkingBGomez.domain.HourItem;
import com.lksnext.ParkingBGomez.domain.Plaza;
import com.lksnext.ParkingBGomez.domain.Reserva;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class DataRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private static final String RESERVAS_COLLECTION = "reservas";
    private static final String PLAZAS_COLLECTION = "plazas";
    private static final String TIPO_PLAZA = "tipoPlaza";
    private static final String USUARIO = "usuario";
    private static final String FECHA = "fecha";
    private static final String TAG = "DataRepository";

    private static DataRepository instance;
    private DataRepository(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    //Creación de la instancia en caso de que no exista.
    public static synchronized DataRepository getInstance(){
        if (instance == null){
            instance = new DataRepository();
        }
        return instance;
    }

    public void login(String email, String password, Callback callback){
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener( unused -> {
                        Log.d(TAG, "signInWithEmail:success");
                        callback.onSuccess();
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "signInWithEmail:failure", e);
                        callback.onFailure();
                    });
        } catch (Exception e){
            Log.e(TAG, "signInWithEmail:failure", e);
            callback.onFailure();
        }
    }

    public void signUp(String email, String password, Callback callback){
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener( unused -> {
                        Log.d(TAG, "createUserWithEmail:success");
                        callback.onSuccess();
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "createUserWithEmail:failure", e);
                        callback.onFailure();
                    });
        } catch (Exception e){
            Log.e(TAG, "createUserWithEmail:failure", e);
            callback.onFailure();
        }
    }

    public void resetPassword(String email, Callback callback){
        try {
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener( unused -> {
                        Log.d(TAG, "resetPassword:success");
                        callback.onSuccess();
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "resetPassword:failure", e);
                        callback.onFailure();
                    });
        } catch (Exception e){
            Log.w(TAG, "resetPassword:exception", e);
            callback.onFailure();
        }
    }

    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public void saveReserva(Reserva reserva, Callback callback){
        db.collection(RESERVAS_COLLECTION).document(reserva.getUuid())
                .set(reserva)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public void deleteReserva(Reserva reserva, Callback callback){
        db.collection(RESERVAS_COLLECTION).document(reserva.getUuid())
                .delete()
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public void updateReserva(Reserva reserva, Callback callback){
        db.collection(RESERVAS_COLLECTION).document(reserva.getUuid())
                .set(reserva)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public LiveData<Map<LocalDate, List<Reserva>>> getReservasByDayByUser(String user, Callback callback){
        MutableLiveData<Map<LocalDate, List<Reserva>>> liveData = new MutableLiveData<>();
        // TreeMap to Order by date ascending
        Map<LocalDate, List<Reserva>> reservasByDay = new TreeMap<>();
        final long firstDayOfMonth =
                TimeUtils.convertLocalDateTimeToEpoch(LocalDate.now().withDayOfMonth(1).atStartOfDay());

        Timestamp firstTimestampOfTheMonth = new Timestamp(firstDayOfMonth, 0);

        db.collection(RESERVAS_COLLECTION)
                .whereEqualTo(USUARIO, user)
                .whereGreaterThanOrEqualTo(FECHA, firstTimestampOfTheMonth)
                .get()
                .addOnSuccessListener(documentReference -> {
                        documentReference.toObjects(Reserva.class).forEach(reserva -> {
                            final LocalDate date = TimeUtils.convertTimestampToLocalDateTime(reserva.getFecha()).toLocalDate();
                            reservasByDay.putIfAbsent(date, new ArrayList<>());
                            if (reservasByDay.get(date) != null
                                    && !Objects.requireNonNull(reservasByDay.get(date)).contains(reserva)){
                                Objects.requireNonNull(reservasByDay.get(date)).add(reserva);
                            }
                        });
                        liveData.setValue(reservasByDay);
                        callback.onSuccess();
                }).addOnFailureListener(e -> callback.onSuccess());
        return liveData;
    }


    public LiveData<List<Reserva>> getActiveReservasOfUser(String user, Callback callback){
        MutableLiveData<List<Reserva>> liveData = new MutableLiveData<>();
        List<Reserva> reservas = new ArrayList<>();
        long nowEpoch = TimeUtils.getNowEpoch();

        db.collection(RESERVAS_COLLECTION)
                .whereEqualTo(USUARIO, user)
                .get()
                .addOnSuccessListener(documentReference -> {
                    documentReference.toObjects(Reserva.class).forEach(reserva -> {
                        final long horaInicioEpoch = reserva.getHora().getHoraInicio();
                        if (nowEpoch <= horaInicioEpoch && !reservas.contains(reserva)) {
                            reservas.add(reserva);
                        }
                    });
                    liveData.setValue(reservas);
                    callback.onSuccess();
                }).addOnFailureListener(e -> callback.onSuccess());
        return liveData;
    }

    public LiveData<Plaza> getPlazaNotReservadaByTipoPlaza(Context context, TipoPlaza tipoPlaza, Hora hora, Callback callback){
        CollectionReference plazasCollection = db.collection(PLAZAS_COLLECTION);
        CollectionReference reservasCollection = db.collection(RESERVAS_COLLECTION);

        MutableLiveData<Plaza> liveData = new MutableLiveData<>();
        LocalDateTime inicioLocalDateTime = TimeUtils.convertEpochTolocalDateTime(hora.getHoraInicio());

        // Last epoch of the day
        long lastEpochOfTheDay =
                TimeUtils.convertLocalDateTimeToEpoch(inicioLocalDateTime.withHour(23).withMinute(59).withSecond(59));
        Timestamp lastTimestampOfTheDay = new Timestamp(lastEpochOfTheDay, 0);

        Timestamp horaInicioTimestamp = new Timestamp(hora.getHoraInicio(), 0);

        MutableLiveData<List<Long>> plazasIdInThatHoursLiveData = new MutableLiveData<>();
        List<Long> plazasIdInThatHours = new ArrayList<>();

        // Obtenemos las reservas que esten en la hora que se quiere reservar y el final del día
        // Si hacemos que la query sea mayor o igual a la hora de inicio o, menor o igual a la hora de fin
        // tambien nos va a coger las que estan fuera de este día y luego tendremos que volver a filtrar
        // De las que obtenemos en la query, solo nos interesan las que estan en las horas que se quiere reservar
        reservasCollection
                .where(Filter.and(Filter.greaterThanOrEqualTo(FECHA, horaInicioTimestamp),
                        Filter.lessThanOrEqualTo(FECHA, lastTimestampOfTheDay)))
                .get()
                .addOnSuccessListener(documentReference -> {
                            documentReference.toObjects(Reserva.class).forEach(reserva -> {
                                // Como hemos cogido las reservas entre la hora de inicio y el final del día
                                // tenemos que filtrar las que coincidan con la hora que se quiere reservar
                                if ((reserva.getHora().getHoraInicio() <= hora.getHoraInicio() ||
                                        reserva.getHora().getHoraFin() >= hora.getHoraFin()) &&
                                        reserva.getPlaza().getTipoPlaza() == tipoPlaza){
                                    plazasIdInThatHours.add(reserva.getPlaza().getId());
                                }
                                callback.onSuccess();
                            });
                            plazasIdInThatHoursLiveData.setValue(plazasIdInThatHours);
                        }
                        )
                .addOnFailureListener(e -> callback.onFailure());

        plazasIdInThatHoursLiveData.observe((LifecycleOwner) context, plazas -> {
            if (plazas.isEmpty()){
                getAnyFreePlazaWithQuery(plazasCollection.whereEqualTo(TIPO_PLAZA, tipoPlaza),
                        liveData,
                        callback);
            }else {
                // Gets the first plaza with a documentId not in the list of plazasIdInThatHours
                getAnyFreePlazaWithQuery(
                        plazasCollection.whereNotIn(FieldPath.documentId(),plazas.stream()
                                        .map(String::valueOf).collect(Collectors.toList()))
                        .whereEqualTo(TIPO_PLAZA, tipoPlaza),
                        liveData,
                        callback);
            }
        });

        return liveData;
    }

    private static void getAnyFreePlazaWithQuery(Query query, MutableLiveData<Plaza> liveData, Callback callback) {
        query
            .limit(1)
            .get()
            .addOnSuccessListener(documentReference -> {
                if (!documentReference.isEmpty()) {
                    liveData.setValue(documentReference.toObjects(Plaza.class).get(0));
                }
                callback.onSuccess();
            }).addOnFailureListener(e -> callback.onFailure());
    }

    public LiveData<Integer> getPlazasLibresByTipoPlaza(TipoPlaza tipoPlaza, long horaActual, Context context, Callback callback){
        CollectionReference plazasCollection = db.collection(PLAZAS_COLLECTION);
        CollectionReference reservasCollection = db.collection(RESERVAS_COLLECTION);

        LocalDateTime todayLocalDateTime = LocalDateTime.now(TimeUtils.ZONE_ID);

        MutableLiveData<Integer> plazasLibre = new MutableLiveData<>();

        AtomicInteger totalPlazas = new AtomicInteger(0);

        MutableLiveData<List<Long>> plazasIdInThatHoursLiveData = new MutableLiveData<>();
        List<Long> plazasIdInThatHours = new ArrayList<>();

        Timestamp horaActualTimestamp = new Timestamp(horaActual, 0);

        // La fecha no se puede comparar en la query, ya que es un string
        // La hora tampoco se puede comparar ya que es un subdocumento
        reservasCollection
                .whereLessThanOrEqualTo(FECHA, horaActualTimestamp)
                .get()
                .addOnSuccessListener(documentReference -> {
                            documentReference.toObjects(Reserva.class).forEach(reserva -> {
                                LocalDateTime fecha = TimeUtils.convertTimestampToLocalDateTime(reserva.getFecha());
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

        plazasIdInThatHoursLiveData.observe((LifecycleOwner) context, plazas -> {
            if (plazas.isEmpty()){
                plazasCollection.whereEqualTo(TIPO_PLAZA, tipoPlaza)
                        .get()
                        .addOnSuccessListener(documentReference -> {
                            documentReference.toObjects(Plaza.class)
                                    .forEach(plaza -> totalPlazas.getAndIncrement());
                            plazasLibre.setValue(totalPlazas.get());
                            callback.onSuccess();
                        }).addOnFailureListener(e -> callback.onFailure());
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

    public LiveData<Integer> getTotalPlazasByTipoPlaza(TipoPlaza tipoPlaza, Callback callback){
        MutableLiveData<Integer> totalPlazas = new MutableLiveData<>();
        db.collection(PLAZAS_COLLECTION)
                .whereEqualTo(TIPO_PLAZA, tipoPlaza)
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(documentReference -> {
                    totalPlazas.setValue((int) documentReference.getCount());
                    callback.onSuccess();
                }).addOnFailureListener(e -> callback.onFailure());
        return totalPlazas;
    }

    public LiveData<List<HourItem>> getAvailableHoursForDateAndTipoPlaza(LocalDate date, TipoPlaza tipoPlaza, MainActivity activity, Callback callback){
        MutableLiveData<List<HourItem>> liveData = new MutableLiveData<>();
        List<HourItem> hourItems = new ArrayList<>();

        boolean isToday = date.equals(LocalDate.now());

        LocalTime localTimeNow = TimeUtils.convertEpochTolocalDateTime(TimeUtils.getNowEpoch()).toLocalTime();

        boolean shouldAddAnHour = isToday && localTimeNow.getMinute() >= 30;

        LocalTime todayStartLocalTime =
                shouldAddAnHour ?
                        localTimeNow.plusHours(1).withMinute(0).withSecond(0)
                        : localTimeNow.withMinute(30).withSecond(0);

        LocalTime startOfAvailableHoursLocalTime =
                isToday ?
                        todayStartLocalTime
                        : LocalTime.of(7, 0, 0);

        LocalTime endOfAvailableHoursLocalTime = LocalTime.of(21, 30, 0);

        AtomicInteger completedReads = new AtomicInteger(0);
        int totalReads = (int) ChronoUnit.MINUTES.between(startOfAvailableHoursLocalTime, endOfAvailableHoursLocalTime) / 30;


        getTotalPlazasByTipoPlaza(tipoPlaza, callback).observe(activity, totalPlazas -> {
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            for (LocalTime time = startOfAvailableHoursLocalTime; time.isBefore(endOfAvailableHoursLocalTime); time = time.plusMinutes(30)){
                long epoch = TimeUtils.convertLocalTimeToEpochWithLocalDate(time, date);
                Timestamp timestamp = new Timestamp(epoch, 0);
                db.collection(RESERVAS_COLLECTION)
                    // Reservas que han empezado ha esta hora o antes
                    .whereLessThanOrEqualTo(FECHA, timestamp)
                    .get()
                    .addOnSuccessListener(documentReference -> {
                        AtomicLong count = new AtomicLong(0);
                        documentReference.toObjects(Reserva.class).forEach(reserva -> {
                            if (reserva.getHora().getHoraFin() > epoch){
                                count.getAndIncrement();
                            }
                        });
                        Date timeToAdd = new Date(epoch * 1000);
                        String timeString = df.format(timeToAdd);

                        addHourItem(totalPlazas, count, hourItems, timeString);

                        if (completedReads.incrementAndGet() == totalReads) {
                            Collections.sort(hourItems);
                            liveData.setValue(hourItems);
                            callback.onSuccess();
                        }

                    }).addOnFailureListener(e -> callback.onFailure());
            }
        });


        return liveData;
    }

    private static void addHourItem(Integer totalPlazas, AtomicLong count, List<HourItem> hourItems, String timeString) {
        if (count.get() >= totalPlazas){
            hourItems.add(new HourItem(timeString, false));
        } else {
            hourItems.add(new HourItem(timeString, true));
        }
    }

}