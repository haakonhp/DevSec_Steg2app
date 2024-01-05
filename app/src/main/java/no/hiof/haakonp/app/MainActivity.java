package no.hiof.haakonp.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements LoginTask.LoginTaskResponse {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
    private String email;
    private String password;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.comment_field);
        passwordEditText = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            String $email = emailEditText.getText().toString().trim();
            String $password = passwordEditText.getText().toString().trim();
            LoginTask.createLoginTask(MainActivity.this, MainActivity.this).execute($email, $password);
        });
    }

    @Override
    public void processLoginFinish(String token) {
        if (!TextUtils.isEmpty(token)) {
            // Store the token for future use
            SharedPreferences preferences = getSharedPreferences("UUID", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("token", token.substring(1, token.length() - 1));
            editor.apply();

            // Navigate to the next screen
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Show an error message to the user
            Toast.makeText(MainActivity.this, "Login failed, please try again!", Toast.LENGTH_LONG).show();
        }
    }

}
