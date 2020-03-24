package com.example.diploma_hospital.ui.notes;

import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.Note;
import com.example.diploma_hospital.model.NoteView;
import com.example.diploma_hospital.ui.ListAdapter;
import com.example.diploma_hospital.ui.doctor.DoctorFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private NotesViewModel mViewModel;
    FirebaseAuth mFirebaseAuth;
    TextView name;
    ImageView imageView;
    AlertDialog.Builder builder;List<NoteView> doctorTempUser = new ArrayList<NoteView>();
    List<String> nsw = new ArrayList<String>();
    String docId;
    RecyclerView recyclerView;
    String docName;
    List<NoteView> nv;
    ListAdapter doctorAdapter;
    Note checkNote;
    String num;
    String checkTime;
    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notes_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);
        // TODO: Use the ViewModel
        mFirebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nv = new ArrayList<NoteView>();
        recyclerView = getView().findViewById(R.id.recyclerViewUser);
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
                            if (checkNote.getUserId().equals(currentId)) {
                                checkTime = checkNote.getTime();
                                readData(new MyCallback() {
                                    @Override
                                    public void onCallback(List<NoteView> listNoteView) {
                                        Log.d("zb",doctorTempUser.size()+" ");
                                    }
                                }, checkTime);
                            }

                        }
                    }
                    writeData(new MyCallback() {
                        @Override
                        public void onCallback(List<NoteView> listNoteView) {
                            if (getActivity()!=null){
                                doctorAdapter = new ListAdapter(listNoteView, getActivity());
                                recyclerView.setAdapter(doctorAdapter);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        builder = new AlertDialog.Builder(getContext());
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            builder.setCancelable(false)
                    .setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Navigation.findNavController(getView()).navigate(R.id.action_nav_notes_to_nav_login);
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Авторизируйтесь для данного действия!");
            alert.show();
        }
    }
    public void readData(final MyCallback myCallback, final String time) {

        DatabaseReference refNum = FirebaseDatabase.getInstance().getReference().child("Users").child(checkNote.doctorId).child("number");
        refNum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                num = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(checkNote.doctorId).child("name");
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("checkTime", checkNote.time);
                NoteView nv1 = new NoteView(dataSnapshot.getValue().toString(), time, num);
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
