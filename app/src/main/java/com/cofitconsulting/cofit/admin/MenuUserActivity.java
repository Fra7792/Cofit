package com.cofitconsulting.cofit.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cofitconsulting.cofit.R;


public class MenuUserActivity extends AppCompatActivity {

    private TextView tvAnagrafica, tvVisualizzaTasse, tvTasse, tvVisualizzaDoc, tvInserisciDoc;
    private ImageButton btnBack;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);

        btnBack = findViewById(R.id.btnBack);
        tvAnagrafica = findViewById(R.id.tvAnagrafica);
        tvVisualizzaTasse = findViewById(R.id.tvVisualizzaTasse);
        tvTasse = findViewById(R.id.tvTasse);
        tvVisualizzaDoc = findViewById(R.id.tvVisualizzaDoc);
        tvInserisciDoc = findViewById(R.id.tvInserisciDoc);

        Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();

        tvVisualizzaTasse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUserActivity.this, VisualizzaTasseCliente.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        tvAnagrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUserActivity.this, AnagraficaCliente.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        tvTasse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUserActivity.this, InserimentoTasseCliente.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUserActivity.this, ListaClienti.class);
                startActivity(intent);
                finish();;
            }
        });

    }



}