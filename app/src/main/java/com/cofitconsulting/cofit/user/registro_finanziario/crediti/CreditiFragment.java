package com.cofitconsulting.cofit.user.registro_finanziario.crediti;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cofitconsulting.cofit.R;

import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterCrediti;
import com.cofitconsulting.cofit.utility.model.ModelRegistro;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class CreditiFragment extends Fragment {

    private ListView listView;
    private TextView tvEmpty;
    private FloatingActionButton btnAggCred;
    private ArrayList<ModelRegistro> modelRegistroArrayList;
    private CustomAdapterCrediti customAdapterCrediti;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lista_conti, container, false);

        btnAggCred = v.findViewById(R.id.btnAggCred);
        listView = v.findViewById(R.id.lv);
        tvEmpty = v.findViewById(R.id.emptyElement);

        btnAggCred.setVisibility(View.VISIBLE);
        btnAggCred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InserimentoCreditiActivity.class);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getContext());

        modelRegistroArrayList = databaseHelper.getAllCrediti();

        customAdapterCrediti = new CustomAdapterCrediti(getContext(), modelRegistroArrayList);
        listView.setAdapter(customAdapterCrediti);

        //se la lista è vuota compare la scritta contenuta in tvEmpty
        listView.setEmptyView(tvEmpty);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), UpdateDeleteCreditiActivity.class);
                intent.putExtra("user", modelRegistroArrayList.get(position));
                startActivity(intent);
            }
        });

        return v;
    }

}

