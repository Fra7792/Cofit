package com.cofitconsulting.cofit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cofitconsulting.cofit.LoginActivity;
import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterListaClienti;
import com.cofitconsulting.cofit.utility.strutture.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ListaClientiActivity extends AppCompatActivity {

    private EditText etCerca;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;
    private CustomAdapterListaClienti adapter;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clienti);

        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.recyclerview_users);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        etCerca = findViewById(R.id.cercaCliente);

        pd = new ProgressDialog(this);

        showData();

       etCerca.setOnKeyListener(new View.OnKeyListener() {
           @Override
           public boolean onKey(View v, int keyCode, KeyEvent event) {
               if (event.getAction() == KeyEvent.ACTION_DOWN)
               {
                   switch (keyCode)
                   {
                       case KeyEvent.KEYCODE_DPAD_CENTER:
                       case KeyEvent.KEYCODE_ENTER:
                           String cerca = etCerca.getText().toString().toLowerCase().trim();
                           searchData(cerca);
                           return true;
                       default:
                           break;
                   }
               }
               return false;
           }
       });

    }


    private void showData() {
        pd.setTitle("Loading...");
        pd.show();

        db.collection("Users").orderBy("Denominazione")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        pd.dismiss();
                        for(DocumentSnapshot doc : task.getResult())
                        {
                            User user = new User(doc.getString("Id"),
                                    doc.getString("Denominazione"),
                                    doc.getString("Email"));
                            userList.add(user);
                        }

                        adapter = new CustomAdapterListaClienti(ListaClientiActivity.this, userList);
                        mRecyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(ListaClientiActivity.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchData(final String s) {
        pd.setTitle("Loading...");
        pd.show();

        db.collection("Users").orderBy("Denominazione")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        pd.dismiss();
                        userList.clear();
                        for (DocumentSnapshot doc : task.getResult())
                        {
                            User user = new User(doc.getString("Id"),
                                    doc.getString("Denominazione"),
                                    doc.getString("Email"));
                            if(doc.getString("search").contains(s))
                            {
                                userList.add(user);
                            }
                            adapter = new CustomAdapterListaClienti(ListaClientiActivity.this, userList);
                            mRecyclerView.setAdapter(adapter);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListaClientiActivity.this, "Errore", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Toast.makeText(ListaClientiActivity.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuNovita: {
                Intent intent = new Intent(ListaClientiActivity.this, Novitactivity.class);
                startActivity(intent);
                break;
            }

            case R.id.menuEsci: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}

