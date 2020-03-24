package com.example.diploma_hospital.ui.doctor;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diploma_hospital.MainActivity;
import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.CreateUser;
import com.example.diploma_hospital.model.Note;
import com.example.diploma_hospital.model.NoteView;
import com.example.diploma_hospital.ui.ListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.DecompositionType.VERTICAL;

public class DoctorFragment extends Fragment {
    List<NoteView> doctorTempUser = new ArrayList<NoteView>();
    List<String> nsw = new ArrayList<String>();
    private DoctorViewModel mViewModel;
    AlertDialog.Builder builder;
    String docId;
    RecyclerView recyclerView;
    String docName;
    List<NoteView> nv;
    ListAdapter doctorAdapter;
    Note checkNote;
    String num;

    public static DoctorFragment newInstance() {
        return new DoctorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 5. set item animator to DefaultAnimator
        return inflater.inflate(R.layout.doctor_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nv = new ArrayList<NoteView>();
        recyclerView = view.findViewById(R.id.recyclerViewDoctor);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Notes");
        refDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                try {
                    if (dataSnapshot != null) {
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            checkNote = issue.getValue(Note.class);
                            if (checkNote.getDoctorId().equals(currentId)) {
                                readData(new MyCallback() {
                                    @Override
                                    public void onCallback(List<NoteView> listNoteView) {
                                        Log.d("zb",doctorTempUser.size()+" ");
                                    }
                                });
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                writeData(new MyCallback() {
                    @Override
                    public void onCallback(List<NoteView> listNoteView) {
                        doctorAdapter = new ListAdapter(listNoteView, getActivity());
                        recyclerView.setAdapter(doctorAdapter);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            nav_Menu.findItem(R.id.nav_doctor).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_home).setVisible(false);
            nav_Menu.findItem(R.id.nav_notes).setVisible(false);
            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            NavigationView navView = getActivity().findViewById(R.id.nav_view);
            View headerView = navView.getHeaderView(0);
            final TextView nameTvCheck = headerView.findViewById(R.id.userName);
            final ImageView imageViewCheck = headerView.findViewById(R.id.logoHeader);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String nameCheck = dataSnapshot.child(userId).child("name").getValue(String.class);
                    imageViewCheck.setImageResource(R.drawable.doctor);
                    nameTvCheck.setText(nameCheck);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            nav_Menu.findItem(R.id.nav_doctor).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_notes).setVisible(true);
            builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false)
                    .setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Navigation.findNavController(getView()).navigate(R.id.action_nav_doctor_to_nav_login);
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Авторизируйтесь для данного действия!");
            alert.show();
        }
    }

    public void readData(final MyCallback myCallback) {

        DatabaseReference refNum = FirebaseDatabase.getInstance().getReference().child("Users").child(checkNote.userId).child("number");
        refNum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                num = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(checkNote.userId).child("name");
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NoteView nv1 = new NoteView(dataSnapshot.getValue().toString(), checkNote.time, num);
                doctorTempUser.add(nv1);
                myCallback.onCallback(doctorTempUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void writeData(final MyCallback myCallback) {
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(checkNote.userId).child("name");
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("zb2",doctorTempUser.size()+" ");

                myCallback.onCallback(doctorTempUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public interface MyCallback{
        void onCallback(List<NoteView> listNoteView);
    }

}
