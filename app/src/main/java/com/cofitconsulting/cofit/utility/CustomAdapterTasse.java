package com.cofitconsulting.cofit.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.admin.VisualizzaTasseCliente;

import java.util.List;

public class CustomAdapterTasse extends RecyclerView.Adapter<ViewHolderTasse> {

    VisualizzaTasseCliente visualizzaTasseCliente;
    List<StrutturaTassa> modelList;

    public CustomAdapterTasse(VisualizzaTasseCliente visualizzaTasseCliente, List<StrutturaTassa> modelList) {
        this.visualizzaTasseCliente = visualizzaTasseCliente;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolderTasse onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lv_item_tasse_cliente, viewGroup, false);
        ViewHolderTasse viewHolderTasse = new ViewHolderTasse(itemView);
        viewHolderTasse.setOnClickListener(new ViewHolderTasse.ClickListener() {
            @Override
            public void onItemCLick(View view, int position) {

                String descrizione = modelList.get(position).getTassa();
                String importo = modelList.get(position).getImporto();
                String scadenza = modelList.get(position).getScadenza();

            }

            @Override
            public void onItemLongClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(visualizzaTasseCliente);
                String[] option = {"Elimina"};
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0)
                        {
                            visualizzaTasseCliente.deleteData(position);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolderTasse;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTasse viewHolderTasse, int i) {

        viewHolderTasse.mDescrizione.setText(modelList.get(i).getTassa());
        viewHolderTasse.mImporto.setText(modelList.get(i).getImporto());
        viewHolderTasse.mScadenza.setText(modelList.get(i).getScadenza());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
