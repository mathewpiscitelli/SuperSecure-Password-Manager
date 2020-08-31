package com.example.password_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.password_manager.data.DatabaseInstance;

import static com.example.password_manager.Crypto.sha256hash;

public class SignUpActivity extends AppCompatActivity {
    private DatabaseInstance db;
    private static final int MIN_PASSWORD_LEN = 3;
    private static final int MIN_USERNAME_LEN = 2;
    private static final String MSG_PASSWORD_LEN = "Password is too short.  Try again.";
    private static final String MSG_USERNAME_LEN = "Username is too short.  Try again.";
    private static final String MSG_PASSWORD_MISMATCH = "Password and confirmation do not match.  Try again.";
    private static final String MSG_USER_CREATED = "User created successfully.";
    private static final String MSG_USER_EXISTS = "User already exists. Try again.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    protected void onResume() {
        // Get DB hook when screen is shown
        super.onResume();
        db = DatabaseInstance.getDbInstance(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        // Make sure changes are saved
        super.onDestroy();
        db.close();
    }

    /* When the user presses the submit button to sign up */
    public void signUp(View view) {
        MainActivity.hideKeyboard(this);

        // Get username, password, and password confirmation strings from text fields
        TextView usernameTextView = findViewById(R.id.editTextSignUpUsername);
        final String username = usernameTextView.getText().toString();
        TextView passwordTextView = findViewById(R.id.editTextSignUpPassword);
        final String password = passwordTextView.getText().toString();
        TextView passwordConfirmTextView = findViewById(R.id.editTextSignUpPasswordConfirm);
        final String passwordConfirm = passwordConfirmTextView.getText().toString();

        // Make sure username is valid
        if (username.length() < MIN_USERNAME_LEN) {
            sendToast(MSG_USERNAME_LEN);
            return;
        }
        // Make sure password is valid
        if (password.length() < MIN_PASSWORD_LEN) {
            sendToast(MSG_PASSWORD_LEN);
            return;
        }
        // Make sure password and confirmation are the same
        if (!password.equals(passwordConfirm)) {
            sendToast(MSG_PASSWORD_MISMATCH);
            return;
        }

        // Hash the password twice, this is stored in the DB
        final String password_sha256_2x = sha256hash(sha256hash(password));

        // Create the user
        new SignUpAsyncTask().execute(username, password_sha256_2x);
    }

    /* Performs DB operation to create a user */
    private class SignUpAsyncTask extends AsyncTask<String, Void, String> {
        // Perform db operation in the background
        protected String doInBackground(String... params) {
            String message = "";
            db = DatabaseInstance.getDbInstance(getApplicationContext());
            try {
                // Send credentials to database, display a success toast if successful
                db.createUser(params[0], params[1]);
            } catch (Exception ex) {
                // Display a toast error message if user couldn't be created
                message = ex.getMessage();
            }
            return message;
        }
        protected void onPostExecute(String message) {
            if (message.isEmpty()) {
                // Success
                sendToast(SignUpActivity.MSG_USER_CREATED);
                onBackPressed();
            } else {
                // User creation failed
                sendToast(message);
            }
        }
    }

    /* Send a message as a toast notification */
    private void sendToast(String message) {
        // Send an error message as a toast
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 64);
        toast.show();
    }
}