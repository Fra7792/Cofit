package com.cofitconsulting.cofit.user.anagrafica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cofitconsulting.cofit.MainActivity;
import com.cofitconsulting.cofit.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ModificaAnagraficaActivity extends AppCompatActivity {

    private TextView nome, numero, numeroCell, email, indirizzo_completo, contabilita, pi, cf, tipo_azienda;
    private CircleImageView profileImage;
    private ImageButton btnBack, btnModifica;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private static final int GALLERY_INTENT_CODE = 1023;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_anagrafica);

        profileImage = findViewById(R.id.profileImage);
        btnBack = findViewById(R.id.btnBack);
        tipo_azienda = findViewById(R.id.textTipoAzienda);
        nome = findViewById(R.id.textNome);
        numero = findViewById(R.id.textNumero);
        numeroCell = findViewById(R.id.textCellulare);
        email = findViewById(R.id.textEmail);
        indirizzo_completo = findViewById(R.id.textIndizzo);
        contabilita = findViewById(R.id.text_contabilita);
        pi = findViewById(R.id.text_pIva);
        cf = findViewById(R.id.text_cf);
        btnModifica = findViewById(R.id.btnModifica);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ModificaAnagraficaActivity.this, MainActivity.class));
                finish();
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference profileRef = storageReference.child("users/" + userId + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

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
                numeroCell.setText(documentSnapshot.getString("Numero di cellulare"));
                contabilita.setText(documentSnapshot.getString("Tipo di contabilità"));
                pi.setText(documentSnapshot.getString("Partita IVA"));
                cf.setText(documentSnapshot.getString("Codice Fiscale"));
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

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, GALLERY_INTENT_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                uploadImageToFirebase(fileUri);

            }
        }
    }

    private void uploadImageToFirebase(final Uri imageUri) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Caricamento in corso...");
        pd.show();
        final StorageReference fileRef = storageReference.child("users/" + userId + "profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pd.dismiss();
                        Picasso.get()
                                .load(uri)
                                .into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ModificaAnagraficaActivity.this, "Caricamento fallito", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ModificaAnagraficaActivity.this, MainActivity.class));
        finish();
    }
}
