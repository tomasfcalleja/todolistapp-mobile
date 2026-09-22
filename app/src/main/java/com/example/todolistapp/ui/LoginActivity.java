package com.example.todolistapp.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.todolistapp.R;
import com.example.todolistapp.data.CategoriaRepository;
import com.example.todolistapp.data.SessionManager;
import com.example.todolistapp.data.TareaRepository;
import com.example.todolistapp.data.UsuarioRepository;
import com.example.todolistapp.model.Categoria;
import com.example.todolistapp.model.Tarea;
import com.example.todolistapp.model.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;

public class LoginActivity extends AppCompatActivity {

    private SessionManager sesion;
    private UsuarioRepository usuarios;

    private EditText etEmail;
    private EditText etPassword;


    private static final float ESCALA_INICIAL = 1.6f;
    private static final long DURACION_CARGA = 1400;

    private ImageView ivIlustracion;
    private View[] contenido;
    private AnimatorSet pulso;

    private MaterialButton btnEntrar;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splash = SplashScreen.installSplashScreen(this); // ANTES de super
        super.onCreate(savedInstanceState);

        // Sacar el splash del sistema al instante (es del mismo color, no se nota)
        splash.setOnExitAnimationListener(vista -> vista.remove());

        seed();   // <-- provisorio, borrar antes de entregar

        sesion = new SessionManager(this);
        if (sesion.haySesion()) {
            irAlHome();
            return;
        }

        setContentView(R.layout.activity_login);

        usuarios = UsuarioRepository.getInstance(this);

        ivIlustracion = findViewById(R.id.ivIlustracion);
        contenido = new View[]{
                findViewById(R.id.tvBienvenido),
                findViewById(R.id.tvSubtitulo),
                findViewById(R.id.etEmail),
                findViewById(R.id.etPassword),
                findViewById(R.id.btnEntrar),
                findViewById(R.id.layoutRegistro)
        };

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvError = findViewById(R.id.tvError);

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                intentarLogin();
                return true;
            }
            return false;
        });
        TextView tvRegistrate = findViewById(R.id.tvRegistrate);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarLogin();
            }
        });

        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });

        if (savedInstanceState == null) {
            prepararIntro(); // solo al abrir la app, no al rotar
        }
    }

    /** Datos de prueba. Se crean una sola vez. */
    private void seed() {
        UsuarioRepository usuarios = UsuarioRepository.getInstance(this);
        if (usuarios.findByEmail("test@test.com") != null) {
            return;
        }

        Usuario u = usuarios.registrar("test@test.com", "1234", "Tomas");

        CategoriaRepository categorias = CategoriaRepository.getInstance(this);
        Categoria facultad = categorias.crear(u.getId(), "Facultad");
        Categoria casa = categorias.crear(u.getId(), "Casa");
        Categoria compras = categorias.crear(u.getId(), "Compras");

        TareaRepository tareas = TareaRepository.getInstance(this);
        tareas.create(new Tarea(u.getId(), facultad.getId(), "Terminar el TP de móviles"));
        tareas.create(new Tarea(u.getId(), facultad.getId(), "Estudiar para el parcial"));
        tareas.create(new Tarea(u.getId(), casa.getId(), "Cambiar la lamparita del pasillo"));
        tareas.create(new Tarea(u.getId(), compras.getId(), "Comprar café"));
        tareas.create(new Tarea(u.getId(), null, "Tarea sin categoría"));

        Tarea hecha = tareas.create(new Tarea(u.getId(), casa.getId(), "Sacar la basura"));
        hecha.setCompletada(true);
        tareas.update(hecha);
    }

    private static final long DURACION_LOADER = 550;

    private void intentarLogin() {
        ocultarTeclado();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            mostrarError(getString(R.string.error_campos_vacios));
            return;
        }

        ocultarError();
        mostrarCargando(true);

        btnEntrar.postDelayed(() -> {
            Usuario u = usuarios.autenticar(email, password);
            if (u == null) {
                mostrarCargando(false);
                mostrarError(getString(R.string.error_credenciales));
                return;
            }
            sesion.iniciarSesion(u.getId());
            salirHaciaHome();
        }, DURACION_LOADER);
    }

    private void mostrarCargando(boolean cargando) {
        if (cargando) {
            float d = getResources().getDisplayMetrics().density;
            CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(this, null);
            spec.indicatorColors = new int[]{btnEntrar.getCurrentTextColor()};
            spec.indicatorSize = (int) (18 * d);
            spec.trackThickness = (int) (2 * d);

            btnEntrar.setText(null);
            btnEntrar.setIcon(IndeterminateDrawable.createCircularDrawable(this, spec));
            btnEntrar.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            btnEntrar.setIconPadding(0);
        } else {
            btnEntrar.setIcon(null);
            btnEntrar.setText(R.string.login_entrar);
        }
        // clickable y no enabled: enabled=false pondría el botón gris
        btnEntrar.setClickable(!cargando);
        etEmail.setEnabled(!cargando);
        etPassword.setEnabled(!cargando);
    }

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.animate().alpha(1f).setDuration(200).start();
        sacudir(etEmail, etPassword, tvError);
    }

    private void ocultarError() {
        tvError.animate().alpha(0f).setDuration(150).start();
    }

    private void sacudir(View... vistas) {
        float d = getResources().getDisplayMetrics().density;
        for (View v : vistas) {
            ObjectAnimator shake = ObjectAnimator.ofFloat(v, View.TRANSLATION_X,
                    0, -10 * d, 10 * d, -7 * d, 7 * d, -3 * d, 3 * d, 0);
            shake.setDuration(420);
            shake.start();
        }
    }

    private void salirHaciaHome() {
        // Todo sale hacia arriba en cascada, en orden inverso a como entró
        float d = getResources().getDisplayMetrics().density;
        View[] todo = new View[contenido.length + 1];
        todo[0] = ivIlustracion;
        System.arraycopy(contenido, 0, todo, 1, contenido.length);

        for (int i = 0; i < todo.length; i++) {
            View v = todo[todo.length - 1 - i];
            v.animate()
                    .alpha(0f)
                    .translationY(-20 * d)
                    .setStartDelay(i * 35L)
                    .setDuration(220)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }

        long total = (todo.length - 1) * 35L + 220;
        btnEntrar.postDelayed(() -> {
            Bundle anim = ActivityOptions.makeCustomAnimation(
                    this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            startActivity(new Intent(this, HomeActivity.class), anim);
            finish();
        }, total);
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View foco = getCurrentFocus();
        if (imm != null && foco != null) {
            imm.hideSoftInputFromWindow(foco.getWindowToken(), 0);
        }
    }

    private void irAlHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void prepararIntro() {
        float desplazamiento = 24 * getResources().getDisplayMetrics().density;
        for (View v : contenido) {
            v.setAlpha(0f);
            v.setTranslationY(desplazamiento);
            v.setEnabled(false);
        }

        // Esperamos a que el layout esté medido para saber dónde está la imagen
        ivIlustracion.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ivIlustracion.getViewTreeObserver().removeOnPreDrawListener(this);

                        View raiz = findViewById(android.R.id.content);
                        int[] posRaiz = new int[2];
                        int[] posImg = new int[2];
                        raiz.getLocationInWindow(posRaiz);
                        ivIlustracion.getLocationInWindow(posImg);

                        float centroRaizX = posRaiz[0] + raiz.getWidth() / 2f;
                        float centroRaizY = posRaiz[1] + raiz.getHeight() / 2f;
                        float centroImgX = posImg[0] + ivIlustracion.getWidth() / 2f;
                        float centroImgY = posImg[1] + ivIlustracion.getHeight() / 2f;

                        // La movemos al centro de la pantalla y la agrandamos
                        ivIlustracion.setTranslationX(centroRaizX - centroImgX);
                        ivIlustracion.setTranslationY(centroRaizY - centroImgY);
                        ivIlustracion.setScaleX(ESCALA_INICIAL);
                        ivIlustracion.setScaleY(ESCALA_INICIAL);

                        iniciarPulso();
                        ivIlustracion.postDelayed(() -> terminarCarga(), DURACION_CARGA);
                        return true;
                    }
                });
    }

    private void iniciarPulso() {
        float max = ESCALA_INICIAL * 1.08f;
        ObjectAnimator x = ObjectAnimator.ofFloat(ivIlustracion, View.SCALE_X, ESCALA_INICIAL, max);
        ObjectAnimator y = ObjectAnimator.ofFloat(ivIlustracion, View.SCALE_Y, ESCALA_INICIAL, max);
        for (ObjectAnimator a : new ObjectAnimator[]{x, y}) {
            a.setRepeatCount(ValueAnimator.INFINITE);
            a.setRepeatMode(ValueAnimator.REVERSE);
        }
        pulso = new AnimatorSet();
        pulso.playTogether(x, y);
        pulso.setDuration(600);
        pulso.setInterpolator(new AccelerateDecelerateInterpolator());
        pulso.start();
    }

    private void terminarCarga() {
        if (pulso != null) pulso.cancel();

        // La imagen "se acomoda" en su lugar con un leve rebote
        ivIlustracion.animate()
                .translationX(0f).translationY(0f)
                .scaleX(1f).scaleY(1f)
                .setDuration(750)
                .setInterpolator(new OvershootInterpolator(1.1f))
                .start();

        // El resto entra escalonado, mientras la imagen todavía se está moviendo
        for (int i = 0; i < contenido.length; i++) {
            View v = contenido[i];
            v.animate()
                    .alpha(1f).translationY(0f)
                    .setStartDelay(400 + i * 70L)
                    .setDuration(450)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> v.setEnabled(true))
                    .start();
        }
    }

    @Override
    protected void onDestroy() {
        if (pulso != null) pulso.cancel();
        super.onDestroy();
    }
}