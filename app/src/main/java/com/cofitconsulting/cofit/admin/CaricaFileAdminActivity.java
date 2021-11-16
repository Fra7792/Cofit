package com.cofitconsulting.cofit.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.sendNotificationPack.APIService;
import com.cofitconsulting.cofit.utility.sendNotificationPack.Client;
import com.cofitconsulting.cofit.utility.sendNotificationPack.Data;
import com.cofitconsulting.cofit.utility.sendNotificationPack.MyResponse;
import com.cofitconsulting.cofit.utility.sendNotificationPack.NotificationSender;
import com.cofitconsulting.cofit.utility.Utility;
import com.cofitconsulting.cofit.utility.model.ModelFile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CaricaFileAdminActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnScegliImag, btnCaricaImag;
    private EditText fileName;
    private Spinner spinnerNomeDoc;
    private ImageView imageView, btnBack;
    private ProgressBar progressBar;

    private String userID;
    private Uri fileUri;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private static final int CAMERA_REQUEST = 1888;
    private Utility utility = new Utility();

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;
    private FirebaseFirestore fStore;
    private String token;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carica_documento);

        //raccolto l'intent con l'id del cliente per poter caricare i documenti nella directory associata all'id
        Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();
        fStore = FirebaseFirestore.getInstance();

        btnScegliImag = findViewById(R.id.btnScegliFile);
        btnCaricaImag = findViewById(R.id.btnCarica);
        fileName = findViewById(R.id.nomeFile);
        spinnerNomeDoc = findViewById(R.id.spinner_nomeFile);
        adapterSpinner();
        imageView = findViewById(R.id.imageView);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar3);

        //mi collego allo storage e al database
        storageReference = FirebaseStorage.getInstance().getReference(userID);
        databaseReference = FirebaseDatabase.getInstance().getReference(userID);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        final DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                token = documentSnapshot.getString("Token").trim();
            }
        });


        //richiedo i permessi per poter accedere alla camera, memoria interna ed esterna
        btnScegliImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest = utility.findUnaskedPermissions(permissions, CaricaFileAdminActivity.this);
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
                    String nome = fileName.getText().toString().trim(); //il nome utilizzato per caricare il file sarà preso dal contenuto di filename
                    String default_ = "Tipo di documento";
                    String spinner = spinnerNomeDoc.getSelectedItem().toString().trim();

                //se la stringa nome è vuota dà errore
                    if(TextUtils.isEmpty(nome)) {
                        fileName.setError("Inserire il nome del file!");
                        return;
                    }

                    //se nello spinner non è selezionato nulla dà errore
                     else if(default_.equals(spinner))
                        {
                            Toast.makeText(CaricaFileAdminActivity.this, "Seleziona il tipo di documento da inviare", Toast.LENGTH_SHORT).show();
                            return;
                        }
                     else
                        {
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


    //facciamo comparire un AlertDialog per scegliere da dove prendere il file
    public void chooserIntent()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CaricaFileAdminActivity.this);
        builder.setTitle("Seleziona la sorgente");
        builder.setIcon(R.drawable.ic_insert_drive_file_black_24dp);
        String[] option = {"Gestione file","Fotocamera"};
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    openFileChooser();
                }
                if (which == 1)
                {
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


    //utilizziamo onActivityResult per prendere il contenuto dell'intent lanciato
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        //se è un'immagine presa dalla memoria interna o ed esterna
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            fileUri = data.getData();
            Picasso.get().load(fileUri).into(imageView);
        }

        //se è utilizzata la camera dello smartphone
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

  //per lanciare l'intent di gestione file
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    //per caricare l'immagine nello storage
    private void uploadFile() {
        if(fileUri != null)
        {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + utility.getFileExtension(fileUri, CaricaFileAdminActivity.this));

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

                            Toast.makeText(CaricaFileAdminActivity.this, "File caricato", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(! urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            String nomeCompleto = "COFIT - " + spinnerNomeDoc.getSelectedItem().toString() + " - " + fileName.getText().toString().trim();
                            ModelFile modelFile = new ModelFile(nomeCompleto, downloadUrl.toString());

                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(modelFile);

                            String message = spinnerNomeDoc.getSelectedItem().toString() + " - " + fileName.getText().toString().trim();

                            sendNotifications(token, "Hai un nuovo documento", message );

                            //una volta caricato il file chiudiamo questa activity e passiamo in quella per visualizzare tutti i file
                            Intent intent = new Intent(CaricaFileAdminActivity.this, VisualizzaFileAdminActivity.class);
                            intent.putExtra("User_ID", userID);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CaricaFileAdminActivity.this, "Caricamento fallito", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                        }
                    });
        } else
        {
            Toast.makeText(this, "Nessun file selezionato", Toast.LENGTH_SHORT).show();
        }

    }


    //Metodo per sapere se i permessi sono stati dati
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        if(requestCode == ALL_PERMISSIONS_RESULT)
        {
            for(String perm : permissionsToRequest)//per ogni permesso in permissionsToRequest
            {
                if(!(CaricaFileAdminActivity.this.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) //se non è stato dato
                {
                    permissionsRejected.add(perm);//lo aggiungiamo in permissionRejected
                }
            }
            if(permissionsRejected.size()>0) //se c'è almeno uno rifiutato diciamogli che dovrebbe accettarli con un TOAST
            {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0)))
                {
                    Toast.makeText(CaricaFileAdminActivity.this, "Approva tutto", Toast.LENGTH_SHORT).show();
                }
            }
            else //altrimenti puoi procedere per cambiare l'immagine.
            {
                chooserIntent();
            }
        }
    }

    private void adapterSpinner()
    {

        //crea un ArrayList con tutte le voci dello spinner
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Tipo di documento");
        arrayList.add("F24");
        arrayList.add("Dichiarazione");
        arrayList.add("Busta paga");
        arrayList.add("Comunicazione");
        arrayList.add("DURC");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNomeDoc.setAdapter(arrayAdapter);
    }

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(CaricaFileAdminActivity.this, "Errore ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}
