package com.cofitconsulting.cofit.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.cofitconsulting.cofit.R;
import com.cofitconsulting.cofit.utility.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



public class ListaClienti extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clienti);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mRecyclerView = findViewById(R.id.recyclerview_users);


        Query query = firebaseFirestore.collection("Users");
        final FirestoreRecyclerOptions <User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_item_lista_clienti, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.text_denominazione.setText(model.getDenominazione());
                holder.text_email.setText(model.getEmail());
                holder.text_id.setText(model.getId()+ " ");
            }
        };

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    private class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView text_denominazione;
        private TextView text_email;
        private TextView text_id;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            text_denominazione = itemView.findViewById(R.id.txt_den);
            text_email = itemView.findViewById(R.id.txt_email);
            text_id = itemView.findViewById(R.id.txt_id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ID = text_id.getText().toString();
                    Intent intent = new Intent(ListaClienti.this, MenuUserActivity.class);
                    intent.putExtra("User_ID", ID);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void showPopup(View V){
        PopupMenu popup = new PopupMenu(this, V);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_action_client);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemTasse:
                Toast.makeText(this, "Vuoi inserire le tasse", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemDocumenti:
                Toast.makeText(this, "Vuoi inserire i documenti", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemAnagrafica:
             /*   Intent intent = new Intent(ListaClienti.this, AnagraficaCliente.class);
               intent.putExtra("User_ID", ID);
                startActivity(intent);
                Toast.makeText(this, "Vuoi visualizzare l'anagrafica", Toast.LENGTH_SHORT).show();*/
                return true;
            default:
                return false;
        }
    }
}

