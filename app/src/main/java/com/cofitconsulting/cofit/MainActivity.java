package com.cofitconsulting.cofit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cofitconsulting.cofit.admin.AdminActivity;
import com.cofitconsulting.cofit.user.anagrafica.InserimentoAnagrafica;
import com.cofitconsulting.cofit.user.anagrafica.ModificaAnagrafica;
import com.cofitconsulting.cofit.user.fragment.CreditiFragment;
import com.cofitconsulting.cofit.user.fragment.DebitiFragment;
import com.cofitconsulting.cofit.user.fragment.TasseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CalendarView calendarView;

    private DebitiFragment debitiFragment;
    private CreditiFragment creditiFragment;
    private TasseFragment tasseFragment;
    private Button btnLogOut;
    private ImageButton btnViewAnagrafica, whatsapp;
    private String email;


    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnViewAnagrafica = findViewById(R.id.btnViewAnagrafica);
        calendarView = findViewById(R.id.calendarView);
        btnLogOut = findViewById(R.id.logout);
        whatsapp = findViewById(R.id.whatsapp);


        email = fAuth.getInstance().getCurrentUser().getEmail();


       if(email.equals("francesco0792@gmail.com"))
        {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        }
       else {
           SharedPreferences preferences = getSharedPreferences("anagrafica", MODE_PRIVATE);  //se il boolean firstrun all'interno delle Preferences è true lanciamo la LoginActivity con l'intent.
           if (preferences.getBoolean("firstrun", true)) {
               Toast.makeText(MainActivity.this, "INSERISCI SUBITO LA TUA ANAGRAFICA", Toast.LENGTH_LONG).show();
               SharedPreferences.Editor editor = preferences.edit();
               editor.putBoolean("firstrun", false);
               editor.apply();
               Intent intent = new Intent(this, InserimentoAnagrafica.class);
               startActivity(intent);
           }

           viewPager = findViewById(R.id.view_pager);
           tabLayout = findViewById(R.id.tabLayout);


           debitiFragment = new DebitiFragment();
           creditiFragment = new CreditiFragment();
           tasseFragment = new TasseFragment();

           tabLayout.setupWithViewPager(viewPager);
           ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
           tabLayout.getTabAt(0);
           tabLayout.getTabAt(1);
           tabLayout.getTabAt(2);
           viewPagerAdapter.addFragment(creditiFragment, "Crediti");
           viewPagerAdapter.addFragment(debitiFragment, "Debiti");
           viewPagerAdapter.addFragment(tasseFragment, "Tasse");
           viewPager.setAdapter(viewPagerAdapter);


           btnLogOut.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   FirebaseAuth.getInstance().signOut();
                   Intent intent = new Intent(MainActivity.this, Login.class);
                   startActivity(intent);
               }
           });

           btnViewAnagrafica.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(MainActivity.this, ModificaAnagrafica.class);
                   startActivity(intent);
               }
           });
       }

       whatsapp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String mobileNumber = "3401861219";

               boolean installed = appInstalledOnNot("com.whatsapp");

               if(installed)
               {
                   Intent intent = new Intent(Intent.ACTION_VIEW);
                   intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"39"+mobileNumber));
                   startActivity(intent);
               } else
               {
                   Toast.makeText(MainActivity.this, "WhatsApp non è installato sul tuo dispositivo", Toast.LENGTH_SHORT).show();
               }
           }
       });

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

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position){
            return fragmentTitle.get(position);
        }
    }



}
