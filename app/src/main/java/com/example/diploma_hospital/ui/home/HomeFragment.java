package com.example.diploma_hospital.ui.home;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.service.autofill.Dataset;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
    CardView cardView;
    Button btnNote;
    FirebaseAuth mFirebaseAuth;
    TextView name, tvTime;
    ImageView imageView;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    Spinner spinner, spinnerDoctor;
    TextView temp;
    ProgressDialog progressDialog2;
    DatabaseReference notesDatabase;
    Note note;
    String did;
    Calendar c;
    String curDate, curTime, finalTime;
    List<String> dateAndTime = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
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
        tvTime = getView().findViewById(R.id.tvTimeCheck);
        tvTime.setVisibility(View.INVISIBLE);
        cardView = getView().findViewById(R.id.cardViewCheck);
        cardView.setVisibility(View.INVISIBLE);
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
                    categories.add("Выберите категорию");
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
                                    doctorsList.add("Выберите врача");
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
                                                c = Calendar.getInstance();
                                                final int day = c.get(Calendar.DAY_OF_MONTH);
                                                final int month = c.get(Calendar.MONTH);
                                                final int year = c.get(Calendar.YEAR);
                                                final int hours = c.get(Calendar.HOUR_OF_DAY);
                                                final int mins = c.get(Calendar.MINUTE);
                                                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                                    @Override
                                                    public void onDateSet(DatePicker view, int myear, int mmonth, int mdayOfMonth) {
                                                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                                                        Date date = new Date(myear, mmonth, mdayOfMonth - 1);
                                                        final String dayOfWeek = simpledateformat.format(date);
                                                        int month1 = mmonth + 1;

                                                        if (month1 < 10) {
                                                            if (mdayOfMonth < 10) {
                                                                String added = "0"+mdayOfMonth;
                                                                curDate = ( added + "-" + 0 + month1 + "-" + myear);
                                                            } else {
                                                                curDate = (mdayOfMonth + "-" + 0 + month1 + "-" + myear);
                                                            }
                                                        }
                                                        if (month1 >= 10) {
                                                            if (mdayOfMonth < 10) {
                                                                String added = "0"+mdayOfMonth;
                                                                curDate = (added + "-" + month1 + "-" + myear);
                                                            } else {
                                                                curDate = (mdayOfMonth + "-" + month1 + "-" + myear);
                                                            }
                                                        }
                                                        timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                                            @Override
                                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                                                if (minute < 10) {
                                                                    if (hourOfDay < 10) {
                                                                        String cor = "0" + hourOfDay;
                                                                        curTime = (cor + ":" + 0 + minute);
                                                                    } else {
                                                                        curTime = (hourOfDay + ":" + 0 + minute);
                                                                    }
                                                                } else {
                                                                    if (hourOfDay < 10) {
                                                                        String cor = "0" + hourOfDay;
                                                                        curTime = (cor + ":" + minute);
                                                                    } else {
                                                                        curTime = (hourOfDay + ":" + minute);
                                                                    }
                                                                }
                                                                finalTime = (curDate + " " + curTime);
                                                                if (hourOfDay > 18 || hourOfDay < 9 || hourOfDay > 12 && hourOfDay < 14 || dayOfWeek.equals("суббота") || dayOfWeek.equals("воскресенье")) {
                                                                    Toast.makeText(getContext(), "Выберите рабочее время!", Toast.LENGTH_SHORT).show();
                                                                    //datePickerDialog.updateDate(year, month, day);
                                                                    datePickerDialog.show();
                                                                } else {
                                                                    tvTime.setText(finalTime);
                                                                    tvTime.setVisibility(View.VISIBLE);
                                                                    cardView.setVisibility(View.VISIBLE);
                                                                    btnNote.setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                        }, hours, mins, false);
                                                        timePickerDialog.setCanceledOnTouchOutside(false);
                                                        timePickerDialog.show();
                                                    }
                                                }, year, month, day);
                                                datePickerDialog.updateDate(year, month, day);
                                                datePickerDialog.setCanceledOnTouchOutside(false);
                                                final String selectedDoctor = parent.getItemAtPosition(position).toString();
                                                if (!selectedDoctor.isEmpty()) {
                                                    for (CreateUser cu : doctorTempUser) {
                                                        if (selectedDoctor.equals(cu.getName())) {
                                                            did = cu.getUid();
                                                            note = new Note("0", mFirebaseAuth.getCurrentUser().getUid(), cu.getUid());
                                                            datePickerDialog.show();
                                                        }
                                                    }// do your stuff
                                                }
                                                DatabaseReference dbzb = FirebaseDatabase.getInstance().getReference("Notes");
                                                dbzb.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                                            try {


                                                                if (did.equals(issue.child("doctorId").getValue().toString())) {
                                                                    dateAndTime.add(issue.child("time").getValue().toString());
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
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
                    final String tempDoctorId = note.doctorId;
                    String tempSelectedUid = note.userId;
                    final String childName = finalTime + "@" + tempDoctorId;
                    final Note currentNote = new Note(finalTime, tempSelectedUid, tempDoctorId);


                    DatabaseReference checkDb = FirebaseDatabase.getInstance().getReference("Notes").child(childName);
                    checkDb.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("doctorId").equals(tempDoctorId) && dataSnapshot.child("time").equals(finalTime)) {
                                    Toast.makeText(getContext(), "Данное время уже занято!", Toast.LENGTH_LONG).show();
                                    int day = c.get(Calendar.DAY_OF_MONTH);
                                    int month = c.get(Calendar.MONTH);
                                    int year = c.get(Calendar.YEAR);
                                    //datePickerDialog.updateDate(year, month, day);
                                    datePickerDialog.show();
                                    progressDialog2.dismiss();
                                } else {
                                    progressDialog2.dismiss();
                                }
                            } else {
                                List<String> forCheck = new ArrayList<>();
                                String str = finalTime;
                                String[] arrOfStr = str.split(" ", 2);
                                String[] arr2 = arrOfStr[1].split(":", 2);
                                String checkForDate = arrOfStr[0];
                                String checkForHour = arr2[0];
                                for (int i = 0; i < dateAndTime.size(); i++) {
                                    String[] a1 = dateAndTime.get(i).split(" ", 2);
                                    String[] a2 = a1[1].split(":", 2);
                                    String date1 = a1[0];
                                    String hour = a2[0];
                                    if (checkForDate.equals(date1) && checkForHour.equals(hour)) {
                                        forCheck.add("est");
                                    }
                                }
                                if (forCheck.isEmpty()) {
                                    notesDatabase.child(childName).setValue(currentNote);
                                    progressDialog2.dismiss();
                                    Toast.makeText(getContext(), "Вы успешно записались на " + finalTime, Toast.LENGTH_SHORT).show();
                                    //break;
                                } else {
                                    Toast.makeText(getContext(), "Данное время уже занято!", Toast.LENGTH_LONG).show();
                                    int day = c.get(Calendar.DAY_OF_MONTH);
                                    int month = c.get(Calendar.MONTH);
                                    int year = c.get(Calendar.YEAR);
                                    //datePickerDialog.updateDate(year, month, day);
                                    datePickerDialog.show();
                                    progressDialog2.dismiss();
                                }
                                progressDialog2.dismiss();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

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
            nav_Menu.findItem(R.id.nav_comments).setVisible(true);
            nav_Menu.findItem(R.id.nav_analyzes).setVisible(true);
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
            nav_Menu.findItem(R.id.nav_comments).setVisible(false);
            nav_Menu.findItem(R.id.nav_analyzes).setVisible(false);
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
    }

}
