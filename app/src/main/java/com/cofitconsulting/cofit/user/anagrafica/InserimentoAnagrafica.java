package com.cofitconsulting.cofit.user.anagrafica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;



public class InserimentoAnagrafica extends AppCompatActivity {

    EditText text_nome, text_indirizzo, text_inpsP, text_inpsD, text_inailP, text_inailD, text_iva, text_cf, text_rea;
    Spinner text_contabilita;
    RadioButton azienda, societa, professionista, ritSi, ritNo;
    RadioGroup radioGroupTipo;
    Button btnSalva;
    ImageButton btnBack;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_anagrafica);
        text_nome = findViewById(R.id.text_denominazione);
        text_indirizzo = findViewById(R.id.text_indirizzo);
        text_inpsP = findViewById(R.id.text_inpsP);
        text_inpsD = findViewById(R.id.text_inpsD);
        text_inailP = findViewById(R.id.text_inailP);
        text_inailD = findViewById(R.id.text_inailD);
        text_iva = findViewById(R.id.text_pIva);
        text_cf = findViewById(R.id.text_cf);
        text_rea = findViewById(R.id.text_rea);
        text_contabilita = findViewById(R.id.spinnerTipoContabilita);
        azienda = findViewById(R.id.tipoAzienda);
        societa = findViewById(R.id.tipoSocieta);
        professionista = findViewById(R.id.tipoProfessionista);
        radioGroupTipo = findViewById(R.id.radioGroup1);
        ritSi = findViewById(R.id.ritSi);
        ritNo = findViewById(R.id.ritNo);
        btnSalva = findViewById(R.id.btnSalva);
        btnBack = findViewById(R.id.btnBack);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final DocumentReference documentReference = fStore.collection("Anagrafica").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String cont = documentSnapshot.getString("Tipo di contabilità");
                text_nome.setText(documentSnapshot.getString("Denominazione"));
                text_indirizzo.setText(documentSnapshot.getString("Indirizzo"));
                text_inpsP.setText(documentSnapshot.getString("Inps Personale"));
                text_inailP.setText(documentSnapshot.getString("Inail Personale"));
                text_inpsD.setText(documentSnapshot.getString("Inps Dipendenti"));
                text_inailD.setText(documentSnapshot.getString("Inail Dipendenti"));
                text_iva.setText(documentSnapshot.getString("Partita IVA"));
                text_cf.setText(documentSnapshot.getString("Codice Fiscale"));
                text_rea.setText(documentSnapshot.getString("Codice REA"));
                text_contabilita.setSelection(getIndex(text_contabilita, cont));
            }
        });


        btnSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nome = text_nome.getText().toString();
                final String indirizzo= text_indirizzo.getText().toString();
                final String inailP = text_inailP.getText().toString();
                final String inpsD = text_inpsD.getText().toString();
                final String inailD = text_inailD.getText().toString();
                final String inpsP = text_inpsP.getText().toString();
                final String iva = text_iva.getText().toString();
                final String cf = text_cf.getText().toString();
                final String rea = text_rea.getText().toString();
                final String contabilita = text_contabilita.getSelectedItem().toString();
                final String tipo_cliente = selectedButton_Cliente();
                final String ritenuta = selectedButton_Ritenuta();
                final String email = fAuth.getCurrentUser().getEmail();

                String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                writeOnDatabaseAnagrafica(nome, indirizzo, inpsP, inpsD, inailP, inailD, iva, cf, rea, contabilita, tipo_cliente, ritenuta, email, uid);
                Toast.makeText(InserimentoAnagrafica.this, "Inserimento avvenuto", Toast.LENGTH_SHORT).show();
                writeOnDatabaseUser(nome, email, userId);
                finish();
            }
        });
    }

    private void writeOnDatabaseAnagrafica(String nome, String indirizzo, String inpsP, String inpsD, String inailP, String inailD, String iva, String cf, String rea, String contabilita, String tipo_cliente, String ritenuta, String email, String uid){
        Map<String, Object> user = new HashMap<>();
        user.put("Id", uid);
        user.put("Email", email);
        user.put("Denominazione", nome);
        user.put("Indirizzo", indirizzo);
        user.put("Inps Personale", inpsP);
        user.put("Inail Personale", inailP);
        user.put("Inps Dipendenti", inpsD);
        user.put("Inail Dipendenti", inailD);
        user.put("Partita IVA", iva);
        user.put("Codice Fiscale", cf);
        user.put("Codice REA", rea);
        user.put("Tipo di contabilità", contabilita);
        user.put("Tipo cliente", tipo_cliente);
        user.put("Ritenuta d'acconto", ritenuta);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Anagrafica").document(uid).set(user);
    }

    private void writeOnDatabaseUser(String nome,  String email, String uid) {
        Map<String, Object> user = new HashMap<>();
        user.put("Id", uid);
        user.put("Email", email);
        user.put("Denominazione", nome);
        user.put("search", nome.toLowerCase());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).set(user);
    }

    public String selectedButton_Cliente(){
        String scelta=" ";
        if (azienda.isChecked()) {
            scelta = azienda.getText().toString();
        } else if (professionista.isChecked()) {
            scelta = professionista.getText().toString();
        } else if (societa.isChecked()) {
            scelta = societa.getText().toString();
        }
        return scelta;
    }

    public String selectedButton_Ritenuta(){
        String scelta=" ";
        if (ritSi.isChecked()) {
            scelta = ritSi.getText().toString();
        } else if (ritNo.isChecked()) {
            scelta = ritNo.getText().toString();
        }
        return scelta;
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

}


