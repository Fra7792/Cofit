package com.cofitconsulting.cofit.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cofitconsulting.cofit.R;

import com.cofitconsulting.cofit.user.bilancio.crediti.DatabaseHelper;
import com.cofitconsulting.cofit.user.bilancio.crediti.InserimentoCreditiActivity;
import com.cofitconsulting.cofit.user.bilancio.crediti.UpdateDeleteCreditiActivity;
import com.cofitconsulting.cofit.user.bilancio.debiti.InserimentoDebitiActivity;
import com.cofitconsulting.cofit.utility.CustomAdapterCrediti;
import com.cofitconsulting.cofit.utility.StrutturaConto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class CreditiFragment extends Fragment {

    private TextView titolo;
    private ListView listView;
    private FloatingActionButton btnMenu, btnCrediti, btnDebiti;
    private ArrayList<StrutturaConto> strutturaContoArrayList;
    private CustomAdapterCrediti customAdapterCrediti;
    private Float translationY = 100f;
    private Float translationX = 100f;
    private OvershootInterpolator interpolator = new OvershootInterpolator();
    private DatabaseHelper databaseHelper;
    private Boolean isMenuOpen = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lista_conti, container, false);

        btnMenu = v.findViewById(R.id.btnFloatingMenu);
        btnCrediti = v.findViewById(R.id.btnAggCred);
        btnDebiti = v.findViewById(R.id.btnAggDeb);
        listView = v.findViewById(R.id.lv);

        btnCrediti.setAlpha(0f);
        btnDebiti.setAlpha(0f);

        btnCrediti.setTranslationX(translationY);
        btnDebiti.setTranslationY(translationX);


        databaseHelper = new DatabaseHelper(getContext());

        strutturaContoArrayList = databaseHelper.getAllUsers();

        customAdapterCrediti = new CustomAdapterCrediti(getContext(), strutturaContoArrayList);
        listView.setAdapter(customAdapterCrediti);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), UpdateDeleteCreditiActivity.class);
                intent.putExtra("user", strutturaContoArrayList.get(position));
                startActivity(intent);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenuOpen){
                    btnCrediti.setVisibility(View.INVISIBLE);
                    btnDebiti.setVisibility(View.INVISIBLE);
                    closeMenu();
                } else {
                    btnCrediti.setVisibility(View.VISIBLE);
                    btnDebiti.setVisibility(View.VISIBLE);
                    openMenu();
                }
            }
        });

        btnCrediti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InserimentoCreditiActivity.class);
                startActivity(intent);
            }
        });

        btnDebiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InserimentoDebitiActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }


    private void openMenu(){
        isMenuOpen = !isMenuOpen;

        btnMenu.animate().setInterpolator(interpolator).rotationBy(45f).setDuration(300).start();
        btnCrediti.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        btnDebiti.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu(){
        isMenuOpen = !isMenuOpen;

        btnMenu.animate().setInterpolator(interpolator).rotationBy(45f).setDuration(300).start();
        btnCrediti.animate().translationX(translationX).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        btnDebiti.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

}

