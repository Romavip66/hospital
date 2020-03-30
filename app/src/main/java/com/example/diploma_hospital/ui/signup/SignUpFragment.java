package com.example.diploma_hospital.ui.signup;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diploma_hospital.MainActivity;
import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.CreateUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpFragment extends Fragment {

    private SignUpViewModel mViewModel;
    private TextInputLayout emailId, password, name, number, confirm;
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
        progressDialog = new ProgressDialog(getContext());
        user=FirebaseAuth.getInstance().getCurrentUser();
        emailId = Objects.requireNonNull(getView()).findViewById(R.id.editText);
        password = getView().findViewById(R.id.editText2);
        btnSignUp = getView().findViewById(R.id.button2);
        tvSignIn = getView().findViewById(R.id.textView);
        name = getView().findViewById(R.id.editText4);
        number = getView().findViewById(R.id.editText5);
        confirm = getView().findViewById(R.id.editText3);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_nav_signup_to_nav_login);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getEditText().getText().toString().trim();
                final String tempName = name.getEditText().getText().toString().trim();
                final String num = number.getEditText().getText().toString().trim();
                final String pwd = password.getEditText().getText().toString().trim();
                final String checkPwd = confirm.getEditText().getText().toString().trim();
                Log.d("n:", pwd);
                Log.d("c:", checkPwd);
                if (tempName.isEmpty()) {
                    name.setError("Введите имя!");
                    name.requestFocus();
                } else if (num.isEmpty()) {
                    number.setError("Введите свой номер!");
                    number.requestFocus();
                } else if (email.isEmpty()) {
                    emailId.setError("Введите почту!");
                    emailId.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Введите пароль!");
                    password.requestFocus();
                } else if (checkPwd.isEmpty()) {
                    confirm.setError("Введите подтверждение!");
                    confirm.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty() && tempName.isEmpty() && num.isEmpty() && checkPwd.isEmpty()) {
                    Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
                } else if(!Objects.equals(checkPwd, pwd)){
                    confirm.setError("Пароли не совпадают!");
                    confirm.requestFocus();
                }
                else if (!(email.isEmpty() && pwd.isEmpty() && tempName.isEmpty() && num.isEmpty() && checkPwd.isEmpty()) && checkPwd.equals(pwd)) {
                    progressDialog.setMessage("Подождите, мы добавляем пользователя...");
                    progressDialog.show();
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userId = mFirebaseAuth.getCurrentUser().getUid();
                                CreateUser createUser = new CreateUser( tempName,num,email,pwd,"0","3", userId, "0");
                                reference.child(userId).setValue(createUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Пользователь зарегистрирован успешно!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_nav_signup_to_nav_home);
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Ошибка соединения, обратитесь в сервис!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Ошибка соединения или данная почта уже занята!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Ошибка соединения, обратитесь в сервис!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
