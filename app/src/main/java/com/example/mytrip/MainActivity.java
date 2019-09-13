package com.example.mytrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mytrip.constant.NavMenuItem;
import com.example.mytrip.fragment.HomeFragment;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private NavigationView navigationView; // for navigation menu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setHomePage();
        init();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedMenuPage(id);

        return true;
    }

    // function to open fragment or Activity according to menu selected
    public void displaySelectedMenuPage(int menuId) {
        Fragment fragment = null;

        switch (menuId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;

            case R.id.nav_my_trip:
                goToDefaultSearch();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        closeDrawer();
    }

    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setHomePage() {

        displaySelectedMenuPage(NavMenuItem.HOME);
        setMenuSelected(NavMenuItem.HOME);
    }

    public void setMenuSelected(int id) {
        navigationView.getMenu().findItem(id).setChecked(true);
    }

    // function accessible from fragments to set the title bar
    public void setActionBarTitle(String title) {

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    private void init() {

        String apiKey = getString(R.string.api_key);

        if(!Places.isInitialized()){

            // Initialize the SDK
            Places.initialize(getApplicationContext(), apiKey);
        }
    }

    private void goToDefaultSearch() {

        Intent intent = new Intent(this, DefaultSearchActivity.class);
        startActivity(intent);
    }
}
