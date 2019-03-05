package su.plugin.glogin.common.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.Getter;
import su.plugin.glogin.common.api.manager.AccountManager;

public class GLoginAPI {
	
	@Getter
	protected static AccountManager accountManager;
	@Getter
	protected static su.plugin.glogin.common.api.manager.SQLManager SQLManager;
	
	public String getSHA256(String msg) {
		MessageDigest sha256;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
			sha256.reset(); 
			sha256.update(msg.getBytes()); 
			byte[] b = sha256.digest();
			StringBuffer sb = new StringBuffer();
			for(int i : b) {
				sb.append(Integer.toString((i&0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
}