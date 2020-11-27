package com.cofitconsulting.cofit.user.documenti;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    private Button btnScegliImag, btnCaricaImag;
    private Spinner spinner_NomeFile;
    private ImageView imageView;
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
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask;
    private String token;
    private String tokenProva;
    private APIService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_file, container, false);

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnScegliImag = v.findViewById(R.id.btnScegliFile);
        btnCaricaImag = v.findViewById(R.id.btnCarica);
        spinner_NomeFile = v.findViewById(R.id.spinner_nomeFile);
        imageView = v.findViewById(R.id.imageView);
        progressBar = v.findViewById(R.id.progressBar3);

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference(userID);
        databaseReference = FirebaseDatabase.getInstance().getReference(userID);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

            DocumentReference documentReference = fStore.collection("Token").document("Token");
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    try {
                        token = documentSnapshot.getString("Token").trim();
                    } catch (NullPointerException E)
                    {

                    }

                }
            });

            DocumentReference documentReference1 = fStore.collection("Token").document("TokenProva");
            documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    try {
                        tokenProva = documentSnapshot.getString("Token").trim();
                    } catch (NullPointerException E){

                    }
                }
            });

        btnScegliImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest = utility.findUnaskedPermissions(permissions, getActivity());
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
                    Toast.makeText(getActivity(), "Seleziona il tipo di documento da inviare", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    uploadFile();
                }
            }
        });

        return v;
    }
    public void chooserIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    fileUri = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAMERA_REQUEST);

                }
            }

        }).create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            fileUri = data.getData();
            Picasso.get().load(fileUri).into(imageView);
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), fileUri);
                Bitmap image = utility.getResizedBitmap(thumbnail, 1417, 1024);
                fileUri =  utility.getImageUri(getContext(), image);
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
                    + "." + utility.getFileExtension(fileUri, getContext()));

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

                            Toast.makeText(getContext(), "File caricato", Toast.LENGTH_SHORT).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(! urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            String nome = spinner_NomeFile.getSelectedItem().toString().trim();
                            Date currentTime = Calendar.getInstance().getTime();
                            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String strDate = dateFormat.format(currentTime);
                            String fileName = nome + " (" + strDate + ")";
                            ModelFile modelFile = new ModelFile(fileName, downloadUrl.toString());
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(modelFile);
                            writeOnDatabaseNotifiche(userID, email, strDate);
                            sendNotifications(token, "Nuovo file da " + email, fileName );
                            sendNotifications(tokenProva, "Nuovo file da " + email, fileName );

                            //una volta caricato il file chiudiamo questa activity e passiamo in quella per visualizzare tutti i file
                            DownloadFileFragment downloadFileFragment = new DownloadFileFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment, downloadFileFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Caricamento fallito", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getContext(), "Nessun file selezionato", Toast.LENGTH_SHORT).show();
        }
    }


    //Metodo per sapere se i permessi sono stati dati
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult){
        if(requestCode == ALL_PERMISSIONS_RESULT)
        {
            for(String perm : permissionsToRequest)//per ogni permesso in permissionsToRequest
            {
                if(!(getContext().checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) //se non è stato dato
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

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(getContext(), "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}

