package com.cofitconsulting.cofit.user.registro_finanziario.debiti;

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
import com.cofitconsulting.cofit.utility.model.ModelRegistro;

import java.util.Calendar;


public class UpdateDeleteDebitiActivity extends AppCompatActivity {

    private ModelRegistro modelRegistro;
    private ImageButton btnBack;
    private TextView titoloDebito;
    private Spinner etTipo;
    private EditText etDescrizione, etImporto, etdata;
    private RadioGroup radioGroupPagato;
    private RadioButton pagSi, pagNo;
    private Button btnModifica, btnCancella;
    private DatabaseHelper databaseHelper;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_conti);

        Intent intent = getIntent();
        modelRegistro = (ModelRegistro) intent.getSerializableExtra("user");

        databaseHelper = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btnBack);
        etTipo = findViewById(R.id.spinnerTipo);
        etDescrizione = findViewById(R.id.editDescrizione);
        etImporto = findViewById(R.id.editImporto);
        etdata = findViewById(R.id.editData);
        radioGroupPagato = findViewById(R.id.radioGroup3);
        pagSi = findViewById(R.id.pagSi);
        pagNo = findViewById(R.id.pagNo);
        btnModifica = findViewById(R.id.btnModifica);
        btnCancella = findViewById(R.id.btnCancella);

        String tipoDeb = modelRegistro.getTipo();
        etTipo.setSelection(getIndex(etTipo, tipoDeb));
        etDescrizione.setText(modelRegistro.getDescrizione());
        etImporto.setText(modelRegistro.getImporto());
        etdata.setText(modelRegistro.getData());

        String pagato = modelRegistro.getPagato();
        if(pagato.equals("Sì"))
        {
            pagSi.setChecked(true);
        }
        else if(pagato.equals("No"))
        {
            pagNo.setChecked(true);
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnModifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipo = etTipo.getSelectedItem().toString();
                String descrizione = etDescrizione.getText().toString();
                String importo = etImporto.getText().toString();
                String data = etdata.getText().toString();
                String pagato = selectedIdRadioGroup(radioGroupPagato);

                if(TextUtils.isEmpty(descrizione)){   //TextUtils controlla la lunghezza della stringa
                    etDescrizione.setError("Inserire la descrizione!");
                    return;
                }
                if(TextUtils.isEmpty(importo)){   //TextUtils controlla la lunghezza della stringa
                    etImporto.setError("Inserire l'importo!");
                    return;
                }
                if(TextUtils.isEmpty(data)){   //TextUtils controlla la lunghezza della stringa
                    etdata.setError("Inserire la data!");
                    return;
                }

                databaseHelper.updateDebito(modelRegistro.getId(),tipo,descrizione,importo, data, pagato);
                Toast.makeText(UpdateDeleteDebitiActivity.this, "Elemento modificato!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateDeleteDebitiActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteDebito(modelRegistro.getId());
                Toast.makeText(UpdateDeleteDebitiActivity.this, "Elemento eliminato!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateDeleteDebitiActivity.this, MainActivity.class);
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
                DatePickerDialog dialog = new DatePickerDialog(UpdateDeleteDebitiActivity.this,
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

    //prendo l'indice della voce selezionata dallo spinner
    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    //recupero in formato stringa ciò che è selezionato dal radiogroup
    private String selectedIdRadioGroup(RadioGroup radioGroup){
        String scelta;

        int selectedId = radioGroup.getCheckedRadioButtonId();

        RadioButton selectedRadioButton = findViewById(selectedId);
        scelta = selectedRadioButton.getText().toString();
        return scelta;
    }
}
