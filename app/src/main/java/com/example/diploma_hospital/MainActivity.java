package com.example.diploma_hospital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_login, R.id.nav_doctor, R.id.nav_notes, R.id.nav_logout, R.id.nav_signup,  R.id.nav_first)
                .setDrawerLayout(drawer)
                .build();
        hideItem();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationView navView =  findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        TextView nameCheck = headerView.findViewById(R.id.userName);
        ImageView imageViewCheck = headerView.findViewById(R.id.logoHeader);
        nameCheck.setText(getString(R.string.headerText));
        imageViewCheck.setImageResource(R.drawable.heart_logo);

    }

    private void hideItem() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_doctor).setVisible(false);
        nav_Menu.findItem(R.id.nav_home).setVisible(false);
        nav_Menu.findItem(R.id.nav_notes).setVisible(false);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
        //Navigation.findNavController(getView()).navigate(R.id.action_nav_logout_to_nav_login);
        finish();
        //Navigation.findNavController(getView()).navigate(R.id.action_nav_logout_to_nav_login);
    }
}
