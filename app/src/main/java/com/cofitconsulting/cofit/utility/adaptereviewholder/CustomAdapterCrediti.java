package com.cofitconsulting.cofit.utility.adaptereviewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.strutture.StrutturaConto;

import java.util.ArrayList;



public class CustomAdapterCrediti extends BaseAdapter {

    private Context context;
    private ArrayList<StrutturaConto> strutturaContoArrayList;

    public CustomAdapterCrediti(Context context, ArrayList<StrutturaConto> strutturaContoArrayList) {

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

            holder.tvtipo = (TextView) convertView.findViewById(R.id.txtTipo);
            holder.tvdescrizione = (TextView) convertView.findViewById(R.id.txtDescrizione);
            holder.tvimporto = (TextView) convertView.findViewById(R.id.txtImporto);
            holder.tvimporto.setTextColor(0xFF41A317);
            holder.tvdata = (TextView) convertView.findViewById(R.id.txtData);


            convertView.setTag(holder);
        }else {

            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvtipo.setText(strutturaContoArrayList.get(position).getTipo());
        holder.tvdescrizione.setText("Descrizione: "+ strutturaContoArrayList.get(position).getDescrizione());
        holder.tvimporto.setText(strutturaContoArrayList.get(position).getImporto() + "€");
        holder.tvdata.setText("Data Scadenza: "+ strutturaContoArrayList.get(position).getData());


        return convertView;
    }

    private class ViewHolder {

        protected TextView tvtipo, tvdescrizione, tvimporto, tvdata;
    }

}