package com.example.diploma_hospital.ui.first;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diploma_hospital.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirstFragment extends Fragment {

    private FirstViewModel mViewModel;
    TextView tv;
    ProgressDialog pd;

    public static FirstFragment newInstance() {
        return new FirstFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FirstViewModel.class);
        tv = getView().findViewById(R.id.lorem);
        tv.setText(R.string.loremIpsum);
        // TODO: Use the ViewModel
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Поджодите");
        pd.show();
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        try {
            final Menu nav_Menu = navigationView.getMenu();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                nav_Menu.findItem(R.id.nav_login).setVisible(false);
                nav_Menu.findItem(R.id.nav_logout).setVisible(true);
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (userId != null) {
                            try {
                            NavigationView navView = getActivity().findViewById(R.id.nav_view);
                            View headerView = navView.getHeaderView(0);
                            String nameCheck = dataSnapshot.child(userId).child("name").getValue(String.class);
                            final TextView nameTvCheck = headerView.findViewById(R.id.userName);
                            final ImageView imageViewCheck = headerView.findViewById(R.id.logoHeader);
                            String roleId = dataSnapshot.child(userId).child("roleId").getValue(String.class);
                            nameTvCheck.setText(nameCheck);
                            if (roleId.equals("3")) {
                                imageViewCheck.setImageResource(R.drawable.user);
                                nav_Menu.findItem(R.id.nav_home).setVisible(true);
                                nav_Menu.findItem(R.id.nav_notes).setVisible(true);
                                nav_Menu.findItem(R.id.nav_comments).setVisible(true);
                                nav_Menu.findItem(R.id.nav_doctor).setVisible(false);
                                nav_Menu.findItem(R.id.nav_analyzes).setVisible(true);
                                pd.dismiss();
                            } else {
                                imageViewCheck.setImageResource(R.drawable.doctor);
                                nav_Menu.findItem(R.id.nav_home).setVisible(false);
                                nav_Menu.findItem(R.id.nav_analyzes).setVisible(false);
                                nav_Menu.findItem(R.id.nav_notes).setVisible(false);
                                nav_Menu.findItem(R.id.nav_comments).setVisible(false);
                                nav_Menu.findItem(R.id.nav_doctor).setVisible(true);
                                pd.dismiss();
                            }
                        }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                pd.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
