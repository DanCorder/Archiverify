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

import java.nio.file.Paths

public class SyncWarningActionTest extends spock.lang.Specification {

	def "Warning contains all values"() {
		setup:
		def file1 = Paths.get("path1")
		def file2 = Paths.get("path2")
		def hash1 = "hash1"
		def hash2 = "hash2"
		def hash3 = "hash3"
		def hash4 = "hash4"
		
		def warning = "There was a problem synching " +
			file1 + " (calculated hash: " + hash1 + ", stored hash: " + hash2 + ") and " +
			file2 + " (calculated hash: " + hash3 + ", stored hash: " + hash4 + ")" +
			" please determine the correct file and hash and update the file(s) and/or hash(es)."
		
		def action = new SyncWarningAction(file1, hash1, hash2, file2, hash3, hash4)

		expect:
		action.toString().contains(warning)
	}
}
