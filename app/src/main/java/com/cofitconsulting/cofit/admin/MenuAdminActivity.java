package com.cofitconsulting.cofit.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cofitconsulting.cofit.R;


public class MenuAdminActivity extends AppCompatActivity {

    private TextView titolo;
    private ImageView tvAnagrafica, tvVisualizzaTasse, tvTasse, tvVisualizzaDoc, tvInserisciDoc;
    private ImageButton btnBack;
    private String userID, denominazione, cognome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);

        btnBack = findViewById(R.id.btnBack);
        titolo = findViewById(R.id.titolo);
        tvAnagrafica = findViewById(R.id.btnAnagrafica);
        tvVisualizzaTasse = findViewById(R.id.btnVisf24);
        tvTasse = findViewById(R.id.btnAddf24);
        tvVisualizzaDoc = findViewById(R.id.btnVisDoc);
        tvInserisciDoc = findViewById(R.id.btnAddDoc);

        Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();
        denominazione = intent.getStringExtra("Nome").trim();
        cognome = intent.getStringExtra("Cognome").trim();


        titolo.setText(denominazione + " " + cognome);

        tvVisualizzaTasse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, VisualizzaTasseAdminActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        tvAnagrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, AnagraficaClienteActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        tvTasse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, InserimentoTasseActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        tvVisualizzaDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, VisualizzaFileAdminActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        tvInserisciDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, CaricaFileAdminActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, ListaClientiActivity.class);
                startActivity(intent);
                finish();;
            }
        });


    }

}