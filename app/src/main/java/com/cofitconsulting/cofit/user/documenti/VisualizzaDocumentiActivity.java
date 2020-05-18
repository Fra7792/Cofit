package com.cofitconsulting.cofit.user.documenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.admin.VisualizzaDocCliente;
import com.cofitconsulting.cofit.utility.CustomAdapterImage;
import com.cofitconsulting.cofit.utility.StrutturaUpload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class VisualizzaDocumentiActivity extends AppCompatActivity implements CustomAdapterImage.OnItemClickListener {

    private ImageButton btnBack;
    private RecyclerView mRecyclerView;
    private CustomAdapterImage mAdapter;
    private ProgressBar mProgressBar;
    private String userID;

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener mDbListener;
    private List<StrutturaUpload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_doc);

       userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnBack = findViewById(R.id.btnBack);
        mProgressBar = findViewById(R.id.progress_circle);
        mRecyclerView = findViewById(R.id.recyclerviewImage);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mAdapter = new CustomAdapterImage(VisualizzaDocumentiActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(VisualizzaDocumentiActivity.this);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(userID);

        mDbListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    StrutturaUpload upload = postSnapshot.getValue(StrutturaUpload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VisualizzaDocumentiActivity.this, "Errore", Toast.LENGTH_SHORT).show();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onDownloadClick(int position) {
        StrutturaUpload selectedItem = mUploads.get(position);
        downloadFiles(VisualizzaDocumentiActivity.this, selectedItem.getFileName(), DIRECTORY_DOWNLOADS, selectedItem.getFileUrl());
        Toast.makeText(VisualizzaDocumentiActivity.this, "Download in corso...",Toast.LENGTH_SHORT).show();

    }
    private void downloadFiles(Context context, String fileName, String destinatonDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinatonDirectory, fileName);

        downloadManager.enqueue(request);

    }

    @Override
    public void onDeleteClick(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VisualizzaDocumentiActivity.this);
        builder.setMessage("Vuoi eliminare? ")
                .setCancelable(false)
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StrutturaUpload selectedItem = mUploads.get(position);
                        final String selectedKey = selectedItem.getKey();
                        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(selectedItem.getFileUrl());
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(selectedKey).removeValue();
                                Toast.makeText(VisualizzaDocumentiActivity.this, "Elemento rimosso", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDbListener);
    }

}
