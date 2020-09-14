package com.cofitconsulting.cofit.utility.adaptereviewholder;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.strutture.StrutturaConto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CustomAdapterDebiti extends BaseAdapter {

    private Context context;
    private ArrayList<StrutturaConto> strutturaContoArrayList;

    public CustomAdapterDebiti(Context context, ArrayList<StrutturaConto> strutturaContoArrayList) {

        this.context = context;
        this.strutturaContoArrayList = strutturaContoArrayList;
    }


    @Override
    public int getCount() {
        return strutturaContoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return strutturaContoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item_conti, null, true);

            holder.tvtipo = convertView.findViewById(R.id.txtTipo);
            holder.tvdescrizione = convertView.findViewById(R.id.txtDescrizione);
            holder.tvimporto = convertView.findViewById(R.id.txtImporto);
            holder.tvdata = convertView.findViewById(R.id.txtData);
            holder.tvScaduto = convertView.findViewById(R.id.tvScaduto);


            convertView.setTag(holder);
        }else {

            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvtipo.setText(strutturaContoArrayList.get(position).getTipo());
        holder.tvdescrizione.setText("Descrizione: "+ strutturaContoArrayList.get(position).getDescrizione());
        holder.tvimporto.setText(strutturaContoArrayList.get(position).getImporto() + "€");
        holder.tvdata.setText("Data Scadenza: "+ strutturaContoArrayList.get(position).getData());

        String pagato = strutturaContoArrayList.get(position).getPagato();
        String data = strutturaContoArrayList.get(position).getData();

        //se la tassa non è stata pagato ed è scaduta allora rende visibile la textView "SCADUTO"
        if(scaduto(data) && pagato.equals("No"))
        {
            holder.tvScaduto.setVisibility(View.VISIBLE);
        }
        //se è stato pagato rende visibile la textView e gli scrive pagato
        else if(pagato.equals("Sì"))
        {
            holder.tvScaduto.setVisibility(View.VISIBLE);
            holder.tvScaduto.setText("PAGATO");
            holder.tvScaduto.setTextColor(0xFF41A317);
        }
        
        return convertView;
    }

    private class ViewHolder {

        protected TextView tvtipo, tvdescrizione, tvimporto, tvdata, tvScaduto;
    }

    //metodo per sapere se una data è antecedente a quella di oggi
    public boolean scaduto(String data_scadenza){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        try {
            Date date = dateFormat.parse(data_scadenza);
            if(currentTime.after(date))
            {
                return true;
            }
            else return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}