package com.example.diploma_hospital.ui.signup;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.CreateUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class SignUpFragment extends Fragment {

    private SignUpViewModel mViewModel;
    private EditText emailId, password, name, number, confirm;
    private Button btnSignUp;
    private TextView tvSignIn;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private String userId;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        mFirebaseAuth = FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        emailId = Objects.requireNonNull(getView()).findViewById(R.id.editText);
        password = getView().findViewById(R.id.editText2);
        btnSignUp = getView().findViewById(R.id.button2);
        tvSignIn = getView().findViewById(R.id.textView);
        name = getView().findViewById(R.id.editText4);
        number = getView().findViewById(R.id.editText5);
        confirm = getView().findViewById(R.id.editText3);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_nav_signup_to_nav_login);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                final String tempName = name.getText().toString();
                final String num = number.getText().toString();
                final String pwd = password.getText().toString();
                final String checkPwd = confirm.getText().toString();
                if (tempName.isEmpty()) {
                    name.setError("Please, enter your name");
                    name.requestFocus();
                } else if (num.isEmpty()) {
                    number.setError("Please, enter your phone number");
                    number.requestFocus();
                } else if (email.isEmpty()) {
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (checkPwd.isEmpty()) {
                    confirm.setError("Please, confirm password");
                    confirm.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty() && tempName.isEmpty() && num.isEmpty() && checkPwd.isEmpty()) {
                    Toast.makeText(getContext(), "Fields Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pwd.isEmpty() && tempName.isEmpty() && num.isEmpty() && checkPwd.isEmpty()) && checkPwd.equals(pwd)) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_nav_signup_to_nav_notes);

                                /*user = mFirebaseAuth.getCurrentUser();
                                userId = user.getUid();
                                CreateUser createUser = new CreateUser(tempName, num, email, pwd, "0", "3", userId);
                                reference.child("userId").setValue(createUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Пользователь зарегистрирован успешно!", Toast.LENGTH_SHORT).show();
                                            Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_nav_signup_to_nav_notes);
                                        } else {
                                            Toast.makeText(getContext(), "Ошибка соединения, обратитесь в сервис!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });*/
                            } else {
                                Toast.makeText(getContext(), "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
