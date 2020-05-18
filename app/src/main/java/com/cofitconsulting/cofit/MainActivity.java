package com.cofitconsulting.cofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cofitconsulting.cofit.admin.ListaClienti;
import com.cofitconsulting.cofit.user.anagrafica.InserimentoAnagrafica;
import com.cofitconsulting.cofit.user.anagrafica.ModificaAnagrafica;
import com.cofitconsulting.cofit.user.documenti.CaricaDocumentiActivity;
import com.cofitconsulting.cofit.user.documenti.VisualizzaDocumentiActivity;
import com.cofitconsulting.cofit.utility.PageAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabItem crediti, debiti, tasse;
    PagerAdapter adapter;
    private String email;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = fAuth.getInstance().getCurrentUser().getEmail();

        if (email.equals("francesco0792@gmail.com") || email.equals("admin@prova.com")) {
            Intent intent = new Intent(MainActivity.this, ListaClienti.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences preferences = getSharedPreferences("anagrafica", MODE_PRIVATE);  //se il boolean firstrun all'interno delle Preferences è true lanciamo la LoginActivity con l'intent.
            if (preferences.getBoolean("firstrun", true)) {
                Toast.makeText(MainActivity.this, "INSERISCI SUBITO LA TUA ANAGRAFICA", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("firstrun", false);
                editor.apply();
                Intent intent = new Intent(this, InserimentoAnagrafica.class);
                startActivity(intent);
            }

        }

        toolbar.setTitle("Benvenuto in Cofit");
        tabLayout = findViewById(R.id.tableLayout);
        viewPager = findViewById(R.id.view_pager);
        crediti = findViewById(R.id.crediti);
        debiti = findViewById(R.id.debiti);
        tasse = findViewById(R.id.tasse);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView text_email = headerView.findViewById(R.id.email);
        text_email.setText(email);

        adapter = new PageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.getTabAt(0).setText("Crediti");
        tabLayout.getTabAt(1).setText("Debiti");
        tabLayout.getTabAt(2).setText("Tasse");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);


        if(item.getItemId() == R.id.menuProfile)
        {
            Intent intent = new Intent(MainActivity.this, ModificaAnagrafica.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.menuInserisciDoc)
        {
            Intent intent = new Intent(MainActivity.this, CaricaDocumentiActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.menuVisualizzaDoc)
        {
            Intent intent = new Intent(MainActivity.this, VisualizzaDocumentiActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.menuWhatsapp)
        {
            String mobileNumber = "3401861219";

            boolean installed = appInstalledOnNot("com.whatsapp");

            if (installed) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "39" + mobileNumber));
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "WhatsApp non è installato sul tuo dispositivo", Toast.LENGTH_SHORT).show();
            }
        }
        if(item.getItemId() == R.id.menuEsci)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        return false;
    }

    private boolean appInstalledOnNot(String url) {
        PackageManager packageManager = getPackageManager();
        boolean app_installed;
        try{
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed;
    }
}
