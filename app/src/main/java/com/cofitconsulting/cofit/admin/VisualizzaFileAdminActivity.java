package com.cofitconsulting.cofit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.Utility;
import com.cofitconsulting.cofit.utility.adaptereviewholder.CustomAdapterDoc;
import com.cofitconsulting.cofit.utility.model.ModelFile;

import com.google.android.gms.tasks.OnSuccessListener;
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

public class VisualizzaFileAdminActivity extends AppCompatActivity implements CustomAdapterDoc.OnItemClickListener{

    private ImageButton btnBack;
    private EditText inputSearch;
    private RecyclerView mRecyclerView;
    private CustomAdapterDoc mAdapter;
    private ProgressBar mProgressBar;
    private String userID;
    private Utility utility = new Utility();

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener mDbListener;
    private List<ModelFile> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_file);

        Intent intent = getIntent();
        userID = intent.getStringExtra("User_ID").trim();

        inputSearch = findViewById(R.id.inputSearch);
        btnBack = findViewById(R.id.btnBack);
        mProgressBar = findViewById(R.id.progress_circle);
        mRecyclerView = findViewById(R.id.recyclerviewImage);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();
        mAdapter = new CustomAdapterDoc(VisualizzaFileAdminActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(VisualizzaFileAdminActivity.this);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(userID);



        //raccolgo tutte le informazioni richieste dal database di firebase
        mDbListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    ModelFile upload = postSnapshot.getValue(ModelFile.class);
                    upload.setKey(postSnapshot.getKey());

                        mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VisualizzaFileAdminActivity.this, "Errore, impossibile caricare i file", Toast.LENGTH_SHORT).show();

            }
        });

        //cerco i file nel database in base al nome
       inputSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            mDbListener = databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mUploads.clear();
                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                    {
                                        ModelFile upload = postSnapshot.getValue(ModelFile.class);
                                        upload.setKey(postSnapshot.getKey());
                                        String fullname = upload.getFileName().toLowerCase();
                                        String search = inputSearch.getText().toString().toLowerCase();
                                        if(fullname.contains(search))
                                        {
                                            mUploads.add(upload);
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(VisualizzaFileAdminActivity.this, "Errore", Toast.LENGTH_SHORT).show();

                                }
                            });

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    //metodo per scaricare il file in base alla posizione in cui si trova il file nella recyclerview
    @Override
    public void onDownloadClick(int position) {
        ModelFile selectedItem = mUploads.get(position);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedItem.getFileUrl());
        utility.downloadFiles(VisualizzaFileAdminActivity.this, selectedItem.getFileName(), fileExtension, DIRECTORY_DOWNLOADS, selectedItem.getFileUrl());
        Toast.makeText(VisualizzaFileAdminActivity.this, "Download in corso...",Toast.LENGTH_SHORT).show();
    }

    ////metodo per eliminare il file in base alla posizione in cui si trova il file nella recyclerview
    @Override
    public void onDeleteClick(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(VisualizzaFileAdminActivity.this);
        builder.setMessage("Vuoi eliminare? ")
                .setCancelable(false)
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ModelFile selectedItem = mUploads.get(position);
                        final String selectedKey = selectedItem.getKey();
                        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(selectedItem.getFileUrl());
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(selectedKey).removeValue();
                                Toast.makeText(VisualizzaFileAdminActivity.this, "Elemento rimosso", Toast.LENGTH_SHORT).show();
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
