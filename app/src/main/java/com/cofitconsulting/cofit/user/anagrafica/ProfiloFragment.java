package com.cofitconsulting.cofit.user.anagrafica;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfiloFragment extends Fragment {

    private TextView nome, numero, numeroCell, email, indirizzo_completo, contabilita, pi, cf, tipo_azienda, viewNumero, viewIVA, viewCF;
    private CircleImageView profileImage;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View v =  inflater.inflate(R.layout.fragment_profilo, container, false);

        profileImage = v.findViewById(R.id.profileImage);
        tipo_azienda = v.findViewById(R.id.textTipoAzienda);
        nome = v.findViewById(R.id.textNome);
        numero = v.findViewById(R.id.textNumero);
        numeroCell = v.findViewById(R.id.textCellulare);
        email = v.findViewById(R.id.textEmail);
        indirizzo_completo = v.findViewById(R.id.textIndizzo);
        contabilita = v.findViewById(R.id.text_contabilita);
        pi = v.findViewById(R.id.text_pIva);
        cf = v.findViewById(R.id.text_cf);
        viewCF = v.findViewById(R.id.text_cf);
        viewIVA = v.findViewById(R.id.text_pIva);
        viewNumero = v.findViewById(R.id.text_numero);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();




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
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    try {
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
                    if (nTelefono.isEmpty()) {
                        numero.setText("Non inserito");
                    }
                    if (pIva.isEmpty()) {
                        pi.setText("Non inserita");
                    }
                    if (cFiscale.isEmpty()) {
                        cf.setText("Non inserito");
                    }
                    if (nCellulare.isEmpty()) {
                        numeroCell.setText("Non inserito");
                    }
                    } catch (Exception E)
                    {

                    }
                }
            });

        //richiedo i permessi se non sono già stati dati, se non ci sono procedo ad aprire la galleria
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest = utility.findUnaskedPermissions(permissions, getContext());
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
        
        return v;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        if(requestCode == ALL_PERMISSIONS_RESULT)
        {
            for(String perm : permissionsToRequest)//per ogni permesso in permissionsToRequest
            {
                if(!(getActivity().checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) //se non è stato dato
                {
                    permissionsRejected.add(perm);//lo aggiungiamo in permissionRejected
                }
            }
            if(permissionsRejected.size()>0) //se c'è almeno uno rifiutato diciamogli che dovrebbe accettarli con un TOAST
            {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0)))
                {
                    Toast.makeText(getContext(), "Approva tutto", Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri fileUri = data.getData();
                uploadImageToFirebase(fileUri);

            }
        }
    }

    //carica l'immagine del profilo nello storage di firebase
    private void uploadImageToFirebase(final Uri imageUri) {
        final ProgressDialog pd = new ProgressDialog(getContext());
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
                Toast.makeText(getContext(), "Caricamento fallito", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
