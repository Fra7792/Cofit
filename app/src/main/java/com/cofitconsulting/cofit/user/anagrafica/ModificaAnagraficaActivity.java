package com.cofitconsulting.cofit.user.anagrafica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cofitconsulting.cofit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class ModificaAnagraficaActivity extends AppCompatActivity {

    private TextView nome, numero, email, indirizzo_completo, contabilita, inpsP, inpsD, inailP, inailD, pi, cf, rea, ritenuta, tipo_azienda;
    private ImageButton btnBack, btnModifica;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_anagrafica);
        btnBack = findViewById(R.id.btnBack);
        tipo_azienda = findViewById(R.id.textTipoAzienda);
        nome = findViewById(R.id.textNome);
        numero = findViewById(R.id.textNumero);
        email = findViewById(R.id.textEmail);
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
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = fStore.collection("Anagrafica").document(userId);
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

        btnModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModificaAnagraficaActivity.this, InserimentoAnagraficaActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
