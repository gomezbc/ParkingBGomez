package com.lksnext.ParkingBGomez.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentAboutMeBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.domain.UserInfo;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class AboutMeSettingsFragment extends Fragment {

    private static final String TAG = "AboutMeSettingsFragment";
    private UserInfo userInfo = new UserInfo();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAboutMeBinding binding = FragmentAboutMeBinding.inflate(inflater);

        FirebaseUser user = DataRepository.getInstance().getCurrentUser();
        userInfo.setUuid(user.getUid());

        binding.username.setText(user.getDisplayName());
        binding.email.setText(user.getEmail());
        binding.phone.setText(user.getPhoneNumber());

        Uri userPhotoURI = user.getPhotoUrl();
        if (userPhotoURI != null) {
            var imageLoader = new ImageLoader.Builder(getContext())
                    .crossfade(true)
                    .build();
            var request = new ImageRequest.Builder(getContext())
                    .data(userPhotoURI)
                    .crossfade(true)
                    .target(binding.avatar)
                    .build();

            imageLoader.enqueue(request);
        }else {
            binding.avatar.setImageResource(R.drawable.default_avatar);
        }

        retriveUserInfo(user, binding);

        binding.btnSave.setOnClickListener(v -> {
            // Save changes
            String newUsername = String.valueOf(binding.username.getText());
            String newPhone = String.valueOf(binding.phone.getText());
            if (!newUsername.equals(user.getDisplayName())){
                setNewUsername(newUsername);
            }
            if (userInfo.getPhone() == null || !newPhone.equals(userInfo.getPhone())){
                setNewPhone(newPhone);
            }

        });

        return binding.getRoot();
    }

    private void retriveUserInfo(FirebaseUser user, FragmentAboutMeBinding binding) {
        LiveData<UserInfo> userInfoLiveData = getUserInfoByUuid(user);

        userInfoLiveData.observe(getViewLifecycleOwner(), userInfo1 -> {
            if (userInfo1 != null) {
                binding.phone.setText(userInfo1.getPhone());
                this.userInfo = userInfo1;
            }
            userInfoLiveData.removeObservers(getViewLifecycleOwner());
        });
    }

    private void setNewPhone(String newPhone) {
        userInfo.setPhone(newPhone);
        DataRepository.getInstance().updateUserInfo(userInfo, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Phone updated successfully");
                Toast.makeText(requireContext(), "Tus datos se han actualizado correctamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "Phone failed to update");
                Toast.makeText(requireContext(), "Hubo un problema al intentar actualizar tus datos. Por favor, inténtalo de nuevo más tarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static LiveData<UserInfo> getUserInfoByUuid(FirebaseUser user) {
        return DataRepository.getInstance().getUserInfoByUuid(user.getUid(), new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "User info fetched successfully");
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "User info failed to fetch");
            }
        });
    }

    private void setNewUsername(String newUsername) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();
        DataRepository.getInstance().updateProfile(request, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Display name updated successfully");
                Toast.makeText(requireContext(), "Tus datos se han actualizado correctamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "Display name failed to update");
                Toast.makeText(requireContext(), "Hubo un problema al intentar actualizar tus datos. Por favor, inténtalo de nuevo más tarde", Toast.LENGTH_SHORT).show();

            }
        });
    }
}