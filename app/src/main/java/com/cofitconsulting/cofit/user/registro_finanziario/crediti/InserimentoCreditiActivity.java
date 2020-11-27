package com.cofitconsulting.cofit.user.registro_finanziario.crediti;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cofitconsulting.cofit.MainActivity;
import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.Utility;

import java.util.Calendar;



public class InserimentoCreditiActivity extends AppCompatActivity {

    private Button btnStore;
    private ImageButton btnBack;
    private TextView titolo, textView2;
    private Spinner ettipo;
    private EditText etdescrizione, etimporto, etdata;
    private RadioGroup radioGroupPagato;
    private DatabaseHelper databaseHelper;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Utility utility = new Utility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_conti);

        databaseHelper = new DatabaseHelper(this);

        titolo = findViewById(R.id.titolo);
        titolo.setText("Inserisci nuovo credito");
        btnStore = findViewById(R.id.btnAggiungi);
        btnBack = findViewById(R.id.btnBack);
        textView2 = findViewById(R.id.textView2);
        textView2.setText("Seleziona il tipo di credito");
        ettipo = findViewById(R.id.spinnerTipoDebito);
        utility.adapterSpinner(InserimentoCreditiActivity.this, ettipo);
        etdescrizione = findViewById(R.id.editDescrizione);
        etimporto = findViewById(R.id.editImporto);
        radioGroupPagato = findViewById(R.id.radioGroup3);
        etdata = findViewById(R.id.editScadenza);
        etdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anno = calendar.get(Calendar.YEAR);
                int mese = calendar.get(Calendar.MONTH);
                int giorno = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(InserimentoCreditiActivity.this,
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
                etdata.setText(data);
            }
        };

        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipo = ettipo.getSelectedItem().toString();
                String descrizione = etdescrizione.getText().toString();
                String importo = etimporto.getText().toString();
                String data = etdata.getText().toString();
                String pagato = selectedIdRadioGroup(radioGroupPagato);

                if(TextUtils.isEmpty(descrizione)){   //TextUtils controlla la lunghezza della stringa
                    etdescrizione.setError("Inserire la descrizione!");
                    return;
                }
                if(TextUtils.isEmpty(importo)){   //TextUtils controlla la lunghezza della stringa
                    etimporto.setError("Inserire l'importo!");
                    return;
                }
                if(TextUtils.isEmpty(data)){   //TextUtils controlla la lunghezza della stringa
                    etdata.setError("Inserire la data!");
                    return;
                }

                databaseHelper.addCredito(tipo, descrizione, importo, data, pagato);
                Toast.makeText(InserimentoCreditiActivity.this, "Elemento inserito!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InserimentoCreditiActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //recupero ciò che ho selezionato dal radiogroup
    private String selectedIdRadioGroup(RadioGroup radioGroup){
        String scelta;

        int selectedId = radioGroup.getCheckedRadioButtonId();

        RadioButton selectedRadioButton = findViewById(selectedId);
        scelta = selectedRadioButton.getText().toString();
        return scelta;
    }
}

