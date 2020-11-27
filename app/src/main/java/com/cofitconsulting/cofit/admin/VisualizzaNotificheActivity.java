package com.cofitconsulting.cofit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterNotifiche;
import com.cofitconsulting.cofit.utility.model.ModelNotifica;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class VisualizzaNotificheActivity extends AppCompatActivity {

    private List<ModelNotifica> modelList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore fStore;
    private CustomAdapterNotifiche adapter;
    private ProgressDialog pd;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_notifiche);

        btnBack = findViewById(R.id.btnBack);
        fStore = FirebaseFirestore.getInstance();

        mRecyclerView = findViewById(R.id.recyclerview_notifiche);
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

    //visualizzo le informazioni dalla collezione notifiche
    private void showData() {
        pd.setTitle("Loading Data...");
        pd.show();

        fStore.collection("Notifiche").orderBy("Visto")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult())
                        {
                            ModelNotifica modelNotifica = new ModelNotifica(doc.getString("Id"),
                                    doc.getString("Email"),
                                    doc.getString("Data"),
                                    doc.getString("Visto"));
                            modelList.add(modelNotifica);
                        }

                        adapter = new CustomAdapterNotifiche(VisualizzaNotificheActivity.this, modelList);
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

    //metodo per cancellare la notifica
    public void deleteData(int index){

        pd.setTitle("Delete Data...");
        pd.show();

        fStore.collection("Notifiche").document(modelList.get(index).getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        modelList.clear();
                        pd.dismiss();
                        Toast.makeText(VisualizzaNotificheActivity.this, "Cancellazione effettuata", Toast.LENGTH_SHORT).show();
                        showData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(VisualizzaNotificheActivity.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateData(int index, String visto){
        fStore.collection("Notifiche").document(modelList.get(index).getId()).update("Visto", visto)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        modelList.clear();
                        showData();
                    }
                });
    }
}
