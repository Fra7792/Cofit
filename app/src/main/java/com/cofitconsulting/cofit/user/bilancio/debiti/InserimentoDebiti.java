package com.cofitconsulting.cofit.user.bilancio.debiti;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cofitconsulting.cofit.MainActivity;
import com.cofitconsulting.cofit.R;

import java.util.Calendar;



public class InserimentoDebiti extends AppCompatActivity {

    private TextView titolo;
    private Button btnStore;
    private ImageButton btnBack;
    private Spinner ettipo;
    private EditText etdescrizione, etimporto, etdata;

    private DatabaseHelper databaseHelper;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserimento_conti);
        databaseHelper = new DatabaseHelper(this);

        titolo = findViewById(R.id.titolo);
        titolo.setText("Inserisci nuovo debito");
        btnStore = findViewById(R.id.btnAggiungi);
        btnBack = findViewById(R.id.btnBack);
        ettipo = findViewById(R.id.spinnerTipoDebito);
        etdescrizione = findViewById(R.id.editDescrizione);
        etimporto = findViewById(R.id.editImporto);
        etdata = findViewById(R.id.editScadenza);
        etdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anno = calendar.get(Calendar.YEAR);
                int mese = calendar.get(Calendar.MONTH);
                int giorno = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(InserimentoDebiti.this,
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
                databaseHelper.addUser(ettipo.getSelectedItem().toString(), etdescrizione.getText().toString(), etimporto.getText().toString(), etdata.getText().toString());
                etdescrizione.setText("");
                etimporto.setText("");
                etdata.setText("");
                Toast.makeText(InserimentoDebiti.this, "Elemento inserito!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InserimentoDebiti.this, MainActivity.class);
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

}

