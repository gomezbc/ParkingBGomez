package com.lksnext.ParkingBGomez;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.Reserva;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

public class DataRepositoryTest {

    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockFirebaseUser;
    @Mock
    private CollectionReference mockCollection;
    @Mock
    private DocumentReference mockDocument;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    private DataRepository dataRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dataRepository = new DataRepository(mockFirestore, mockAuth);
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        Callback callback = mock(Callback.class);

        // Create a TaskCompletionSource
        TaskCompletionSource<AuthResult> taskCompletionSource = new TaskCompletionSource<>();
        // Get the Task from the TaskCompletionSource
        Task<AuthResult> task = taskCompletionSource.getTask();
        // Make the mock return this task when signInWithEmailAndPassword is called
        when(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(task);

        // Define behavior for callback.onSuccess()
        doAnswer(invocation -> {
            // Act
            dataRepository.signUp(email, password, callback);

            // Assert
            verify(mockAuth).signInWithEmailAndPassword(email, password);
            verify(callback).onSuccess();
            return null;
        }).when(callback).onSuccess();
    }

    @Test
    public void testSignUp_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        Callback callback = mock(Callback.class);

        // Create a TaskCompletionSource
        TaskCompletionSource<AuthResult> taskCompletionSource = new TaskCompletionSource<>();
        // Get the Task from the TaskCompletionSource
        Task<AuthResult> task = taskCompletionSource.getTask();
        // Make the mock return this task when createUserWithEmailAndPassword is called
        when(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(task);

        // Define behavior for callback.onSuccess()
        doAnswer(invocation -> {
            // Act
            dataRepository.signUp(email, password, callback);

            // Assert
            verify(mockAuth).createUserWithEmailAndPassword(email, password);
            verify(callback).onSuccess();
            return null;
        }).when(callback).onSuccess();

        // Complete the task successfully
        taskCompletionSource.setResult(null);
    }

    @Test
    public void testLogout() {
        // Act
        dataRepository.logout();

        // Assert
        verify(mockAuth).signOut();
    }

    @Test
    public void testResetPassword_Success() {
        // Arrange
        String email = "test@example.com";
        Callback callback = mock(Callback.class);

        // Create a TaskCompletionSource
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        // Get the Task from the TaskCompletionSource
        Task<Void> task = taskCompletionSource.getTask();
        // Make the mock return this task when sendPasswordResetEmail is called
        when(mockAuth.sendPasswordResetEmail(email)).thenReturn(task);


        // Define behavior for callback.onSuccess()
        doAnswer(invocation -> {
            // Act
            dataRepository.resetPassword(email, callback);

            // Assert
            verify(mockAuth).sendPasswordResetEmail(email);
            verify(callback).onSuccess();
            return null;
        }).when(callback).onSuccess();

        // Complete the task successfully
        taskCompletionSource.setResult(null);
    }

    @Test
    public void testGetCurrentUser() {
        // Arrange
        when(mockAuth.getCurrentUser()).thenReturn(mockFirebaseUser);

        // Act
        FirebaseUser user = dataRepository.getCurrentUser();

        // Assert
        verify(mockAuth).getCurrentUser();
        assertEquals(mockFirebaseUser, user);
    }

    @Test
    public void testSaveReserva_Success() {
        // Arrange
        Reserva reserva = new Reserva();
        String uuid = UUID.randomUUID().toString();
        reserva.setUuid(uuid);
        Callback callback = mock(Callback.class);

        when(mockFirestore.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        // Create a TaskCompletionSource
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        // Get the Task from the TaskCompletionSource
        Task<Void> task = taskCompletionSource.getTask();
        // Make the mock return this task when set is called
        when(mockDocument.set(any())).thenReturn(task);

        // Define behavior for callback.onSuccess()
        doAnswer(invocation -> {
            // Act
            dataRepository.saveReserva(reserva, callback);

            // Assert
            verify(mockDocument).set(reserva);
            verify(callback).onSuccess();
            return null;
        }).when(callback).onSuccess();

        // Complete the task successfully
        taskCompletionSource.setResult(null);
    }

    @Test
    public void testDeleteReserva_Success() {
        // Arrange
        Reserva reserva = new Reserva();
        String uuid = UUID.randomUUID().toString();
        reserva.setUuid(uuid);
        Callback callback = mock(Callback.class);

        when(mockFirestore.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        // Create a TaskCompletionSource
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        // Get the Task from the TaskCompletionSource
        Task<Void> task = taskCompletionSource.getTask();
        // Make the mock return this task when delete is called
        when(mockDocument.delete()).thenReturn(task);

        // Define behavior for callback.onSuccess()
        doAnswer(invocation -> {
            // Act
            dataRepository.deleteReserva(reserva, callback);

            // Assert
            verify(mockDocument).delete();
            verify(callback).onSuccess();
            return null;
        }).when(callback).onSuccess();

        // Complete the task successfully
        taskCompletionSource.setResult(null);
    }

    @Test
    public void testUpdateReserva_Success() {
        // Arrange
        Reserva reserva = new Reserva();
        String uuid = UUID.randomUUID().toString();
        reserva.setUuid(uuid);
        Callback callback = mock(Callback.class);

        when(mockFirestore.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(uuid)).thenReturn(mockDocument);

        // Create a TaskCompletionSource
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        // Get the Task from the TaskCompletionSource
        Task<Void> task = taskCompletionSource.getTask();
        // Make the mock return this task when set is called
        when(mockDocument.set(any())).thenReturn(task);

        // Define behavior for callback.onSuccess()
        doAnswer(invocation -> {
            // Act
            dataRepository.updateReserva(reserva, callback);

            // Assert
            verify(mockDocument).set(reserva);
            verify(callback).onSuccess();
            return null;
        }).when(callback).onSuccess();

        // Complete the task successfully
        taskCompletionSource.setResult(null);
    }

    @Test
    public void testGetReservaByUuid_Success() {
        // Arrange
        String uuid = UUID.randomUUID().toString();
        Callback callback = mock(Callback.class);

        when(mockFirestore.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(uuid)).thenReturn(mockDocument);

        // Create a TaskCompletionSource
        TaskCompletionSource<DocumentSnapshot> taskCompletionSource = new TaskCompletionSource<>();
        // Get the Task from the TaskCompletionSource
        Task<DocumentSnapshot> task = taskCompletionSource.getTask();
        // Make the mock return this task when get is called
        when(mockDocument.get()).thenReturn(task);

        // Define behavior for callback.onSuccess()
        doAnswer(invocation -> {
            // Act
            LiveData<Reserva> liveData = dataRepository.getReservaByUuid(uuid, callback);
            Reserva reserva = LiveDataTestUtil.getOrAwaitValue(liveData);
            assert (reserva != null);

            // Assert
            verify(mockDocument).get();
            verify(callback).onSuccess();
            return null;
        }).when(callback).onSuccess();

        // Complete the task successfully
        taskCompletionSource.setResult(mockDocumentSnapshot);
    }
}
