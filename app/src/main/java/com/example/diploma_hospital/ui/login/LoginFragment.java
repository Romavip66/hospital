package com.example.diploma_hospital.ui.login;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diploma_hospital.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    TextInputLayout emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
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
        progressDialog = new ProgressDialog(getContext());

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Navigation.findNavController(Objects.requireNonNull(getView())).navigate(R.id.action_nav_login_to_nav_signup);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    final String userId = mFirebaseAuth.getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String roleId = dataSnapshot.child(userId).child("roleId").getValue(String.class);
                            if(roleId.equals("3")){
                                progressDialog.dismiss();
                                Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_home);
                            }
                            else if(roleId.equals("2")){
                                progressDialog.dismiss();
                                Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_doctor);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    //Toast.makeText(getContext(),"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getEditText().getText().toString().trim();
                String pwd = password.getEditText().getText().toString().trim();
                if(email.isEmpty()){
                    emailId.setError("Введите свою почту!");
                    emailId.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Введите свой пароль!");
                    password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(getContext(),"Заполните поля!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    progressDialog.show();
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),"Ошибка входа, Повторите попытку",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                final String userId = mFirebaseAuth.getCurrentUser().getUid();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String roleId = dataSnapshot.child(userId).child("roleId").getValue(String.class);
                                        if(roleId.equals("3")){
                                            progressDialog.dismiss();
                                            Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_home);
                                        }
                                        else if(roleId.equals("2")){
                                            progressDialog.dismiss();
                                            Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_doctor);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Возникла ошибка...",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
