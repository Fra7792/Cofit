package com.cofitconsulting.cofit.user.documenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterNovita;
import com.cofitconsulting.cofit.utility.strutture.StrutturaUpload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VisualizzaNovitaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private CustomAdapterNovita adapter;

    private DatabaseReference databaseReference;
    private List<StrutturaUpload> strutturaUploadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_novita);

        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progress_circle);

        recyclerView = findViewById(R.id.recyclerview_novita);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        strutturaUploadList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Novità");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    StrutturaUpload strutturaUpload = postSnapshot.getValue(StrutturaUpload.class);
                    strutturaUploadList.add(strutturaUpload);
                }

                adapter = new CustomAdapterNovita(VisualizzaNovitaActivity.this, strutturaUploadList);

                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VisualizzaNovitaActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
