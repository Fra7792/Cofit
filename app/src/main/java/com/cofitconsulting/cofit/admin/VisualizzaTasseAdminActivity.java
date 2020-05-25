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
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterTasse;
import com.cofitconsulting.cofit.utility.strutture.StrutturaTassa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class VisualizzaTasseAdminActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_visualizza_tasse_admin);

        Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();
        btnBack = findViewById(R.id.btnBack);
        fStore = FirebaseFirestore.getInstance();

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

        fStore.collection(userID).orderBy("Pagato")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult())
                        {
                            StrutturaTassa strutturaTassa = new StrutturaTassa(doc.getString("Tassa"),
                                    doc.getString("Importo"),
                                    doc.getString("Scadenza"),
                                    doc.getString("Pagato"));
                            modelList.add(strutturaTassa);
                        }

                        adapter = new CustomAdapterTasse(VisualizzaTasseAdminActivity.this, modelList);
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
                        Toast.makeText(VisualizzaTasseAdminActivity.this, "Cancellazione effettuata", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(VisualizzaTasseAdminActivity.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateData(int index, String pagato){
        fStore.collection(userID).document(modelList.get(index).getTassa()).update("Pagato", pagato)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                modelList.clear();
                showData();
            }
        });
    }

}



