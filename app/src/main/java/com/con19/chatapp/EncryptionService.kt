package com.con19.chatapp

import java.math.BigInteger
import java.util.*

/**
 * Used to transform String messages into encrypted ByteArrays using the RSA algorithm.
 */
object EncryptionService {

    private const val bitLength = 1024
    private val r = Random()

    /**
     * Generates a new public key and private key.
     * @returns Pair<PrivateKey, PublicKey>
     */
    fun generateKeys(): Pair<PrivateKey, PublicKey> {
        val d: BigInteger
        val p = BigInteger.probablePrime(bitLength, r)
        val q = BigInteger.probablePrime(bitLength, r)
        val n = p.multiply(q)
        val totient = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)) // (p-1)(q-1)

        val e = BigInteger.probablePrime(bitLength / 2, r)

        while (totient.gcd(e) > BigInteger.ONE && e < totient) {
            e.add(BigInteger.ONE)
        }
        d = e.modInverse(totient)

        return Pair(
            PrivateKey(n, d),
            PublicKey(n, e)
        )
    }

    /**
     * Encrypt message.
     * @param message - The String to be encrypted.
     * @param receiverPublicKey - Public key for the intended recipient of this message.
     * @returns The encrypted message as a ByteArray.
     */
    fun encrypt(message: String, receiverPublicKey: PublicKey): ByteArray =
        BigInteger(message.toByteArray())
            .modPow(receiverPublicKey.e, receiverPublicKey.n)
            .toByteArray()


    /**
     * Decrypt message.
     * @param message - The ByteArray to be decrypted.
     * @returns The decrypted message as a String.
     */
    internal fun decrypt(message: ByteArray, privateKey: PrivateKey): String = String(
        BigInteger(message)
            .modPow(privateKey.d, privateKey.n)
            .toByteArray()
    )
}

data class PublicKey(
    val n: BigInteger,
    val e: BigInteger
)

data class PrivateKey(
    val n: BigInteger,
    val d: BigInteger
)