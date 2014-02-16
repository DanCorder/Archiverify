package com.dancorder.PhotoSync;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

class HashGenerator {
	String calculateMd5(InputStream data) throws IOException {
		return DigestUtils.md5Hex(data);
	}
}
