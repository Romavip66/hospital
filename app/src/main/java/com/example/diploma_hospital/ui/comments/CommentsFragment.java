package com.example.diploma_hospital.ui.comments;

import androidx.lifecycle.ViewModelProviders;

import android.app.AppOpsManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.CreateUser;
import com.example.diploma_hospital.model.Desc;
import com.example.diploma_hospital.model.NoteView;
import com.example.diploma_hospital.ui.ListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsFragment extends Fragment {

    private CommentsViewModel mViewModel;
    String descrip;
    Spinner spinner, spinnerDoctor;
    TextView tvName, tvCategory, description;
    ImageView imv;
    EditText coms;
    RecyclerView recCom;
    Button btnCom;
    String curName;
    ListAdapter la;
    private boolean isSpinnerTouched = false;
    private boolean isDoctorTouched = false;
    FirebaseAuth mFirebaseAuth;

    public static CommentsFragment newInstance() {
        return new CommentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comments_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CommentsViewModel.class);
        // TODO: Use the ViewModel
        mFirebaseAuth = FirebaseAuth.getInstance();
        recCom = getView().findViewById(R.id.recyclerViewCom);
        spinner = getView().findViewById(R.id.spinnerCategory2);
        spinnerDoctor = getView().findViewById(R.id.spinnerDoctor2);
        //tvName = getView().findViewById(R.id.textViewDoc);
        //tvCategory = getView().findViewById(R.id.textViewCategory);
        description = getView().findViewById(R.id.textViewDesc);
        imv = getView().findViewById(R.id.imageViewOfDoc);
        coms = getView().findViewById(R.id.comments);
        btnCom = getView().findViewById(R.id.btnComments);
        hideItems();


        if (mFirebaseAuth.getCurrentUser() != null) {

            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseAuth.getCurrentUser().getUid()).child("name");
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    curName = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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
                            DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Users");
                            refDoctor.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final List<String> uidList = new ArrayList<String>();
                                    final List<String> doctorsList = new ArrayList<String>();
                                    final List<CreateUser> doctorTempUser = new ArrayList<CreateUser>();
                                    uidList.add("smth");
                                    doctorsList.add("Выберите врача");
                                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                        try {
                                            CreateUser checkDoctor = issue.getValue(CreateUser.class);
                                            if (checkDoctor.getCategory().equals(selectedCategory)) {
                                                uidList.add(checkDoctor.getUid());
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
                                        //    tvName.setVisibility(View.INVISIBLE);
                                        //  tvCategory.setVisibility(View.INVISIBLE);
                                        description.setVisibility(View.INVISIBLE);
                                        imv.setVisibility(View.INVISIBLE);
                                        coms.setVisibility(View.INVISIBLE);
                                        btnCom.setVisibility(View.INVISIBLE);
                                    }
                                    spinnerDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if (!isDoctorTouched) return;
                                            else {
                                                if (position > 0) {
                                                    final String selectedCategory2 = parent.getItemAtPosition(position).toString();
                                                    final String selectedUid = uidList.get(position);
                                                    //  tvName.setVisibility(View.VISIBLE);
                                                    //tvCategory.setVisibility(View.VISIBLE);
                                                    recCom.setVisibility(View.VISIBLE);
                                                    description.setVisibility(View.VISIBLE);
                                                    imv.setVisibility(View.VISIBLE);
                                                    coms.setVisibility(View.VISIBLE);
                                                    btnCom.setVisibility(View.VISIBLE);

                                                    readData(new MyCallback() {
                                                        @Override
                                                        public void onCallback(String descri) {

                                                            description.setText(descri);
                                                        }
                                                    }, selectedUid);

                                                    //  tvName.setText(selectedCategory2);
                                                    //   tvCategory.setText(selectedCategory);
                                                    //STOPPED HERE

                                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                                    recCom.setLayoutManager(layoutManager);
                                                    DatabaseReference rc = FirebaseDatabase.getInstance().getReference("Comments").child(selectedUid);
                                                    rc.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            List<NoteView> dsc = new ArrayList<NoteView>();
                                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                for (DataSnapshot ds2 : ds.getChildren()) {
                                                                    dsc.add(new NoteView(ds2.getValue().toString(), ds2.getKey().toString(), " ", selectedUid));
                                                                }
                                                            }
                                                            try {


                                                                la = new ListAdapter(dsc, getActivity());
                                                                recCom.setAdapter(la);
                                                                recCom.addItemDecoration(new DividerItemDecoration(recCom.getContext(), DividerItemDecoration.VERTICAL));
                                                            }catch (Exception e){
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                    btnCom.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String val = coms.getText().toString();
                                                            if (!val.isEmpty()) {
                                                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                                Date date = new Date();
                                                                String strDate = sdf.format(date);
                                                                DatabaseReference rb1 = FirebaseDatabase.getInstance().getReference("Comments").child(selectedUid).child(strDate);
                                                                rb1.child(curName).setValue(val);
                                                                Toast.makeText(getContext(), "Ваш комментарий добавлен!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                } else {
                                                    recCom.setVisibility(View.INVISIBLE);
                                                    // tvName.setVisibility(View.INVISIBLE);
                                                    //  tvCategory.setVisibility(View.INVISIBLE);
                                                    description.setVisibility(View.INVISIBLE);
                                                    imv.setVisibility(View.INVISIBLE);
                                                    coms.setVisibility(View.INVISIBLE);
                                                    btnCom.setVisibility(View.INVISIBLE);

                                                }
                                            }
                                        }

                                        @Override
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
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


        }
    }

    private void hideItems() {
        recCom.setVisibility(View.INVISIBLE);
        spinnerDoctor.setVisibility(View.INVISIBLE);
        //tvName.setVisibility(View.INVISIBLE);
        // tvCategory.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        imv.setVisibility(View.INVISIBLE);
        coms.setVisibility(View.INVISIBLE);
        btnCom.setVisibility(View.INVISIBLE);
    }

    public interface MyCallback {
        void onCallback(String descri);
    }

    public void readData(final MyCallback myCallback, final String selectedUid) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(selectedUid);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String vals = dataSnapshot.child("description").getValue().toString();
                myCallback.onCallback(vals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
