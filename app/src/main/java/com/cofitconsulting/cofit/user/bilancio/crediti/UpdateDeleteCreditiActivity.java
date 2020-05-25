package com.cofitconsulting.cofit.user.bilancio.crediti;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cofitconsulting.cofit.MainActivity;
import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.strutture.StrutturaConto;

import java.util.ArrayList;
import java.util.Calendar;


public class UpdateDeleteCreditiActivity extends AppCompatActivity {
    private StrutturaConto strutturaConto;
    private TextView titoloDebito;
    private Spinner etTipo;
    private EditText etDescrizione, etImporto, etdata;
    private Button btnModifica, btnCancella;
    private ImageButton btnBack;
    private DatabaseHelper databaseHelper;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_conti);

        Intent intent = getIntent();
        strutturaConto = (StrutturaConto) intent.getSerializableExtra("user");

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Obbligazioni e obbligazioni convertibili");
        arrayList.add("Crediti verso soci per finanziamenti");
        arrayList.add("Crediti verso altri finanziatori");
        arrayList.add("Crediti verso clienti");
        arrayList.add("Crediti rappresentati da titoli di credito");
        arrayList.add("Crediti verso imprese controllate");
        arrayList.add("Acconti");
        arrayList.add("Crediti verso imprese collegate");
        arrayList.add("Crediti verso controllanti");
        arrayList.add("Credi verso imprese sottoposte al controllo delle controllanti");
        arrayList.add("Crediti tributari");
        arrayList.add("Crediti verso istituti di previdenza e di sicurezza sociale");
        arrayList.add("Altri crediti");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        databaseHelper = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btnBack);
        etTipo = findViewById(R.id.spinnerTipo);
        etTipo.setAdapter(arrayAdapter);
        etDescrizione = findViewById(R.id.editDescrizione);
        etImporto = findViewById(R.id.editImporto);
        etdata = findViewById(R.id.editData);
        btnModifica = findViewById(R.id.btnModifica);
        btnCancella = findViewById(R.id.btnCancella);

        String tipoDeb = strutturaConto.getTipo();
        etTipo.setSelection(getIndex(etTipo, tipoDeb));
        etDescrizione.setText(strutturaConto.getDescrizione());
        etImporto.setText(strutturaConto.getImporto());
        etdata.setText(strutturaConto.getData());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.updateUser(strutturaConto.getId(),etTipo.getSelectedItem().toString(),etDescrizione.getText().toString(),etImporto.getText().toString(), etdata.getText().toString());
                Toast.makeText(UpdateDeleteCreditiActivity.this, "Elemento modificato!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateDeleteCreditiActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteUSer(strutturaConto.getId());
                Toast.makeText(UpdateDeleteCreditiActivity.this, "Elemento eliminato!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateDeleteCreditiActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        etdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anno = calendar.get(Calendar.YEAR);
                int mese = calendar.get(Calendar.MONTH);
                int giorno = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(UpdateDeleteCreditiActivity.this,
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

