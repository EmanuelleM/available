package com.aprendizagem.manu.estudobancodedados.login;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aprendizagem.manu.estudobancodedados.Constantes;
import com.aprendizagem.manu.estudobancodedados.R;
import com.aprendizagem.manu.estudobancodedados.database.Contract;
import com.aprendizagem.manu.estudobancodedados.viagem.ListaViagemActivity;
import com.aprendizagem.manu.estudobancodedados.viagem.NovaViagemActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistroUsuarioActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    EditText editLogin, editNomeUsuario, editPassword;
    Button btnLoginRegister;
    ProgressDialog progressLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_usuario);

        firebaseAuth = FirebaseAuth.getInstance();

        editNomeUsuario = (EditText) findViewById(R.id.nome_usuario);
        editLogin = (EditText) findViewById(R.id.email);
        editPassword = (EditText) findViewById(R.id.password);
        btnLoginRegister = (Button) findViewById(R.id.email_sign_in_button);
        btnLoginRegister.setText(R.string.criar_conta);

        progressLogin = new ProgressDialog(this);

        btnLoginRegister.setOnClickListener(this);

    }

    private void registerUser() {

        String email = editLogin.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.email_obrigatorio, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.senha_obrigatoria, Toast.LENGTH_LONG).show();
            return;
        }

        progressLogin.setMessage(getString(R.string.registrando_nova_conta));
        progressLogin.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistroUsuarioActivity.this, R.string.conta_criada_sucesso, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), ListaViagemActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegistroUsuarioActivity.this, R.string.falha_criacao_conta, Toast.LENGTH_LONG).show();
                        }
                        progressLogin.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        registerUser();
    }
}