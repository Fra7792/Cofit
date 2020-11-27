package com.cofitconsulting.cofit.user.documenti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class DownloadFileFragment extends Fragment implements CustomAdapterDoc.OnItemClickListener {

    private ImageButton btnBack;
    private EditText inputSearch;
    private RecyclerView mRecyclerView;
    private CustomAdapterDoc mAdapter;
    private ProgressBar mProgressBar;
    private String userID;

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener mDbListener;
    private List<ModelFile> mUploads;
    private Utility utility = new Utility();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_download_doc, container, false);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        inputSearch = v.findViewById(R.id.inputSearch);
        btnBack = v.findViewById(R.id.btnBack);
        mProgressBar = v.findViewById(R.id.progress_circle);
        mRecyclerView = v.findViewById(R.id.recyclerviewImage);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUploads = new ArrayList<>();
        mAdapter = new CustomAdapterDoc(getContext(), mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(DownloadFileFragment.this);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(userID);

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
                Toast.makeText(getContext(), "Errore", Toast.LENGTH_SHORT).show();

            }
        });

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
                                    Toast.makeText(getContext(), "Errore", Toast.LENGTH_SHORT).show();

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


        return v;
    }

    @Override
    public void onDownloadClick(int position) {
        ModelFile selectedItem = mUploads.get(position);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedItem.getFileUrl());
        utility.downloadFiles(getContext(), selectedItem.getFileName(), fileExtension, DIRECTORY_DOWNLOADS, selectedItem.getFileUrl());
        Toast.makeText(getContext(), "Download in corso...",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDeleteClick(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                Toast.makeText(getContext(), "Elemento rimosso", Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDbListener);
    }

}
