package com.example.diploma_hospital.ui.analyzes;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diploma_hospital.R;
import com.example.diploma_hospital.model.NoteView;
import com.example.diploma_hospital.ui.ListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnalyzesFragment extends Fragment {

    private AnalyzesViewModel mViewModel;
    RecyclerView recyclerView;
    ListAdapter la;

    public static AnalyzesFragment newInstance() {
        return new AnalyzesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.analyzes_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AnalyzesViewModel.class);
        // TODO: Use the ViewModel
        recyclerView = getView().findViewById(R.id.recAnalyzes);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            final String cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            recyclerView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Analyzes");
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<NoteView> list = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("uid").getValue().toString().equals(cuid)) {
                            list.add(new NoteView(ds.child("name").getValue().toString(), ds.child("status").getValue().toString(), " ", " "));
                        }
                    }
                    try {
                        la = new ListAdapter(list, getActivity());
                        recyclerView.setAdapter(la);
                        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            recyclerView.setVisibility(View.INVISIBLE);
        }

    }

}
