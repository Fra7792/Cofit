package com.cofitconsulting.cofit.user.documenti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.admin.CaricaDocAdminActivity;
import com.cofitconsulting.cofit.admin.VisualizzaDocAdminActivity;
import com.cofitconsulting.cofit.utility.Utility;
import com.cofitconsulting.cofit.utility.strutture.StrutturaUpload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CaricaDocUsersActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private Button btnScegliImag, btnCaricaImag;
    private EditText text_fileName;
    private Spinner spinner_NomeFile;
    private ImageView imageView, btnBack;
    private ProgressBar progressBar;

    private String userID;
    private Uri fileUri;
    private String email;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private Utility utility = new Utility();


    private FirebaseAuth fAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carica_documento);

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnScegliImag = findViewById(R.id.btnScegliFile);
        btnCaricaImag = findViewById(R.id.btnCarica);
        text_fileName = findViewById(R.id.nomeFile);
        text_fileName.setVisibility(View.INVISIBLE);
        spinner_NomeFile = findViewById(R.id.spinner_nomeFile);
        imageView = findViewById(R.id.imageView);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar3);
        storageReference = FirebaseStorage.getInstance().getReference(userID);
        databaseReference = FirebaseDatabase.getInstance().getReference(userID);

        btnScegliImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest = utility.findUnaskedPermissions(permissions, CaricaDocUsersActivity.this);
                if(permissionsToRequest.size()>0)//se abbiamo qualche permesso da richiedere
                {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),ALL_PERMISSIONS_RESULT);
                }
                else {
                    chooserIntent();
                }
            }
        });

        btnCaricaImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String default_ = "Tipo di documento";
                String spinner = spinner_NomeFile.getSelectedItem().toString().trim();
                if(default_.equals(spinner))
                {
                    Toast.makeText(CaricaDocUsersActivity.this, "Seleziona il tipo di documento da inviare", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    uploadFile();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void chooserIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CaricaDocUsersActivity.this);
        builder.setTitle("Seleziona la sorgente");
        builder.setIcon(R.drawable.ic_insert_drive_file_black_24dp);
        String[] option = {"Gestione file","Fotocamera"};
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    openFileChooser();
                }
                if (which == 1) {
                   ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    fileUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAMERA_REQUEST);

                }
            }

        }).create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            fileUri = data.getData();
            Picasso.get().load(fileUri).into(imageView);
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            try {
               Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), fileUri);
               Bitmap image = utility.getResizedBitmap(thumbnail, 1417, 1024);
             fileUri =  utility.getImageUri(this, image);
                Picasso.get().load(fileUri).into(imageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private void uploadFile() {
        if (fileUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + utility.getFileExtension(fileUri, CaricaDocUsersActivity.this));

            uploadTask = fileReference.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(CaricaDocUsersActivity.this, "File caricato", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(! urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            String nome = spinner_NomeFile.getSelectedItem().toString().trim();
                            Date currentTime = Calendar.getInstance().getTime();
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String strDate = dateFormat.format(currentTime);
                            String fileName = nome + " (" + strDate + ")";
                            StrutturaUpload strutturaUpload = new StrutturaUpload(fileName, downloadUrl.toString());

                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(strutturaUpload);
                            writeOnDatabaseNotifiche(userID, email, strDate);

                            Intent intent = new Intent(CaricaDocUsersActivity.this, VisualizzaDocUsersActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CaricaDocUsersActivity.this, "Caricamento fallito", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            try {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            } catch (Exception e) {

                            }

                        }
                    });

        } else {
            Toast.makeText(this, "Nessun file selezionato", Toast.LENGTH_SHORT).show();
        }
    }


    //Metodo per sapere se i permessi sono stati dati
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        if(requestCode == ALL_PERMISSIONS_RESULT)
        {
            for(String perm : permissionsToRequest)//per ogni permesso in permissionsToRequest
            {
                if(!(CaricaDocUsersActivity.this.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) //se non è stato dato
                {
                    permissionsRejected.add(perm);//lo aggiungiamo in permissionRejected
                }
            }
            if(permissionsRejected.size()>0) //se c'è almeno uno rifiutato diciamogli che dovrebbe accettarli con un TOAST
            {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0)))
                {
                    Toast.makeText(CaricaDocUsersActivity.this, "Approva tutto", Toast.LENGTH_SHORT).show();
                }
            }
            else //altrimenti puoi procedere per cambiare l'immagine.
            {
                chooserIntent();
            }
        }
    }

    private void writeOnDatabaseNotifiche(String id, String email, String data){
        Map<String, Object> notifica = new HashMap<>();
        notifica.put("Id", id);
        notifica.put("Email", email);
        notifica.put("Data", data);
        notifica.put("Visto", "No");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifiche").document(userID).set(notifica);

    }

}
