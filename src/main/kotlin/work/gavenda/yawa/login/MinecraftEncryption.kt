/*
 * Yawa - All in one plugin for my personally deployed Vanilla SMP servers
 *
 * Copyright (c) 2022 Gavenda <gavenda@disroot.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package work.gavenda.yawa.login

import java.math.BigInteger
import java.security.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Utilities related to Minecraft Encryption.
 */
object MinecraftEncryption {

    private const val VERIFY_TOKEN_LENGTH = 4
    private const val KEY_PAIR_ALGORITHM = "RSA"
    private val SECURE_RANDOM = SecureRandom()

    /**
     * Generate an RSA key pair with a `1024` length.
     */
    fun generateKeyPair(keySize: Int = 1024): KeyPair =
        KeyPairGenerator.getInstance(KEY_PAIR_ALGORITHM).apply {
            initialize(keySize)
        }.generateKeyPair()

    /**
     * Generate a random token.
     *
     * This is used to verify that we are communicating with the same player in a login session.
     *
     * @return a verify token with 4 bytes long
     */
    fun generateVerifyToken(): ByteArray {
        val bytes = ByteArray(VERIFY_TOKEN_LENGTH)
        SECURE_RANDOM.nextBytes(bytes)
        return bytes
    }

    /**
     * Generate the server id based on client and server data.
     *
     * @param sessionId session for the current login attempt
     * @param sharedSecret shared secret between the client and the server
     * @param publicKey public key of the server
     * @return the server id formatted as a hexadecimal string.
     */
    fun generateServerIdHash(sessionId: String, sharedSecret: SecretKey, publicKey: PublicKey): String {
        val digest = MessageDigest.getInstance("SHA-1").apply {
            update(sessionId.toByteArray(Charsets.ISO_8859_1))
            update(sharedSecret.encoded)
            update(publicKey.encoded)
        }
        val serverHash = digest.digest()
        return BigInteger(serverHash).toString(16)
    }

    /**
     * Decrypts the content and extracts the shared key.
     *
     * @param privateKey the private key
     * @param sharedKey the encrypted shared key
     * @return shared secret key
     * @throws GeneralSecurityException if it fails to decrypt the data
     */
    fun decryptSharedKey(privateKey: PrivateKey, sharedKey: ByteArray): SecretKey =
        SecretKeySpec(decrypt(privateKey, sharedKey), "AES")

    /**
     * Decrypt the given data using the given private key.
     *
     * @param key the private key
     * @param data the encrypted data
     * @return clear text data
     * @throws GeneralSecurityException if it fails to decrypt the data
     */
    fun decrypt(key: PrivateKey, data: ByteArray): ByteArray =
        Cipher.getInstance(key.algorithm).apply {
            init(Cipher.DECRYPT_MODE, key)
        }.doFinal(data)

    /**
     * Returns the given key as a cipher.
     * @param mode cipher mode
     * @param key secret key
     */
    fun asCipher(mode: Int, key: SecretKey): Cipher = Cipher.getInstance("AES/CFB8/NoPadding").apply {
        init(mode, key, IvParameterSpec(key.encoded))
    }
}