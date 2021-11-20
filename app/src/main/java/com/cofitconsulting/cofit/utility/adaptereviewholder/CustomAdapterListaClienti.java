package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.admin.ListaClientiActivity;
import com.cofitconsulting.cofit.admin.MenuAdminActivity;
import com.cofitconsulting.cofit.utility.model.ModelUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapterListaClienti extends RecyclerView.Adapter<ViewHolderClienti> {

    ListaClientiActivity listaClientiActivity;
    List<ModelUser> modelList;
    private StorageReference storageReference;

    public CustomAdapterListaClienti(ListaClientiActivity listaClientiActivity, List<ModelUser> modelList) {
        this.listaClientiActivity = listaClientiActivity;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolderClienti onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lv_item_lista_clienti, viewGroup, false);
        ViewHolderClienti viewHolderClienti = new ViewHolderClienti(itemView);
        viewHolderClienti.setOnClickListener(new ViewHolderClienti.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String denominazione = modelList.get(position).getDenominazione();
                String id = modelList.get(position).getId();
                String cognome = modelList.get(position).getCognome();
                Intent intent = new Intent(listaClientiActivity, MenuAdminActivity.class);
                intent.putExtra("User_ID", id);
                intent.putExtra("Nome", denominazione);
                intent.putExtra("Cognome", cognome);
                listaClientiActivity.startActivity(intent);
            }
        });
        return viewHolderClienti;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderClienti viewHolderClienti, int i) {

        String nome = modelList.get(i).getDenominazione();
        String cognome = modelList.get(i).getCognome();
        viewHolderClienti.mDenominazione.setText(cognome + " " + nome);
        viewHolderClienti.mEmail.setText(modelList.get(i).getEmail());

        storageReference = FirebaseStorage.getInstance().getReference();
       final StorageReference profileRef = storageReference.child("users/" + modelList.get(i).getId() + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(viewHolderClienti.profileImage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}

