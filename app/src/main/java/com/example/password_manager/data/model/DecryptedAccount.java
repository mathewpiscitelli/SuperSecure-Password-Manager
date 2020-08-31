package com.example.password_manager.data.model;

import com.example.password_manager.Crypto;

public class DecryptedAccount {
    private String serviceName;
    private String username;
    private String password;

    public DecryptedAccount(Account account, String key) {
        String encoded;
        byte[] ciphertextBytes;
        byte[] plaintextBytes;

        // Decrypt account fields
        serviceName = Crypto.decryptAesB64String(account.serviceName, key.getBytes());
        username = Crypto.decryptAesB64String(account.username, key.getBytes());
        password = Crypto.decryptAesB64String(account.password, key.getBytes());
    }

    public String getServiceName() { return serviceName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

