package com.lksnext.ParkingBGomez.data;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DataRepository {

    private static final String RESERVAS_COLLECTION = "reservas";
    private static final String PLAZAS_COLLECTION = "plazas";
    private static final String TIPO_PLAZA = "tipoPlaza";
    private static final String USUARIO = "usuario";
    private static final String FECHA = "fecha";

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

    public void deleteReserva(Reserva reserva, MainActivity activity, Callback callback){
        activity.getDb().collection(RESERVAS_COLLECTION).document(reserva.getUuid())
                .delete()
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public void updateReserva(Reserva reserva, MainActivity activity, Callback callback){
        activity.getDb().collection(RESERVAS_COLLECTION).document(reserva.getUuid())
                .set(reserva)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public LiveData<Map<LocalDate, List<Reserva>>> getReservasByDayByUser(String user, MainActivity activity, Callback callback){
        MutableLiveData<Map<LocalDate, List<Reserva>>> liveData = new MutableLiveData<>();
        // TreeMap to Order by date ascending
        Map<LocalDate, List<Reserva>> reservasByDay = new TreeMap<>();
        final long firstDayOfMonth =
                TimeUtils.convertLocalDateTimeToEpoch(LocalDate.now().withDayOfMonth(1).atStartOfDay());

        Timestamp firstTimestampOfTheMonth = new Timestamp(firstDayOfMonth, 0);

        activity.getDb().collection(RESERVAS_COLLECTION)
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


    public LiveData<List<Reserva>> getActiveReservasOfUser(String user, MainActivity activity, Callback callback){
        MutableLiveData<List<Reserva>> liveData = new MutableLiveData<>();
        List<Reserva> reservas = new ArrayList<>();
        long nowEpoch = TimeUtils.getNowEpoch();

        activity.getDb().collection(RESERVAS_COLLECTION)
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

    public LiveData<Plaza> getPlazaNotReservadaByTipoPlaza(TipoPlaza tipoPlaza, Hora hora, MainActivity activity, Callback callback){
        CollectionReference plazasCollection = activity.getDb().collection(PLAZAS_COLLECTION);
        CollectionReference reservasCollection = activity.getDb().collection(RESERVAS_COLLECTION);

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

        plazasIdInThatHoursLiveData.observe(activity, plazas -> {
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

    public LiveData<Integer> getPlazasLibresByTipoPlaza(TipoPlaza tipoPlaza, long horaActual ,MainActivity activity, Callback callback){
        CollectionReference plazasCollection = activity.getDb().collection(PLAZAS_COLLECTION);
        CollectionReference reservasCollection = activity.getDb().collection(RESERVAS_COLLECTION);

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

        plazasIdInThatHoursLiveData.observe(activity, plazas -> {
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

    public LiveData<Integer> getTotalPlazasByTipoPlaza(TipoPlaza tipoPlaza, MainActivity activity, Callback callback){
        MutableLiveData<Integer> totalPlazas = new MutableLiveData<>();
        activity.getDb().collection(PLAZAS_COLLECTION)
                .whereEqualTo(TIPO_PLAZA, tipoPlaza)
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(documentReference -> {
                    totalPlazas.setValue((int) documentReference.getCount());
                    callback.onSuccess();
                }).addOnFailureListener(e -> callback.onFailure());
        return totalPlazas;
    }

}