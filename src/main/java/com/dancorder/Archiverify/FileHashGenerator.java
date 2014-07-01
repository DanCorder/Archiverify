//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2014  Daniel Corder (contact: archiverify@dancorder.com)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.dancorder.Archiverify;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;

class FileHashGenerator {
	String calculateMd5(Path file) throws IOException {
		if (!Files.exists(file)) {
			return null;
		}
		
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file.toString());
			return DigestUtils.md5Hex(stream);
		}
		finally {
			if (stream != null) {
				stream.close();
			}
		}
	}
}
