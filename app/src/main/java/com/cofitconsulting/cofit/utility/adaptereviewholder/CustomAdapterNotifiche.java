package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.admin.VisualizzaFileAdminActivity;
import com.cofitconsulting.cofit.admin.VisualizzaNotificheActivity;
import com.cofitconsulting.cofit.utility.model.ModelNotifica;

import java.util.List;

public class CustomAdapterNotifiche extends RecyclerView.Adapter<ViewHolderNotifiche> {

    VisualizzaNotificheActivity visualizzaNotificheActivity;
    List<ModelNotifica> modelList;

    public CustomAdapterNotifiche(VisualizzaNotificheActivity visualizzaNoticheActivity, List<ModelNotifica> modelList) {
        this.visualizzaNotificheActivity = visualizzaNoticheActivity;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolderNotifiche onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lv_item_notifiche, viewGroup, false);
        ViewHolderNotifiche viewHolderNotifiche = new ViewHolderNotifiche(itemView);

        viewHolderNotifiche.setOnClickListener(new ViewHolderNotifiche.ClickListener() {
            @Override
            public void onItemCLick(View view, int position) {

                String id = modelList.get(position).getId();

                Intent intent = new Intent(visualizzaNotificheActivity, VisualizzaFileAdminActivity.class);
                intent.putExtra("User_ID", id);
                visualizzaNotificheActivity.startActivity(intent);
                String vista = "Sì";
                visualizzaNotificheActivity.updateData(position, vista);

            }

            @Override
            public void onItemLongClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(visualizzaNotificheActivity);
                String[] option = {"Contrassegna come letto", "Elimina"};
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(visualizzaNotificheActivity);
                            builder.setMessage("E' stato letto?")
                                    .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String vista = "Sì";
                                            visualizzaNotificheActivity.updateData(position, vista);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String vista = "No";
                                            visualizzaNotificheActivity.updateData(position, vista);
                                        }
                                    });
                            builder.show();
                        }
                        if (which == 1) {
                            visualizzaNotificheActivity.deleteData(position);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolderNotifiche;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotifiche viewHolderNotifiche, int i) {

        viewHolderNotifiche.mEmail.setText(modelList.get(i).getEmail());
        viewHolderNotifiche.mData.setText(modelList.get(i).getData());
        String visto = modelList.get(i).getVisto();
        if (visto.equals("No"))
        {
            viewHolderNotifiche.mEmail.setTextColor(visualizzaNotificheActivity.getResources().getColor(R.color.rosso2));
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}