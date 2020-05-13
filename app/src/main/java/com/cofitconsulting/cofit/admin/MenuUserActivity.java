package com.cofitconsulting.cofit.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cofitconsulting.cofit.R;


public class MenuUserActivity extends AppCompatActivity {

    private TextView tvAnagrafica, tvTasse, tvVisualizzaDoc, tvInserisciDoc;
    private ImageButton btnBack;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);

        btnBack = findViewById(R.id.btnBack);
        tvAnagrafica = findViewById(R.id.tvAnagrafica);
        tvTasse = findViewById(R.id.tvTasse);
        tvVisualizzaDoc = findViewById(R.id.tvVisualizzaDoc);
        tvInserisciDoc = findViewById(R.id.tvInserisciDoc);

        Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();

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
                Intent intent = new Intent(MenuUserActivity.this, TasseClienteActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
    }
}