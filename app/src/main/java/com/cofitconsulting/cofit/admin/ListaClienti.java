package com.cofitconsulting.cofit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.CustomAdapterClienti;
import com.cofitconsulting.cofit.utility.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ListaClienti extends AppCompatActivity {

    EditText etCerca;
    ImageButton btnCerca;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<User> userList = new ArrayList<>();
    FirebaseFirestore db;
    CustomAdapterClienti adapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clienti);

        db = FirebaseFirestore.getInstance();
        mRecyclerView = findViewById(R.id.recyclerview_users);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        etCerca = findViewById(R.id.cercaCliente);
        btnCerca = findViewById(R.id.cerca);

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

        btnCerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cerca = etCerca.getText().toString().toLowerCase().trim();
                searchData(cerca);
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

                        adapter = new CustomAdapterClienti(ListaClienti.this, userList);
                        mRecyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(ListaClienti.this, "Errore", Toast.LENGTH_SHORT).show();
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
                            adapter = new CustomAdapterClienti(ListaClienti.this, userList);
                            mRecyclerView.setAdapter(adapter);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListaClienti.this, "Errore", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Toast.makeText(ListaClienti.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}

