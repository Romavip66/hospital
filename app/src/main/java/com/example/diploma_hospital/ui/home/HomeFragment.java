package com.example.diploma_hospital.ui.home;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diploma_hospital.MainActivity;
import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.CreateUser;
import com.example.diploma_hospital.model.Note;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    Button btnNote;
    FirebaseAuth mFirebaseAuth;
    TextView name;
    ImageView imageView;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    Spinner spinner, spinnerDoctor;
    TextView temp;
    ProgressDialog progressDialog2;
    DatabaseReference notesDatabase;
    Note note;
    private boolean isSpinnerTouched = false;
    private boolean isDoctorTouched = false;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mFirebaseAuth = FirebaseAuth.getInstance();
        notesDatabase = FirebaseDatabase.getInstance().getReference("Notes");
        spinner = (Spinner) getView().findViewById(R.id.spinnerCategory);
        spinnerDoctor = (Spinner) getView().findViewById(R.id.spinnerDoctor);
        btnNote = getView().findViewById(R.id.btnNote);
        btnNote.setVisibility(View.INVISIBLE);
        spinnerDoctor.setVisibility(View.INVISIBLE);
        note = new Note();
        temp = getView().findViewById(R.id.checkHome);
        progressDialog2 = new ProgressDialog(getContext());

        if (mFirebaseAuth.getCurrentUser() != null) {
            temp.setVisibility(View.INVISIBLE);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final List<String> categories = new ArrayList<String>();
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String category = areaSnapshot.getValue(String.class);
                        categories.add(category);
                    }
                    try {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
                        spinner.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            spinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isSpinnerTouched = true;
                    return false;
                }
            });

            spinnerDoctor.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isDoctorTouched = true;
                    return false;
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!isSpinnerTouched) return;
                    else {
                        final String selectedCategory = parent.getItemAtPosition(position).toString();
                        if (!selectedCategory.isEmpty()) {
                            final String doctorId = null;
                            progressDialog2.setMessage("Загрузка");
                            progressDialog2.show();
                            DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Users");
                            refDoctor.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final List<String> doctorsList = new ArrayList<String>();
                                    final List<CreateUser> doctorTempUser = new ArrayList<CreateUser>();
                                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                        try {
                                            CreateUser checkDoctor = issue.getValue(CreateUser.class);
                                            if (checkDoctor.getCategory().equals(selectedCategory)) {
                                                doctorsList.add(checkDoctor.getName());
                                                doctorTempUser.add(checkDoctor);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.d("sizecheck", doctorsList.size() + " ");
                                    if (!doctorsList.isEmpty()) {
                                        if (getActivity() != null) {
                                            ArrayAdapter<String> adapterDoctor = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, doctorsList);
                                            spinnerDoctor.setAdapter(adapterDoctor);
                                            spinnerDoctor.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Врачи отсуствуют!!!", Toast.LENGTH_SHORT).show();
                                        spinnerDoctor.setVisibility(View.INVISIBLE);
                                        btnNote.setVisibility(View.INVISIBLE);
                                    }
                                    progressDialog2.dismiss();
                                    spinnerDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if (!isDoctorTouched) return;
                                            else {
                                                final String selectedDoctor = parent.getItemAtPosition(position).toString();
                                                if (!selectedDoctor.isEmpty()) {
                                                    for (CreateUser cu : doctorTempUser) {
                                                        if (selectedDoctor.equals(cu.getName())) {
                                                            note = new Note("0", mFirebaseAuth.getCurrentUser().getUid(), cu.getUid());
                                                            btnNote.setVisibility(View.VISIBLE);
                                                        }
                                                    }// do your stuff
                                                }
                                            }
                                        } // to close the onItemSelected

                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                } // to close the onItemSelected

                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btnNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog2.setMessage("Подождите...");
                    progressDialog2.show();
                    String tempDoctorId = note.doctorId;
                    String tempSelectedUid = note.userId;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
                    String date = sdf.format(new Date());
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(sdf.parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.add(Calendar.DATE, 1);  // number of days to add
                    date = sdf.format(c.getTime());
                    Note currentNote = new Note(date, tempSelectedUid, tempDoctorId);
                    notesDatabase.child(date).setValue(currentNote);
                    progressDialog2.dismiss();
                    Toast.makeText(getContext(), "Вы успешно записались на " + date, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            spinner.setVisibility(View.INVISIBLE);
            temp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Загрузка...");
        progressDialog.show();
        builder = new AlertDialog.Builder(getContext());
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            NavigationView navView = getActivity().findViewById(R.id.nav_view);
            View headerView = navView.getHeaderView(0);
            name = headerView.findViewById(R.id.userName);
            imageView = headerView.findViewById(R.id.logoHeader);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nameCheck = dataSnapshot.child(userId).child("name").getValue(String.class);
                    imageView.setImageResource(R.drawable.user);
                    name.setText(nameCheck);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            builder.setCancelable(false)
                    .setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_nav_login);
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Авторизируйтесь для данного действия!");
            alert.show();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
      /*  try {
            final String currentLoggedUid = mFirebaseAuth.getCurrentUser().getUid();
            DatabaseReference refUserCheck = FirebaseDatabase.getInstance().getReference().child("Users");
            refUserCheck.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (currentLoggedUid.equals(dataSnapshot.getValue())) {
                        if (dataSnapshot.child(currentLoggedUid).child("roleId").equals("2")) {
                            Navigation.findNavController(getView()).navigate(R.id.action_nav_home_to_nav_doctor);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
