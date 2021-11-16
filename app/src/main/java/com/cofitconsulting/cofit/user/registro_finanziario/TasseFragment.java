package com.cofitconsulting.cofit.user.registro_finanziario;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterTasseClienti;
import com.cofitconsulting.cofit.utility.model.ModelTassa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TasseFragment extends Fragment {

    private List<ModelTassa> modelList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore fStore;
    private CustomAdapterTasseClienti adapter;
    private ProgressDialog pd;
    private FirebaseAuth fAuth;
    private String userID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tasse, container, false);

        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = v.findViewById(R.id.recyclerview_tasse);
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        pd = new ProgressDialog(getContext());

        showData();

        return v;
    }

    //recupero tutte le tasse presenti nella directory associata all'id del cliente
    private void showData() {
        pd.setTitle("Caricamento in corso...");
        pd.show();

        fStore.collection(userID).orderBy("Pagato")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        pd.dismiss();
                        for(DocumentSnapshot doc: task.getResult())
                        {
                            ModelTassa modelTassa = new ModelTassa(doc.getString("Tassa"),
                                    doc.getDouble("Importo"),
                                    doc.getString("Scadenza"),
                                    doc.getBoolean("Pagato"),
                                    doc.getBoolean("Permesso pagamento"));

                            modelList.add(modelTassa);
                        }

                        adapter = new CustomAdapterTasseClienti(TasseFragment.this, modelList);
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



    //metodo per aggiornare il pagamento effettuato
    public void updatePermesso(int index, Boolean permesso){
        fStore.collection(userID).document(modelList.get(index).getTassa()).update("Permesso pagamento", permesso)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        modelList.clear();
                        showData();
                    }
                });
    }

}
