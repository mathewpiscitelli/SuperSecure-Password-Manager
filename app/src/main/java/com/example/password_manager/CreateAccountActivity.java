package com.example.password_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.password_manager.data.DatabaseInstance;

public class CreateAccountActivity extends AppCompatActivity {
    private DatabaseInstance db;
    private static String key;
    private static int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Get the Intent that started the app to get the passed parameters
        Intent intent = getIntent();

        // SHA-256 of password will be used for encryption
        key = intent.getStringExtra(MainActivity.KEY);
        userId = intent.getIntExtra(MainActivity.USER_ID, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = DatabaseInstance.getDbInstance(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        // Make sure changes are saved
        super.onDestroy();
        db.close();
    }

    public void createAccount(View view) {
        // Create account and return to card view
        TextView serviceNameView = findViewById(R.id.createAccountServiceName);
        String serviceName = serviceNameView.getText().toString();
        final String encodedServiceName = Crypto.encryptAesB64String(serviceName, key.getBytes());

        TextView usernameView = findViewById(R.id.createAccountUsername);
        String username = usernameView.getText().toString();
        final String encodedUsername= Crypto.encryptAesB64String(username, key.getBytes());

        TextView passwordView = findViewById(R.id.createAccountPassword);
        String password = passwordView.getText().toString();
        final String encodedPassword = Crypto.encryptAesB64String(password, key.getBytes());

        new CreateAccountAsyncTask().execute(encodedServiceName, encodedUsername, encodedPassword);
    }

    private class CreateAccountAsyncTask extends AsyncTask<String, Void, Void> {
        // Perform db operation in the background
        protected Void doInBackground(String... params) {
            db = DatabaseInstance.getDbInstance(getApplicationContext());
            db.addAccount(userId, params[0], params[1], params[2]);
            return null;
        }
        protected void onPostExecute(Void param) {
            onBackPressed();
        }
    }

}