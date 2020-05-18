package com.cofitconsulting.cofit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.CustomAdapterTasse;
import com.cofitconsulting.cofit.utility.StrutturaTassa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VisualizzaTasseCliente extends AppCompatActivity {

    private List<StrutturaTassa> modelList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore fStore;
    private CustomAdapterTasse adapter;
    private ProgressDialog pd;
    private ImageButton btnBack;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizza_tasse_cliente);

        Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();
        btnBack = findViewById(R.id.btnBack);
        fStore = FirebaseFirestore.getInstance();

        //btnAggiungi = findViewById(R.id.btnFloatingAggiungi);
        mRecyclerView = findViewById(R.id.recyclerview_tasse);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        pd = new ProgressDialog(this);

        showData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showData() {
        pd.setTitle("Loading Data...");
        pd.show();

        fStore.collection(userID).orderBy("Tassa")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult())
                        {
                            StrutturaTassa strutturaTassa = new StrutturaTassa(doc.getString("Tassa"),
                                    doc.getString("Importo"),
                                    doc.getString("Scadenza"));
                            modelList.add(strutturaTassa);
                        }

                        adapter = new CustomAdapterTasse(VisualizzaTasseCliente.this, modelList);
                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();

                    }
                });
    }

    public void deleteData(int index){

        pd.setTitle("Delete Data...");
        pd.show();

        fStore.collection(userID).document(modelList.get(index).getTassa()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        modelList.clear();
                        pd.dismiss();
                        Toast.makeText(VisualizzaTasseCliente.this, "Cancellazione effettuata", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(VisualizzaTasseCliente.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}



