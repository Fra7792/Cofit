package com.cofitconsulting.cofit.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class AnagraficaClienteActivity extends AppCompatActivity {

    private String userID;
    private TextView nome, numero, email, indirizzo_completo, contabilita, inpsP, inpsD, inailP, inailD, pi, cf, rea, ritenuta, tipo_azienda;
    private ImageButton btnBack, btnModifica, btnChiamata, btnEmail;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_anagrafica);

        Intent intent = getIntent();
        btnBack = findViewById(R.id.btnBack);
        btnChiamata = findViewById(R.id.btnChiamata);
        btnEmail = findViewById(R.id.btnEmail);
        numero = findViewById(R.id.textNumero);
        email = findViewById(R.id.textEmail);
        userID = intent.getStringExtra("User_ID").trim();
        tipo_azienda = findViewById(R.id.textTipoAzienda);
        nome = findViewById(R.id.textNome);
        indirizzo_completo = findViewById(R.id.textIndizzo);
        contabilita = findViewById(R.id.text_contabilita);
        inpsP = findViewById(R.id.textInpsP);
        inpsD = findViewById(R.id.textInpsD);
        inailP = findViewById(R.id.textInailP);
        inailD = findViewById(R.id.textInailD);
        pi = findViewById(R.id.text_pIva);
        cf = findViewById(R.id.text_cf);
        rea = findViewById(R.id.text_rea);
        ritenuta = findViewById(R.id.text_ritenuta);
        btnModifica = findViewById(R.id.btnModifica);
        btnModifica.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fAuth.getCurrentUser();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final DocumentReference documentReference = fStore.collection("Anagrafica").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                tipo_azienda.setText("Tipo Cliente: " + documentSnapshot.getString("Tipo cliente"));
                nome.setText(documentSnapshot.getString("Denominazione"));
                email.setText(documentSnapshot.getString("Email"));
                String citta = documentSnapshot.getString("Città");
                String indirizzo = documentSnapshot.getString("Indirizzo");
                indirizzo_completo.setText(citta + ", " + indirizzo);
                numero.setText(documentSnapshot.getString("Numero di telefono"));
                contabilita.setText(documentSnapshot.getString("Tipo di contabilità"));
                inpsP.setText(documentSnapshot.getString("Inps Personale"));
                inailP.setText(documentSnapshot.getString("Inail Personale"));
                inpsD.setText(documentSnapshot.getString("Inps Dipendenti"));
                inailD.setText(documentSnapshot.getString("Inail Dipendenti"));
                pi.setText(documentSnapshot.getString("Partita IVA"));
                cf.setText(documentSnapshot.getString("Codice Fiscale"));
                rea.setText(documentSnapshot.getString("Codice REA"));
                ritenuta.setText(documentSnapshot.getString("Ritenuta d'acconto"));
            }
        });

        btnChiamata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = numero.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ind_email = email.getText().toString();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{ind_email});
                try {
                    startActivity(Intent.createChooser(i, "Invia email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AnagraficaClienteActivity.this, "Client di posta non disponibile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        indirizzo_completo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String indirizzo = indirizzo_completo.getText().toString();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q="+indirizzo));
                startActivity(intent);
            }
        });

    }
}
