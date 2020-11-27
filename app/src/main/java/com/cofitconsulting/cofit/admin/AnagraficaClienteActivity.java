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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class AnagraficaClienteActivity extends AppCompatActivity {

    private String userID;
    private TextView nome, numero, numeroCell, email, indirizzo_completo, contabilita, pi, cf, tipo_azienda;
    private ImageButton btnChiamata, btnEmail;
    private CircleImageView profileImage;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profilo);


        //raccolgo l'intent con l'id del cliente
        Intent intent = getIntent();

        profileImage = findViewById(R.id.profileImage);
        btnChiamata = findViewById(R.id.btnChiamata);
        btnEmail = findViewById(R.id.btnEmail);
        numero = findViewById(R.id.textNumero);
        numeroCell = findViewById(R.id.textCellulare);
        email = findViewById(R.id.textEmail);
        userID = intent.getStringExtra("User_ID").trim();
        tipo_azienda = findViewById(R.id.textTipoAzienda);
        nome = findViewById(R.id.textNome);
        indirizzo_completo = findViewById(R.id.textIndizzo);
        contabilita = findViewById(R.id.text_contabilita);
        pi = findViewById(R.id.text_pIva);
        cf = findViewById(R.id.text_cf);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fAuth.getCurrentUser();



        //mi collego allo storage di firebase per riprendere l'immagine del profilo
        storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference profileRef = storageReference.child("users/" + userID + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        //mi collego al database di firebase per riprendere i dati anagrafici del cliente
        final DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                tipo_azienda.setText("Tipo Cliente: " + documentSnapshot.getString("Tipo cliente"));
                nome.setText(documentSnapshot.getString("Denominazione"));
                email.setText(documentSnapshot.getString("Email"));
                String citta = documentSnapshot.getString("Città");
                String indirizzo = documentSnapshot.getString("Indirizzo");
                String numeroCivico = documentSnapshot.getString("Numero civico");
                String nTelefono = documentSnapshot.getString("Numero di telefono");
                String pIva = documentSnapshot.getString("Partita IVA");
                String cFiscale = documentSnapshot.getString("Codice Fiscale");
                String nCellulare = documentSnapshot.getString("Numero di cellulare");
                indirizzo_completo.setText(indirizzo + " " + numeroCivico + ", " + citta);
                numero.setText(nTelefono);
                numeroCell.setText(nCellulare);
                contabilita.setText(documentSnapshot.getString("Tipo di contabilità"));
                pi.setText(pIva);
                cf.setText(cFiscale);

                if(nTelefono.isEmpty())
                {
                    numero.setText("Non inserito");
                }
                if(pIva.isEmpty())
                {
                    pi.setText("Non inserita");
                }
                if(cFiscale.isEmpty())
                {
                    cf.setText("Non inserito");
                }
                if(nCellulare.isEmpty())
                {
                    numeroCell.setText("Non inserito");
                }
            }
        });

        btnChiamata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = numeroCell.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        numero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroFisso = numero.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", numeroFisso, null));
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

        //google maps associato all'indirizzo del cliente
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
