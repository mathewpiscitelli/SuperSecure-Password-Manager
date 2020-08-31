package com.example.password_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.password_manager.data.DatabaseInstance;
import com.example.password_manager.data.model.User;

import java.util.List;

import static com.example.password_manager.Crypto.sha256hash;

public class MainActivity extends AppCompatActivity {
    public static final String KEY = "key";
    public static final String USER_ID = "user_id";
    public static final String INVALID_LOGIN_MESSAGE = "Invalid login.  Try again.";
    private DatabaseInstance db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DatabaseInstance.getDbInstance(getApplicationContext());
    }

    /* When the user presses the Sign In button */
    public void validateLogin(View view) {
        // Hide the keyboard after the user submits
        hideKeyboard(this);

        // Get the entered username
        EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        final String username = editTextUsername.getText().toString();

        // Hash the entered password
        // 1x hash is used as the decryption key so the plaintext password is not in memory for very long
        // 2x hash is stored in the DB for authentication
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        final String password = editTextPassword.getText().toString();
        final String password_sha256 = sha256hash(password);
        final String password_sha256_2x = sha256hash(password_sha256);

        // Compare user-entered password to password in DB
        LiveData<List<User>> users = db.getUser(username, password_sha256_2x);
        users.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                //User user = users.get(0);
                //if (password_sha256_2x.equals(user.passwordHash)) {
                //     launchHomePage(password_sha256);
                if (users.size() > 0) {
                    // User found with matching username and hash, valid login
                    launchHomePage(password_sha256, users.get(0).id);
                } else {
                    // No matching users, invalid login
                    sendInvalidLoginToast();
                }
            }
        });
    }

    /* Launch the new activity if the password is correct */
    private void launchHomePage(String password_sha256, int id) {
        Intent intent = new Intent(this, AccountActivity.class);

        // Send the password hash to the new activity
        // Send the user ID to get the appropriate accounts
        intent.putExtra(KEY, password_sha256);
        intent.putExtra(USER_ID, id);
        startActivity(intent);
    }

    /* Alert the user that their login failed */
    private void sendInvalidLoginToast() {
        // Send an error message as a toast
        Context context = getApplicationContext();
        CharSequence invalidLoginMessage = INVALID_LOGIN_MESSAGE;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, invalidLoginMessage, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 64);
        toast.show();
    }

    /* Hide the keyboard after the user clicks the Sign In button */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (java.lang.NullPointerException ex) {
            // Keyboard is already closed, do nothing
        }
    }

    /* When the user presses the Sign Up button */
    public void signUp(View view) {
        launchSignUpPage();
    }

    /* Launch Sign Up Screen */
    private void launchSignUpPage() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}