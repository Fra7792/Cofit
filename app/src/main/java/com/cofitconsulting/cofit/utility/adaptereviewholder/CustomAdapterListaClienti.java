package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.admin.ListaClientiActivity;
import com.cofitconsulting.cofit.admin.MenuAdminActivity;
import com.cofitconsulting.cofit.utility.strutture.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapterListaClienti extends RecyclerView.Adapter<ViewHolderClienti> {

    ListaClientiActivity listaClientiActivity;
    List<User> modelList;
    private StorageReference storageReference;

    public CustomAdapterListaClienti(ListaClientiActivity listaClientiActivity, List<User> modelList) {
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
                Intent intent = new Intent(listaClientiActivity, MenuAdminActivity.class);
                intent.putExtra("User_ID", id);
                intent.putExtra("Nome", denominazione);
                listaClientiActivity.startActivity(intent);
            }
        });
        return viewHolderClienti;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderClienti viewHolderClienti, int i) {

        viewHolderClienti.mDenominazione.setText(modelList.get(i).getDenominazione());
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

