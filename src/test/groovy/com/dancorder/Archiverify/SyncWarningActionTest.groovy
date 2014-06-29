package com.dancorder.Archiverify;

import java.nio.file.Paths

import com.dancorder.Archiverify.SyncWarningAction;


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
