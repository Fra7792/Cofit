package com.cofitconsulting.cofit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class RegisterActivity extends AppCompatActivity {

    private EditText  mEmail, mPassword, mConfPass;
    private Button mBtnRegistra;
    private TextView linkLogin, condizioni;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth; //crea un oggetto della classe FirebaseAuth per l'autenticazione

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfPass = findViewById(R.id.confPass);
        mBtnRegistra = findViewById(R.id.btnRegistra);
        linkLogin = findViewById(R.id.btnViewLogin);
        fAuth = FirebaseAuth.getInstance(); //crea un'istanza
        progressBar = findViewById(R.id.progressBar);
        condizioni = findViewById(R.id.condizioni);
        String htmlText = "Registrandoti dichiari di aver preso visione e accetti integralmente i nostri " +
                "<A HREF='https://www.cofitconsulting.com/termini-e-condizioni-duso/'>termini e condizioni</A>. " +
                "\nI tuoi dati personali saranno trattati in conformità con privacy policy.";
        condizioni.setText(Html.fromHtml(htmlText));
        condizioni.setMovementMethod(LinkMovementMethod.getInstance());

        if(fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mBtnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confPass = mConfPass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){   //TextUtils controlla la lunghezza della stringa
                    mEmail.setError("Inserire l'email!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Inserire la password!");
                    return;
                }
                if(TextUtils.isEmpty(confPass)){
                    mConfPass.setError("Inserire nuovamente la password!");
                }
                if(password.length() < 6){
                    mPassword.setError("La password dev'essere almeno di 6 caratteri!");
                    return;
                }
                if(!(password.equals(confPass)))
                {
                    mConfPass.setError("Le password non coincidono!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Registra l'utente in Firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {   //bisogna gestire le attività asincrone
                        if(task.isSuccessful())
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            //Se l'utente è creato correttamente viene visualizzato un toast e si passerà alla main activity
                            Toast.makeText(RegisterActivity.this, "Utente creato correttamente", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else if(!task.isSuccessful())
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                mEmail.setError("E-mail errata!");
                                Toast.makeText(RegisterActivity.this, "L'email è errata!", Toast.LENGTH_LONG).show();

                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                mEmail.setError("E-mail già utilizzata");
                                Toast.makeText(RegisterActivity.this, "Email già utilizzata!", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(RegisterActivity.this, "Errore!" , Toast.LENGTH_LONG).show();
                            }

                        }

                    }
                });
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
