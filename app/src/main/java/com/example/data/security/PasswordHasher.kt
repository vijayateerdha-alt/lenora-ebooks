package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {

    private val secureRandom = SecureRandom()

    /**
     * Generates a cryptographically secure 16-byte random salt, Base64-encoded.
     */
    fun generateSalt(): String {
        val saltBytes = ByteArray(16)
        secureRandom.nextBytes(saltBytes)
        return Base64.getEncoder().encodeToString(saltBytes)
    }

    /**
     * Hashes a password using SHA-256 with multi-round salting.
     * Passwords are NEVER stored in plain text.
     */
    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = "$salt:$password:$salt".toByteArray(Charsets.UTF_8)
        var hash = digest.digest(combined)
        
        // 1,000 rounds of key stretching
        for (i in 1 until 1000) {
            digest.reset()
            hash = digest.digest(hash)
        }
        
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Securely verifies a plain-text password against stored hash and salt.
     */
    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computedHash = hashPassword(password, salt)
        return MessageDigest.isEqual(
            computedHash.toByteArray(Charsets.UTF_8),
            expectedHash.toByteArray(Charsets.UTF_8)
        )
    }
}
