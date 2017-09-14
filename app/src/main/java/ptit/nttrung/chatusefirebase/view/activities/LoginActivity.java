package ptit.nttrung.chatusefirebase.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.base.BaseActivity;
import ptit.nttrung.chatusefirebase.data.define.StaticConfig;
import ptit.nttrung.chatusefirebase.listener.LoginListener;
import ptit.nttrung.chatusefirebase.service.LoginService;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_email)
    EditText inputEmail;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;
    @BindView(R.id.link_signup)
    TextView linkSignup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginService loginService;
    private boolean firstTimeAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        mAuth = FirebaseAuth.getInstance();
        firstTimeAccess = true;
        loginService = new LoginService(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userFB = firebaseAuth.getCurrentUser();
                if (userFB != null) {
                    // User is signed in
                    StaticConfig.UID = userFB.getUid();
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + userFB.getUid());
                    if (firstTimeAccess) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    } else {
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    firstTimeAccess = false;
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @OnClick({R.id.btn_login, R.id.link_signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.link_signup:
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
//                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    public void login() {
        if (!validate()) {
            return;
        }

        showProgressDialog("Authenticating...");
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        loginService.loginAccountEmail(email, password, new LoginListener() {
            @Override
            public void loginSuccess() {
                hideProgressDialog();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void loginComplete() {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, getString(R.string.wrong_email_or_pass), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void loginFailure(String message) {
                hideProgressDialog();
                Log.e(TAG, message);
            }
        });
    }

    /**
     * Kiểm tra dữ liệu
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }
}
