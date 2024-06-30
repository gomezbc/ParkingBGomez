package com.lksnext.ParkingBGomez.data;


import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import com.lksnext.ParkingBGomez.domain.UserInfo;
import com.lksnext.ParkingBGomez.enums.TipoPlaza;
import com.lksnext.ParkingBGomez.utils.TimeUtils;

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

    private static final String RESERVAS_COLLECTION = "reservas";
    private static final String PLAZAS_COLLECTION = "plazas";
    private static final String USER_INFO_COLLECTION = "userInfo";
    private static final String TIPO_PLAZA = "tipoPlaza";
    private static final String USUARIO = "usuario";
    private static final String FECHA = "fecha";
    private static final String TAG = "DataRepository";

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private static DataRepository instance;

    private DataRepository(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public DataRepository(FirebaseFirestore db, FirebaseAuth mAuth) {
        this.db = db;
        this.mAuth = mAuth;
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

    public void signInWithCredential(AuthCredential credential, Callback callback){
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener( unused -> {
                    Log.d(TAG, "signInWithCredential:success");
                    callback.onSuccess();
                }).addOnFailureListener(e -> {
                    Log.w(TAG, "signInWithCredential:failure", e);
                    callback.onFailure();
                });
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
            callback.onFailure();
        }
    }

    public void logout(){
        mAuth.signOut();
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
            Log.w(TAG, "signInWithEmail:exception", e);
            callback.onFailure();
        }
    }

    public FirebaseUser getCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "getCurrentUser: " + user);
        return user;
    }

    public void updateProfile(UserProfileChangeRequest profileUpdates, Callback callback){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            callback.onFailure();
            return;
        }
        try{
            user.updateProfile(profileUpdates)
                    .addOnSuccessListener( unused -> {
                        Log.d(TAG, "updateProfile:success");
                        callback.onSuccess();
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "updateProfile:failure", e);
                        callback.onFailure();
                    });
        } catch (Exception e){
            Log.w(TAG, "updateProfile:exception", e);
            callback.onFailure();
        }
    }

    public void updateUserInfo(UserInfo userInfo, Callback callback){
        db.collection(USER_INFO_COLLECTION)
                .document(userInfo.getUuid())
                .set(userInfo)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public LiveData<UserInfo> getUserInfoByUuid(String uuid, Callback callback){
        MutableLiveData<UserInfo> liveData = new MutableLiveData<>();
        try{
            db.collection(USER_INFO_COLLECTION)
                    .document(uuid)
                    .get()
                    .addOnSuccessListener(documentReference -> {
                        if (documentReference.exists()){
                            liveData.setValue(documentReference.toObject(UserInfo.class));
                        }
                        callback.onSuccess();
                    }).addOnFailureListener(e -> callback.onFailure());
        } catch (Exception e){
            callback.onFailure();
        }
        return liveData;
    }

    public LiveData<Reserva> getReservaByUuid(String uuid, Callback callback){
        MutableLiveData<Reserva> liveData = new MutableLiveData<>();
        db.collection(RESERVAS_COLLECTION).document(uuid)
                .get()
                .addOnSuccessListener(documentReference -> {
                    if (documentReference.exists()){
                        liveData.setValue(documentReference.toObject(Reserva.class));
                    }
                    callback.onSuccess();
                }).addOnFailureListener(e -> callback.onFailure());
        return liveData;
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

    public LiveData<Plaza> getPlazaNotReservadaByTipoPlaza(LifecycleOwner lifecycleOwner, TipoPlaza tipoPlaza, Hora hora, Callback callback){
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

        plazasIdInThatHoursLiveData.observe(lifecycleOwner, plazas -> {
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
            plazasIdInThatHoursLiveData.removeObservers(lifecycleOwner);
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

    public LiveData<Integer> getPlazasLibresByTipoPlaza(TipoPlaza tipoPlaza, long horaActual, LifecycleOwner lifecycleOwner, Callback callback){
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

        plazasIdInThatHoursLiveData.observe(lifecycleOwner, plazas -> {
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
            plazasIdInThatHoursLiveData.removeObservers(lifecycleOwner);
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

    public LiveData<List<HourItem>> getAvailableHoursForDateAndTipoPlaza(LocalDate date, TipoPlaza tipoPlaza, LifecycleOwner lifecycleOwner, Callback callback){
        Log.d(TAG, "getAvailableHoursForDateAndTipoPlaza: date: " + date);

        MutableLiveData<List<HourItem>> liveData = new MutableLiveData<>();
        List<HourItem> hourItems = new ArrayList<>();

        LocalTime startOfAvailableHoursLocalTime = getStartOfAvailableHoursLocalTime(date);
        LocalTime endOfAvailableHoursLocalTime = LocalTime.of(21, 30, 0);

        if (isToday(date) && isAfterEndOfAvailableHours(date, endOfAvailableHoursLocalTime)){
            callback.onFailure();
            liveData.setValue(hourItems);
            return liveData;
        }

        AtomicInteger completedReads = new AtomicInteger(0);
        int totalReads = getTotalReads(startOfAvailableHoursLocalTime, endOfAvailableHoursLocalTime);

        LiveData<Integer> totalPlazasByTipoPlaza = getTotalPlazasByTipoPlaza(tipoPlaza, callback);

        totalPlazasByTipoPlaza.observe(lifecycleOwner, totalPlazas -> {
            DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            for (LocalTime time = startOfAvailableHoursLocalTime; time.isBefore(endOfAvailableHoursLocalTime); time = time.plusMinutes(30)){
                long epoch = TimeUtils.convertLocalTimeToEpochWithLocalDate(time, date);
                Timestamp startOfTheDayTimeStamp = new Timestamp(TimeUtils.getStartOfTodayEpoch(), 0);
                Timestamp timestamp = new Timestamp(epoch, 0);
                // Reservas de hoy que han empezado ha esta hora o antes
                db.collection(RESERVAS_COLLECTION)
                    .where(Filter.and(Filter.greaterThanOrEqualTo(FECHA, startOfTheDayTimeStamp),
                        Filter.lessThanOrEqualTo(FECHA, timestamp)))
                    .get()
                    .addOnSuccessListener(documentReference -> {
                        AtomicLong countReservasInThatTime = new AtomicLong(0);
                        documentReference.toObjects(Reserva.class).forEach(reserva -> {
                            if (reserva.getHora().getHoraFin() > epoch){
                                countReservasInThatTime.getAndIncrement();
                            }
                        });
                        Date timeToAdd = new Date(epoch * 1000);
                        String timeString = df.format(timeToAdd);

                        // Adds the hour item to the list
                        addHourItem(totalPlazas, countReservasInThatTime, hourItems, timeString);

                        // When all the time reads are completed, sort the list and set the value of the liveData
                        if (completedReads.incrementAndGet() == totalReads) {
                            Collections.sort(hourItems);
                            liveData.setValue(hourItems);
                            callback.onSuccess();
                        }

                    }).addOnFailureListener(e -> callback.onFailure());
            }
            totalPlazasByTipoPlaza.removeObservers(lifecycleOwner);
        });


        return liveData;
    }

    private LocalTime getStartOfAvailableHoursLocalTime(LocalDate date) {
        LocalTime localTimeNow = TimeUtils.convertEpochTolocalDateTime(TimeUtils.getNowEpoch()).toLocalTime();
        boolean shouldAddAnHour = isToday(date) && localTimeNow.getMinute() >= 30;
        LocalTime todayStartLocalTime = shouldAddAnHour ? localTimeNow.plusHours(1).withMinute(0).withSecond(0) : localTimeNow.withMinute(30).withSecond(0);
        return isToday(date) ? todayStartLocalTime.minusMinutes(30) : LocalTime.of(7, 0, 0);
    }

    private boolean isToday(LocalDate date) {
        return date.equals(LocalDate.now());
    }

    private boolean isAfterEndOfAvailableHours(LocalDate date, LocalTime endOfAvailableHoursLocalTime) {
        LocalTime localTimeNow = TimeUtils.convertEpochTolocalDateTime(TimeUtils.getNowEpoch()).toLocalTime();
        return isToday(date) && localTimeNow.isAfter(endOfAvailableHoursLocalTime);
    }

    private int getTotalReads(LocalTime startOfAvailableHoursLocalTime, LocalTime endOfAvailableHoursLocalTime) {
        return (int) ChronoUnit.MINUTES.between(startOfAvailableHoursLocalTime, endOfAvailableHoursLocalTime) / 30;
    }

    /**
     * Add an HourItem to the list of HourItems
     * If the there are more reservations than totalPlazas, the HourItem is not available
     * @param totalPlazas total number of plazas for that time and tipoPlaza
     * @param count number of reservations for that time and tipoPlaza
     * @param hourItems list of HourItems
     * @param timeString time in HH:mm format
     */
    private static void addHourItem(Integer totalPlazas, AtomicLong count, List<HourItem> hourItems, String timeString) {
        if (count.get() >= totalPlazas){
            hourItems.add(new HourItem(timeString, false));
        } else {
            hourItems.add(new HourItem(timeString, true));
        }
    }

}