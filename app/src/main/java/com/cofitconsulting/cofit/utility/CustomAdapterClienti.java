package com.cofitconsulting.cofit.utility;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.admin.ListaClienti;
import com.cofitconsulting.cofit.admin.MenuUserActivity;

import java.util.List;

public class CustomAdapterClienti extends RecyclerView.Adapter<ViewHolderClienti> {

    ListaClienti listaClienti;
    List<User> modelList;

    public CustomAdapterClienti(ListaClienti listaClienti, List<User> modelList) {
        this.listaClienti = listaClienti;
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
                String email = modelList.get(position).getEmail();
                String id = modelList.get(position).getId();
                Intent intent = new Intent(listaClienti, MenuUserActivity.class);
                intent.putExtra("User_ID", id);
                intent.putExtra("Nome", denominazione);
                listaClienti.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(listaClienti, "Hai tenuto schiacciato", Toast.LENGTH_SHORT).show();
            }
        });

        return viewHolderClienti;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderClienti viewHolderClienti, int i) {

        viewHolderClienti.mDenominazione.setText(modelList.get(i).getDenominazione());
        viewHolderClienti.mEmail.setText(modelList.get(i).getEmail());
        viewHolderClienti.mId.setText(modelList.get(i).getId());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}

