package ml.game.android.SaveNewton;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public final class SecueUtil {
	public static String getRandomEncryptKey(){
		SecretKey key;
		try {
			key = KeyGenerator.getInstance("DES").generateKey();
			return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
		} catch (NoSuchAlgorithmException e) {}
		return null;
	}
	
	public static String encryptData(String input, String keyBase64){
		SecretKeySpec key = new SecretKeySpec(Base64.decode(keyBase64, Base64.DEFAULT), "DES");
		Cipher ecipher = null;
		try {
			ecipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] inputBytes = input.getBytes("UTF8");
			return Base64.encodeToString(ecipher.doFinal(inputBytes), Base64.DEFAULT);
		} catch (Exception e) {}
		return null;
	}
	
	public static String decryptData(String encryptBase64Data, String keyBase64){
		SecretKeySpec key = new SecretKeySpec(Base64.decode(keyBase64, Base64.DEFAULT), "DES");
		Cipher decipher = null;
		try {
			decipher = Cipher.getInstance("DES");
			decipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encryptData = Base64.decode(encryptBase64Data, Base64.DEFAULT);
			return new String(decipher.doFinal(encryptData), "UTF8");
		} catch (Exception e) {}
		return null;
	}
}
