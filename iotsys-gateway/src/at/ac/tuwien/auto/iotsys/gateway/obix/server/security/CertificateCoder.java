package at.ac.tuwien.auto.iotsys.gateway.obix.server.security;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

/**
 * Certificate utilities
 * 
 * @author Luyu ZHOU
 * 
 */
public class CertificateCoder {

	public static final String CERT_TYPE = "X.509";

	/**
	 * Get private key from keyStore
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * 
	 * @return PrivateKey
	 * @throws Exception
	 */
	private static PrivateKey getPrivateKeyByKeyStore(String keyStorePath,
			String alias, String password) throws Exception {

		KeyStore ks = getKeyStore(keyStorePath, password);

		return (PrivateKey) ks.getKey(alias, password.toCharArray());
	}

	/**
	 * Get public key from certificate
	 * 
	 * @param certificatePath
	 * 
	 * @return PublicKey
	 * @throws Exception
	 */
	private static PublicKey getPublicKeyByCertificate(String certificatePath)
			throws Exception {

		Certificate certificate = getCertificate(certificatePath);

		return certificate.getPublicKey();
	}

	/**
	 * Get certificate
	 * 
	 * @param certificatePath
	 * 
	 * @return Certificate
	 * @throws Exception
	 */
	private static Certificate getCertificate(String certificatePath)
			throws Exception {

		CertificateFactory certificateFactory = CertificateFactory
				.getInstance(CERT_TYPE);

		FileInputStream in = new FileInputStream(certificatePath);

		Certificate certificate = certificateFactory.generateCertificate(in);

		in.close();
		return certificate;
	}

	/**
	 * Get certificate from keyStore
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * 
	 * @return Certificate
	 * @throws Exception
	 */
	private static Certificate getCertificate(String keyStorePath,
			String alias, String password) throws Exception {

		KeyStore ks = getKeyStore(keyStorePath, password);

		return ks.getCertificate(alias);
	}

	/**
	 * Get keyStore
	 * 
	 * @param keyStorePath
	 * @param password
	 * 
	 * @return KeyStore
	 * @throws Exception
	 */
	private static KeyStore getKeyStore(String keyStorePath, String password)
			throws Exception {

		KeyStore ks = KeyStore.getInstance("JKS"); 
		FileInputStream is = new FileInputStream(keyStorePath);

		ks.load(is, password.toCharArray());

		is.close();
		return ks;
	}

	/**
	 * Encrypt with private key
	 * 
	 * @param data
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * 
	 * @return byte[] Encrypted message
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath,
			String alias, String password) throws Exception {

		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, alias,
				password);

		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * Decrypt with private key
	 * 
	 * @param data
	 * 
	 * @param keyStorePath
	 * @param alias
	 * @param password
	 * 
	 * @return byte[] Decrypted message
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String keyStorePath,
			String alias, String password) throws Exception {

		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, alias,
				password);

		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());

		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * Encrypt with public key
	 * 
	 * @param data
	 * 
	 * @param certificatePath
	 * 
	 * @return byte[] Encrypted message
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String certificatePath)
			throws Exception {

		PublicKey publicKey = getPublicKeyByCertificate(certificatePath);

		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * ï¿¼ï¿¼Decrypted with public key
	 * 
	 * @param data
	 * @param certificatePath
	 * 
	 * @return byte[] Decrypted message
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String certificatePath)
			throws Exception {

		PublicKey publicKey = getPublicKeyByCertificate(certificatePath); 
		
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		
		return cipher.doFinal(data);
	}

	/**
	 * Sign datas
	 * 
	 * @param keyStorePath
	 * @param alias 
	 * @param password

	 * @return byte[] Signature
	 * @throws Exception
	 */
	public static byte[] sign(byte[] sign, String keyStorePath, String alias,
			String password) throws Exception {

		X509Certificate x509Certificate = (X509Certificate) getCertificate(
				keyStorePath, alias, password);

		Signature signature = Signature.getInstance(x509Certificate
				.getSigAlgName()); 
		PrivateKey privateKey = getPrivateKeyByKeyStore(keyStorePath, alias,
				password); 
		
		signature.initSign(privateKey);
		signature.update(sign);
		
		return signature.sign();
	}

	/**
	 * Verify the signature
	 * 
	 * @param data
	 * @param sign
	 * @param certificatePath
	 * 
	 * @return boolean 
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, byte[] sign,
			String certificatePath) throws Exception {

		X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath); 
		Signature signature = Signature.getInstance(x509Certificate
				.getSigAlgName()); 

		signature.update(data);
		
		return signature.verify(sign);
	}
}
