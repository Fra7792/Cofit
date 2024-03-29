package com.cofitconsulting.cofit.user.registro_finanziario.debiti;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterDebiti;
import com.cofitconsulting.cofit.utility.model.ModelRegistro;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;



public class DebitiFragment extends Fragment {
    private ListView listView;
    private TextView tvEmpty;
    private FloatingActionButton btnAggDeb;
    private ArrayList<ModelRegistro> modelRegistroArrayList;
    private CustomAdapterDebiti customAdapter;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lista_conti, container, false);

        listView =  v.findViewById(R.id.lv);
        tvEmpty = v.findViewById(R.id.emptyElement);
        btnAggDeb = v.findViewById(R.id.btnAggDeb);


        databaseHelper = new DatabaseHelper(getContext());

        modelRegistroArrayList = databaseHelper.getAllDebiti();

        customAdapter = new CustomAdapterDebiti(getContext(), modelRegistroArrayList);
        listView.setAdapter(customAdapter);

        tvEmpty.setText("Non ci sono debiti");
        listView.setEmptyView(tvEmpty);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), UpdateDeleteDebitiActivity.class);
                intent.putExtra("user", modelRegistroArrayList.get(position));
                startActivity(intent);
            }
        });

        btnAggDeb.setVisibility(View.VISIBLE);
        btnAggDeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InserimentoDebitiActivity.class);
                startActivity(intent);
            }
        });


        return v;
    }

}

