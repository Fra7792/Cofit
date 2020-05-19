package com.cofitconsulting.cofit.admin;


import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class InserimentoTasseActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner tipoF24, annoRif, meseRif;
    private EditText importo, scadenza;
    private RadioGroup radioGroupPagato;
    private Button btnInserisci;
    private String userID;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasse_cliente);

        final Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();
        fStore = FirebaseFirestore.getInstance();


        btnBack = findViewById(R.id.btnBack);
        tipoF24 = findViewById(R.id.spinnerF24);
        annoRif = findViewById(R.id.spinnerAnno);
        meseRif = findViewById(R.id.spinnerMese);
        importo = findViewById(R.id.etImporto);
        scadenza = findViewById(R.id.etScadenza);
        radioGroupPagato = findViewById(R.id.radioGroup3);
        btnInserisci = findViewById(R.id.btnInserisci);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2017; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        annoRif.setAdapter(adapter);

        scadenza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anno = calendar.get(Calendar.YEAR);
                int mese = calendar.get(Calendar.MONTH);
                int giorno = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(InserimentoTasseActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, anno, mese, giorno);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String data = dayOfMonth + "/" + month + "/" + year;
                scadenza.setText(data);
            }
        };

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnInserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f24 = tipoF24.getSelectedItem().toString().trim();
                String anno = annoRif.getSelectedItem().toString().trim();
                String mese = meseRif.getSelectedItem().toString().trim();
                String valore = importo.getText().toString().trim() + "€";
                String dataScadenza = scadenza.getText().toString().trim();
                String totale = f24 + " " + mese + " " + anno;

                if(!(checkedRadioGroup(radioGroupPagato)))
                {
                    Toast.makeText(InserimentoTasseActivity.this, "Devi completare tutti i campi", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String pagato = selectedIdRadioGroup(radioGroupPagato);

                writeOnDatabaseTasse(f24, anno, mese, valore, dataScadenza, pagato, totale);
                Toast.makeText(InserimentoTasseActivity.this, "Inserimento avvenuto", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InserimentoTasseActivity.this, VisualizzaTasseAdminActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
                finish();
            }
        });

    }

    private void writeOnDatabaseTasse(String f24, String anno, String mese, String valore, String dataScadenza, String pagato, String totale){
        Map<String, Object> user = new HashMap<>();
        user.put("Tassa", totale);
        user.put("Importo", valore);
        user.put("Scadenza", dataScadenza);
        user.put("Pagato", pagato);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userID).document(f24 + " " + mese + " " + anno).set(user);

    }


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

    private String selectedIdRadioGroup(RadioGroup radioGroup){
        String scelta;
        // get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        RadioButton selectedRadioButton = findViewById(selectedId);
        scelta = selectedRadioButton.getText().toString();
        Toast.makeText(getApplicationContext(), scelta + " is selected", Toast.LENGTH_SHORT).show();
        return scelta;
    }

}
