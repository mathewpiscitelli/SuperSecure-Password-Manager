package com.example.password_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.password_manager.data.DatabaseInstance;
import com.example.password_manager.data.model.Account;
import com.example.password_manager.data.model.DecryptedAccount;

import java.util.List;

public class AccountActivity extends AppCompatActivity {
    private static String key = "";
    private static int userId;
    private DatabaseInstance db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        // Get the Intent that started the app to get the passed parameters
        Intent intent = getIntent();

        // SHA-256 of password will be used for decryption
        if (intent.hasExtra(MainActivity.KEY)) {
            key = intent.getStringExtra(MainActivity.KEY);
        }

        // User for accounts
        if (intent.hasExtra(MainActivity.USER_ID)) {
            userId = intent.getIntExtra(MainActivity.USER_ID, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = DatabaseInstance.getDbInstance(getApplicationContext());
        populateAccountCards();
    }

    @Override
    protected void onDestroy() {
        // Make sure changes are saved
        super.onDestroy();
        db.close();
    }

    private void populateAccountCards() {
        // Get all accounts in DB, then send to function to populate in view
        db = DatabaseInstance.getDbInstance(getApplicationContext());
        LiveData<List<Account>> accounts = db.getAccountsByUser(userId);
        accounts.observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> accounts) {
                populateAccountCardsNow(accounts);
            }
        });
    }

    private void populateAccountCardsNow(List<Account> accounts) {
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.accountLinearLayout);
        linearLayout.removeAllViews();

        if (accounts.size() == 0) {
            // If 0 accounts, display TextView
            TextView noAccountsText = new TextView(this);
            noAccountsText.setText(getString(R.string.no_accounts_found));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(16,16,16,16);
            noAccountsText.setLayoutParams(params);
            noAccountsText.setTextSize(18);
            noAccountsText.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.addView(noAccountsText);
        }
        else {
            // Else, populate with cards
            for (int i=0; i<accounts.size(); i++) {
                // Create card from XML file
                CardView card = createAccountCard(accounts.get(i), linearLayout);
                // Add card to list
                linearLayout.addView(card);
            }

        }
    }

    private CardView createAccountCard(Account account, LinearLayout parent) {
        // Create card from custom XML file
        LayoutInflater inflater = LayoutInflater.from(this);
        CardView card = (CardView) inflater.inflate(R.layout.account_card, parent, false);

        // Decrypt account info
        if (account == null) return card;
        DecryptedAccount decryptedAccount = new DecryptedAccount(account, key);

        // Add attributes to the card from the DB Account
        TextView serviceName = card.findViewById(R.id.textViewAccountService);
        serviceName.setText(decryptedAccount.getServiceName());

        TextView accountUsername = card.findViewById(R.id.textViewAccountUsername);
        accountUsername.setText(accountUsername.getText() + "  " + decryptedAccount.getUsername());

        TextView accountPassword = card.findViewById(R.id.textViewAccountPassword);
        accountPassword.setText(accountPassword.getText() + "  " + decryptedAccount.getPassword());

        Button deleteButton = card.findViewById(R.id.deleteAccountButton);
        deleteButton.setId(account.id);

        return card;
    }

    public void launchCreateAccountPage(View view) {
        // Bring up the create account screen
        Intent intent = new Intent(this, CreateAccountActivity.class);
        // Send the password hash to the new activity
        intent.putExtra(MainActivity.KEY, key);
        intent.putExtra(MainActivity.USER_ID, userId);
        startActivity(intent);
    }

    public void deleteAccount(View view) {
        // Delete chosen account (button id is account id in database)
        int id = view.getId();

        // Delete account and refresh cards
        new DeleteAccountAsyncTask().execute(id);
    }

    private class DeleteAccountAsyncTask extends AsyncTask<Integer, Void, Void> {
        protected Void doInBackground(Integer... params) {
            db = DatabaseInstance.getDbInstance(getApplicationContext());
            db.deleteAccount(params[0]);
            return null;
        }
        protected void onPostExecute(Void param) {
            populateAccountCards();
        }
    }
}



