package com.cofitconsulting.cofit.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cofitconsulting.cofit.Login;
import com.cofitconsulting.cofit.R;
import com.google.firebase.auth.FirebaseAuth;


public class AdminActivity extends AppCompatActivity {

    private Button btn, btnClienti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btn = findViewById(R.id.button2);
        btnClienti = findViewById(R.id.btnClienti);

        btnClienti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ListaClienti.class);
                startActivity(intent);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
