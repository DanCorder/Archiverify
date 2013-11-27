package com.dancorder.PhotoSync;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class HashGenerator {
	public byte[] calculateMd5(InputStream data) throws NoSuchAlgorithmException, IOException {
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		DigestInputStream digestStream = new DigestInputStream(data, md5Digest);
		int readReturnValue;
		byte[] dummy = new byte[1024];
		do {
			readReturnValue = digestStream.read(dummy);
		} while (readReturnValue != -1);
		
		return md5Digest.digest();
	}
}
