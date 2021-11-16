package com.cofitconsulting.cofit;



import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cofitconsulting.cofit.admin.ListaClientiActivity;
import com.cofitconsulting.cofit.user.anagrafica.InserimentoAnagraficaActivity;
import com.cofitconsulting.cofit.user.documenti.AddFileFragment;
import com.cofitconsulting.cofit.user.documenti.DownloadFileFragment;
import com.cofitconsulting.cofit.user.registro_finanziario.GraficoTasseFragment;
import com.cofitconsulting.cofit.user.registro_finanziario.HomeFragment;
import com.cofitconsulting.cofit.user.anagrafica.ProfiloFragment;
import com.cofitconsulting.cofit.user.documenti.VisNovitaFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FrameLayout frameLayout;
    private String email;
    private String userID;
    private FirebaseAuth fAuth;
    private StorageReference storageReference;
    private Toolbar toolbar;


    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.fragment);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userID = fAuth.getInstance().getCurrentUser().getUid();

        try {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } catch(NullPointerException e)
        {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        //se l'utente ha uno di questi indirizzi entra nella versione admin
        if (email.equals("francesco0792@gmail.com") || email.equals("cofitconsulting@outlook.it")) {
            String tokenUser = FirebaseInstanceId.getInstance().getToken();
            if(email.equals("francesco0792@gmail.com"))
            {
                writeOnDatabaseTokenProva(tokenUser);

            } else if(email.equals("cofitconsulting@outlook.it"))
                {
                    writeOnDatabaseToken(tokenUser);
                }

            Intent intent = new Intent(MainActivity.this, ListaClientiActivity.class);
            startActivity(intent);
            finish();
        } else {

            //utilizzo le sharedPreferences per far inserire l'anagrafica al primo avvio
            SharedPreferences preferences = getSharedPreferences("anagrafica", MODE_PRIVATE);  //se il boolean firstrun all'interno delle Preferences è true lanciamo la LoginActivity con l'intent.
            if (preferences.getBoolean("firstrun", true)) {
                Toast.makeText(MainActivity.this, "INSERISCI SUBITO LA TUA ANAGRAFICA", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("firstrun", false);
                editor.apply();
                Intent intent = new Intent(this, InserimentoAnagraficaActivity.class);
                startActivity(intent);
            }
            SharedPreferences preferences2 = getSharedPreferences("token", MODE_PRIVATE);
            String tokenUser = FirebaseInstanceId.getInstance().getToken();
            String token = preferences2.getString("myToken", "null");

            if(!(tokenUser.equals(token)))
            {
                SharedPreferences.Editor editor = preferences2.edit();
                editor.putString("myToken", tokenUser);
                editor.apply();
                writeOnDatabaseTokenUsers(userID, tokenUser);
            }

        }

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        View headerView = navigationView.getHeaderView(0);
        TextView text_email = headerView.findViewById(R.id.email);
        text_email.setText(email);

        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment, homeFragment);
        ft.commit();

        final CircleImageView profileImage = headerView.findViewById(R.id.profileImage);
        storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference profileRef = storageReference.child("users/" + userID + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (item.getItemId()) {
            case R.id.menuHome: {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.menuProfile: {

                ProfiloFragment fragProfilo = new ProfiloFragment();
                ft.replace(R.id.fragment, fragProfilo);
                ft.commit();
                toolbar.setTitle("Il tuo profilo");
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_profilo);
                break;
            }

            case R.id.menuGrafico: {

                GraficoTasseFragment fragGrafico = new GraficoTasseFragment();
                ft.replace(R.id.fragment, fragGrafico);
                ft.commit();
                toolbar.setTitle("La tua situazione");
                toolbar.getMenu().clear();
                break;
            }

            case R.id.menuNovita: {
                VisNovitaFragment fragnovita = new VisNovitaFragment();
                ft.replace(R.id.fragment, fragnovita);
                ft.commit();
                toolbar.setTitle("Novità COFIT");
                toolbar.getMenu().clear();
                break;
            }

            case R.id.menuInserisciDoc: {
                AddFileFragment fileAddFragment = new AddFileFragment();
                ft.replace(R.id.fragment, fileAddFragment);
                ft.commit();
                toolbar.setTitle("Inserisci documenti");
                toolbar.getMenu().clear();
                break;
            }
            case R.id.menuVisualizzaDoc: {
                DownloadFileFragment downloadFileFragment = new DownloadFileFragment();
                ft.replace(R.id.fragment, downloadFileFragment);
                ft.commit();
                toolbar.setTitle("Download documenti");
                toolbar.getMenu().clear();
                break;
            }
            case R.id.menuIndirizzo: {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=via+Gabriele+d'annunzio+24+pescara"));
                startActivity(intent);
                break;
            }
            case R.id.menuTelefono: {
                String phone = "085377395";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
                break;
            }
            case R.id.menuWhatsapp: {
                String mobileNumber = "3401861219";
                boolean installed = appInstalledOnNot("com.whatsapp");

                if (installed) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "39" + mobileNumber));
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "WhatsApp non è installato sul tuo dispositivo", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.menuFacebook: {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "219948828196163"));
                    startActivity(intent);
                } catch (Exception e) {
                    Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + "219948828196163"));
                    startActivity(intent);
                }
                break;
            }
            case R.id.menuEmail:
            {
                String ind_email = "info@cofit.consulting.com";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{ind_email});
                try {
                    startActivity(Intent.createChooser(i, "Invia email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Client di posta non disponibile", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.menuWeb: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.cofitconsulting.com")));
                break;
            }


            case R.id.menuEntrate: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.agenziaentrate.gov.it/portale/home")));
                break;
            }

            case R.id.menuRiscossione: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.agenziaentrateriscossione.gov.it/it/")));
                break;
            }
            case R.id.menuInps: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.inps.it/nuovoportaleinps/default.aspx")));
                break;
            }

            case R.id.menuInail: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.inail.it/cs/internet/home.html")));
                break;
            }

            case R.id.menuCciaa: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.pe.camcom.it/")));
                break;
            }

            case R.id.menuInfo: {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.cofitconsulting.com/termini-e-condizioni-duso/")));
                break;
            }

            case R.id.menuEsci: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            default: return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuEdit: {
                Intent intent = new Intent(MainActivity.this, InserimentoAnagraficaActivity.class);
                startActivity(intent);
                break;
            }

        }
        return super.onOptionsItemSelected(item);

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


    private void writeOnDatabaseToken(String token){
        Map<String, Object> tokenAdmin = new HashMap<>();
        tokenAdmin.put("Token", token);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Token").document("Token").set(tokenAdmin);
    }

    private void writeOnDatabaseTokenUsers(String uid, String token){
        Map<String, Object> user = new HashMap<>();
        user.put("Token", token);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).update(user);
    }


    private void writeOnDatabaseTokenProva(String token){
        Map<String, Object> tokenAdmin = new HashMap<>();
        tokenAdmin.put("Token", token);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Token").document("TokenProva").set(tokenAdmin);
    }


}
