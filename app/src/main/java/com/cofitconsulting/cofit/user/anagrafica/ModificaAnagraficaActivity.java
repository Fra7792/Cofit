package com.cofitconsulting.cofit.user.anagrafica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
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

import com.cofitconsulting.cofit.user.documenti.CaricaDocUsersActivity;
import com.cofitconsulting.cofit.utility.Utility;
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

import java.util.ArrayList;
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

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private Utility utility = new Utility();

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

        //recupero l'immagine del profilo dallo storage di firebase
        final StorageReference profileRef = storageReference.child("users/" + userId + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        //recupero le informazioni dal database e setto le textView
        final DocumentReference documentReference = fStore.collection("Users").document(userId);
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

        //richiedo i permessi se non sono già stati dati, se non ci sono procedo ad aprire la galleria
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest = utility.findUnaskedPermissions(permissions, ModificaAnagraficaActivity.this);
                if(permissionsToRequest.size()>0)//se abbiamo qualche permesso da richiedere
                {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),ALL_PERMISSIONS_RESULT);
                }
                else {
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, GALLERY_INTENT_CODE);
                }
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        if(requestCode == ALL_PERMISSIONS_RESULT)
        {
            for(String perm : permissionsToRequest)//per ogni permesso in permissionsToRequest
            {
                if(!(ModificaAnagraficaActivity.this.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) //se non è stato dato
                {
                    permissionsRejected.add(perm);//lo aggiungiamo in permissionRejected
                }
            }
            if(permissionsRejected.size()>0) //se c'è almeno uno rifiutato diciamogli che dovrebbe accettarli con un TOAST
            {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0)))
                {
                    Toast.makeText(ModificaAnagraficaActivity.this, "Approva tutto", Toast.LENGTH_SHORT).show();
                }
            }
            else //altrimenti puoi procedere per cambiare l'immagine.
            {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, GALLERY_INTENT_CODE);
            }
        }
    }

    //recupero l'immagine dalla galleria
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


    //carica l'immagine del profilo nello storage di firebase
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

    //se premo indietro torno nella mainActivity
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ModificaAnagraficaActivity.this, MainActivity.class));
        finish();
    }
}
