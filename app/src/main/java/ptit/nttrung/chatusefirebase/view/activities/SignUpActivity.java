package ptit.nttrung.chatusefirebase.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.base.BaseActivity;
import ptit.nttrung.chatusefirebase.listener.RegisterListener;
import ptit.nttrung.chatusefirebase.service.ResgisterService;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.input_email)
    EditText inputEmail;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.input_reEnterPassword)
    EditText inputReEnterPassword;
    @BindView(R.id.btn_signup)
    AppCompatButton btnSignup;
    @BindView(R.id.link_login)
    TextView linkLogin;

    private ResgisterService registerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        registerService = new ResgisterService(this);
    }

    @OnClick({R.id.btn_signup, R.id.link_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:
                signup();
                break;
            case R.id.link_login:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            return;
        }

        showProgressDialog("Creating Account...");
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String reEnterPassword = inputReEnterPassword.getText().toString();

        registerService.registerAccount(email, password, name, new RegisterListener() {
            @Override
            public void registerSuccess() {
                hideProgressDialog();
                setResult(RESULT_OK, null);
                Toast.makeText(SignUpActivity.this, "Sign Up Succesfull", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void registerComplete() {
                hideProgressDialog();
                Toast.makeText(SignUpActivity.this, "Resgiter fail. Email exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void registerFailure(String message) {
                hideProgressDialog();
                Log.e(TAG, message);
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String reEnterPassword = inputReEnterPassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            inputName.setError("at least 3 characters");
            valid = false;
        } else {
            inputName.setError(null);
        }

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

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            inputReEnterPassword.setError("Password Do not match");
            valid = false;
        } else {
            inputReEnterPassword.setError(null);
        }

        return valid;
    }
}
