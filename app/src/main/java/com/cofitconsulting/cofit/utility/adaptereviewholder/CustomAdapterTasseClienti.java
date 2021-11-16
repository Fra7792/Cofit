package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.user.registro_finanziario.TasseFragment;
import com.cofitconsulting.cofit.utility.model.ModelTassa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

    public class CustomAdapterTasseClienti extends RecyclerView.Adapter<ViewHolderTasse> {


        TasseFragment context;
        List<ModelTassa> modelList;

        public CustomAdapterTasseClienti(TasseFragment context, List<ModelTassa> modelList) {
            this.context = context;
            this.modelList = modelList;
        }

        @NonNull
        @Override
        public ViewHolderTasse onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lv_item_tasse, viewGroup, false);
            ViewHolderTasse viewHolderTasse = new ViewHolderTasse(itemView);
            viewHolderTasse.setOnClickListener(new ViewHolderTasse.ClickListener() {
                @Override
                public void onItemCLick(View view, int position) {

                    String descrizione = modelList.get(position).getTassa();
                    Double importo = modelList.get(position).getImporto();
                    String scadenza = modelList.get(position).getScadenza();
                    Boolean pagato = modelList.get(position).getPagato();
                    Boolean permesso = modelList.get(position).getPermesso();

                }

                @Override
                public void onItemLongClick(View view, final int position) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
                    String[] option = {"Richiesta di pagamento della tassa"};
                    builder.setItems(option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
                                builder.setMessage("Vuoi richiedere il pagamento della tassa?")
                                        .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                context.updatePermesso(position, true);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                context.updatePermesso(position, false);
                                            }
                                        });
                                builder.show();
                            }
                        }
                    }).create().show();
                }
            });

            return viewHolderTasse;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolderTasse viewHolderTasse, int i) {

            viewHolderTasse.mDescrizione.setText(modelList.get(i).getTassa());
            viewHolderTasse.mImporto.setText(modelList.get(i).getImporto().toString() + "€");
            viewHolderTasse.mScadenza.setText("Data scadenza: " + modelList.get(i).getScadenza());
            String dataScadenza = modelList.get(i).getScadenza();
            Boolean pagato = modelList.get(i).getPagato();
            Boolean permesso = modelList.get(i).getPermesso();

            //se la tassa non è stata pagato ed è scaduta allora rende visibile la textView "SCADUTO"
            if (scaduto(dataScadenza) && !pagato) {
                viewHolderTasse.tvScaduto.setVisibility(View.VISIBLE);
            }

            //se è stato pagato rende visibile la textView e gli scrive pagato
            if (pagato) {
                viewHolderTasse.tvScaduto.setVisibility(View.VISIBLE);
                viewHolderTasse.tvScaduto.setText("PAGATO");
                viewHolderTasse.tvScaduto.setTextColor(context.getResources().getColor(R.color.verde1));
            }
            if(!permesso) {
                viewHolderTasse.permesso.setColorFilter(context.getResources().getColor(R.color.rosso2));
            } else if(permesso && !pagato){
                viewHolderTasse.permesso.setColorFilter(context.getResources().getColor(R.color.giallo));
            } else if(pagato)
            {
                viewHolderTasse.permesso.setColorFilter(context.getResources().getColor(R.color.verde1));
            }

        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        //metodo per sapere se una data è antecedente a quella di oggi
        public boolean scaduto(String data_scadenza) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
            Date currentTime = Calendar.getInstance().getTime();
            try {
                Date date = dateFormat.parse(data_scadenza);
                if (currentTime.after(date)) {
                    return true;
                } else return false;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

