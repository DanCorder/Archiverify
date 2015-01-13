//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2015  Daniel Corder (contact: archiverify@dancorder.com)
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

package com.dancorder.Archiverify.IntegrationTests.Helpers

import java.io.InputStream;

class RunResult {
	RunResult(Process process) {
		stdout = inputStreamToString(process.inputStream)
		stderr = inputStreamToString(process.errorStream)
	}
	
	public String stdout
	public String stderr
	
	private static String inputStreamToString(InputStream stream) {
		StringBuilder builder = new StringBuilder();
		String line;
		
		def reader = new BufferedReader(new InputStreamReader(stream))
		try {
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"))
			}
		} finally {
			reader.close()
		}
		
		return builder.toString()
	}
}
