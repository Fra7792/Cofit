package com.cofitconsulting.cofit.user.anagrafica;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.cofitconsulting.cofit.MainActivity;
import com.cofitconsulting.cofit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class InserimentoAnagraficaActivity extends AppCompatActivity {

    private EditText text_nome, text_citta, text_indirizzo, text_numeroCivico, text_numero, text_cellulare, text_iva, text_cf;
    private Spinner text_contabilita;
    private RadioGroup radioGroupTipo;
    private RadioButton azienda, societa, professionista;
    private Button btnSalva;
    private ImageButton btnBack;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_anagrafica);
        text_nome = findViewById(R.id.text_denominazione);
        text_citta = findViewById(R.id.text_citta);
        text_indirizzo = findViewById(R.id.text_indirizzo);
        text_numeroCivico = findViewById(R.id.text_numeroCivico);
        text_numero = findViewById(R.id.text_numero);
        text_cellulare = findViewById(R.id.text_cellulare);
        text_iva = findViewById(R.id.text_pIva);
        text_cf = findViewById(R.id.text_cf);
        text_cf.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        text_contabilita = findViewById(R.id.spinnerTipoContabilita);
        azienda = findViewById(R.id.tipoAzienda);
        societa = findViewById(R.id.tipoSocieta);
        professionista = findViewById(R.id.tipoProfessionista);

        radioGroupTipo = findViewById(R.id.radioGroup1);

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

        //recuupero le informazioni già caricate in database e setto le edittext
        final DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    String cont = documentSnapshot.getString("Tipo di contabilità");
                    text_nome.setText(documentSnapshot.getString("Denominazione"));
                    text_citta.setText(documentSnapshot.getString("Città"));
                    text_indirizzo.setText(documentSnapshot.getString("Indirizzo"));
                    text_numeroCivico.setText(documentSnapshot.getString("Numero civico"));
                    text_numero.setText(documentSnapshot.getString("Numero di telefono"));
                    text_cellulare.setText(documentSnapshot.getString("Numero di cellulare"));
                    text_iva.setText(documentSnapshot.getString("Partita IVA"));
                    text_cf.setText(documentSnapshot.getString("Codice Fiscale"));
                    text_contabilita.setSelection(getIndex(text_contabilita, cont));
                    String tipoCliente = documentSnapshot.getString("Tipo cliente");

                    //seleziono il radiobutton relazivo al tipo cliente salvato nel database
                    if (tipoCliente.equals("Società")) {
                        societa.setChecked(true);
                    } else if (tipoCliente.equals("Professionista")) {
                        professionista.setChecked(true);
                    } else if (tipoCliente.equals("Azienda")) {
                        azienda.setChecked(true);
                    }
                }catch (NullPointerException E)
                {

                }
                }
        });

        //salvo nel database i dati modificati
        btnSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nome = text_nome.getText().toString();
                final String indirizzo = text_indirizzo.getText().toString();
                final String numeroCivico = text_numeroCivico.getText().toString();
                final String citta = text_citta.getText().toString();
                final String numero = text_numero.getText().toString();
                final String cellulare = text_cellulare.getText().toString();
                final String iva = text_iva.getText().toString();
                final String cf = text_cf.getText().toString();
                final String contabilita = text_contabilita.getSelectedItem().toString();
                final String email = fAuth.getCurrentUser().getEmail();

                int countCF = cf.length();
                int countIva = iva.length();

                if(!(checkedRadioGroup(radioGroupTipo)))
                {
                   Toast.makeText(InserimentoAnagraficaActivity.this, "Devi completare tutti i campi", Toast.LENGTH_SHORT).show();
                   return;
                }
                final String tipo_cliente = selectedIdRadioGroup(radioGroupTipo);
                if(TextUtils.isEmpty(nome)){   //TextUtils controlla la lunghezza della stringa
                    text_nome.setError("Inserire la denominazione!");
                    return;
                }
                if(TextUtils.isEmpty(indirizzo)){   //TextUtils controlla la lunghezza della stringa
                    text_indirizzo.setError("Inserire l'indirizzo!");
                    return;
                }

                if(TextUtils.isEmpty(numeroCivico)){
                    text_numeroCivico.setError("Inserire il numero civico");
                }

                if(TextUtils.isEmpty(citta)){   //TextUtils controlla la lunghezza della stringa
                    text_citta.setError("Inserire la città!");
                    return;
                }
                if(TextUtils.isEmpty(cellulare) && TextUtils.isEmpty(numero)){   //TextUtils controlla la lunghezza della stringa
                    text_cellulare.setError("Numero di cellulare mancante!");
                    text_numero.setError("Numero di telefono mancante");
                    Toast.makeText(InserimentoAnagraficaActivity.this, "Inserire almeno un numero di telefono!", Toast.LENGTH_LONG).show();
                    return;
                }
                if((countCF < 16 || countCF > 16) && (countIva < 11 || countIva > 11)){
                    text_iva.setError("Partita IVA non corretta");
                    text_cf.setError("Codice Fiscale non corretto");
                    Toast.makeText(InserimentoAnagraficaActivity.this, "Uno tra Partita IVA e Codice Fiscale è obbligatorio!", Toast.LENGTH_LONG).show();
                    return;
                }
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String tokenUser = FirebaseInstanceId.getInstance().getToken();
                writeOnDatabaseUsers(nome, citta, indirizzo, numeroCivico, numero, cellulare, iva, cf, contabilita, tipo_cliente, email, uid, tokenUser);
                Toast.makeText(InserimentoAnagraficaActivity.this, "Inserimento avvenuto", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //scrivo nel database
    private void writeOnDatabaseUsers(String nome, String citta, String indirizzo, String numeroCivico,
                                      String numero, String cellulare, String iva, String cf, String contabilita,
                                      String tipo_cliente, String email, String uid, String token){
        Map<String, Object> user = new HashMap<>();
        user.put("Id", uid);
        user.put("Token", token);
        user.put("Email", email);
        user.put("Denominazione", nome);
        user.put("Numero di telefono", numero);
        user.put("Numero di cellulare", cellulare);
        user.put("Città", citta);
        user.put("Indirizzo", indirizzo);
        user.put("Numero civico", numeroCivico);
        user.put("Partita IVA", iva);
        user.put("Codice Fiscale", cf);
        user.put("Tipo di contabilità", contabilita);
        user.put("Tipo cliente", tipo_cliente);
        user.put("search", nome.toLowerCase());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).set(user);
    }

    //metodo per recuperare l'indice selezionato nello spinner
    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    //verifico che il radiogroup sia stato selezionato
    private boolean checkedRadioGroup(RadioGroup radioGroup){
        if(radioGroup.getCheckedRadioButtonId()==-1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    //recupero ciò che è stato selezionato
    private String selectedIdRadioGroup(RadioGroup radioGroup){
        String scelta;

            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            scelta = selectedRadioButton.getText().toString();
        return scelta;
    }

}


