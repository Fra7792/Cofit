package com.cofitconsulting.cofit.admin;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;

import com.cofitconsulting.cofit.utility.sendNotificationPack.APIService;
import com.cofitconsulting.cofit.utility.sendNotificationPack.Client;
import com.cofitconsulting.cofit.utility.sendNotificationPack.Data;
import com.cofitconsulting.cofit.utility.sendNotificationPack.MyResponse;
import com.cofitconsulting.cofit.utility.sendNotificationPack.NotificationSender;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InserimentoTasseActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner tipoF24, annoRif, meseRif;
    private EditText importo, scadenza;
    private Button btnInserisci;
    private String userID;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseFirestore fStore;
    private String token;
    private APIService apiService;
    private String blockCharacterSet = "@_-+*()=/:;'€~#^|$%&*!?,";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasse_cliente);

        //raccolto l'intent con l'id del cliente
        final Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();
        fStore = FirebaseFirestore.getInstance();


        btnBack = findViewById(R.id.btnBack);
        tipoF24 = findViewById(R.id.spinnerF24);
        annoRif = findViewById(R.id.spinnerAnno);
        meseRif = findViewById(R.id.spinnerMese);
        importo = findViewById(R.id.etImporto);
        importo.setFilters(new InputFilter[] {filter});
        scadenza = findViewById(R.id.etScadenza);
        btnInserisci = findViewById(R.id.btnInserisci);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);



        //creo un arraylist con gli anni che vanno dal 2017 ad oggi
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 2017; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        annoRif.setAdapter(adapter);

        //uso un oggetto di tipo Calendar per inserire la data che mi serve
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

       final DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                token = documentSnapshot.getString("Token").trim();
            }
        });

        btnInserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prendo i dati dalle edittext presenti
                String f24 = tipoF24.getSelectedItem().toString().trim();
                String anno = annoRif.getSelectedItem().toString().trim();
                String mese = meseRif.getSelectedItem().toString().trim();
                Double valore;
                String dataScadenza = scadenza.getText().toString().trim();
                String title = f24 + " " + mese + " " + anno;

                if(TextUtils.isEmpty(importo.getText().toString())){   //TextUtils controlla la lunghezza della stringa
                    importo.setError("Inserire l'importo!");
                    return;
                }
                else
                {
                    valore = Double.parseDouble(importo.getText().toString());

                }
                if(TextUtils.isEmpty(dataScadenza)){   //TextUtils controlla la lunghezza della stringa
                    scadenza.setError("Inserire la data di scadenza!");
                    return;
                }
                writeOnDatabaseTasse(f24, anno, mese, valore, dataScadenza, title);

                String message = title;

               sendNotifications(token, "Hai una nuova scadenza", message );

                //fino a qui
                Toast.makeText(InserimentoTasseActivity.this, "Inserimento avvenuto", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InserimentoTasseActivity.this, VisualizzaTasseAdminActivity.class);
                intent.putExtra("User_ID", userID);
                startActivity(intent);
                finish();
            }
        });

    }

    //inserisco nel database la tassa del cliente, la raccolta prende il nome dall'id del cliente
    private void writeOnDatabaseTasse(String f24, String anno, String mese, Double valore, String dataScadenza, String totale){
        Map<String, Object> user = new HashMap<>();
        user.put("Tassa", totale);
        user.put("Importo", valore);
        user.put("Scadenza", dataScadenza);
        user.put("Pagato", false);
        user.put("Permesso pagamento", false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userID).document(f24 + " " + mese + " " + anno).set(user);

    }

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(InserimentoTasseActivity.this, "Errore ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}
