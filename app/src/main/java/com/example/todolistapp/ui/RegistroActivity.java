package com.example.todolistapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolistapp.R;
import com.example.todolistapp.data.SessionManager;
import com.example.todolistapp.data.UsuarioRepository;
import com.example.todolistapp.model.Usuario;
import com.google.android.material.button.MaterialButton;

public class RegistroActivity extends AppCompatActivity {

    private UsuarioRepository usuarios;
    private SessionManager sesion;

    private EditText etNombre;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvErrorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuarios = UsuarioRepository.getInstance(this);
        sesion = new SessionManager(this);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        MaterialButton btnCrear = findViewById(R.id.btnCrear);
        TextView tvEntra = findViewById(R.id.tvEntra);

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarRegistro();
            }
        });

        tvEntra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Apenas toca el email, el error desaparece.
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                ocultarError();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario u = usuarios.registrar(email, password, nombre);
        if (u == null) {
            mostrarError();
            return;
        }

        sesion.iniciarSesion(u.getId());

        Intent i = new Intent(this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void mostrarError() {
        etEmail.setBackgroundResource(R.drawable.bg_input_error);
        tvErrorEmail.setVisibility(View.VISIBLE);
    }

    private void ocultarError() {
        if (tvErrorEmail.getVisibility() == View.VISIBLE) {
            etEmail.setBackgroundResource(R.drawable.bg_input);
            tvErrorEmail.setVisibility(View.GONE);
        }
    }
}