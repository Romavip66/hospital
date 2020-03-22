package com.example.diploma_hospital.ui.login;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.diploma_hospital.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = getView().findViewById(R.id.email);
        password = getView().findViewById(R.id.password);
        btnSignIn = getView().findViewById(R.id.signIn);
        tvSignUp = getView().findViewById(R.id.tvSignUp);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_nav_login_to_nav_signup);
            }
        });
    }

}
