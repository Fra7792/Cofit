package com.cofitconsulting.cofit.user.documenti;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterNovita;
import com.cofitconsulting.cofit.utility.model.ModelFile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class VisNovitaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CustomAdapterNovita adapter;

    private DatabaseReference databaseReference;
    private List<ModelFile> modelFileList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_vis_novita, container, false);

        progressBar = v.findViewById(R.id.progress_circle);

        recyclerView = v.findViewById(R.id.recyclerview_novita);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        modelFileList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Novità");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    ModelFile modelFile = postSnapshot.getValue(ModelFile.class);
                    modelFileList.add(modelFile);
                }

                adapter = new CustomAdapterNovita(getContext(), modelFileList);

                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
