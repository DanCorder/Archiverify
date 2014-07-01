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

import java.nio.file.Files
import java.nio.file.Paths

public class CreateDirectoryActionTest extends spock.lang.Specification {
	private static final tempDir = Paths.get(System.getProperty("java.io.tmpdir"))

	def "Null path"() {
		when: "A null parameter is passed"
		new CreateDirectoryAction(null)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)
	}
	
	def "Relative path"() {
		setup:
		def directoryPath = Paths.get("testDirectory")

		when: "A relative path is passed"
		new CreateDirectoryAction(directoryPath)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)
	}
	
	def "Create a directory"() {
		setup:
		def directoryPath = tempDir.resolve("testDirectory")
		def cda = new CreateDirectoryAction(directoryPath)

		when: "doACtion is called"
		cda.doAction()

		then: "A new directory is created"
		Files.exists(directoryPath)

		cleanup:
		if (Files.exists(directoryPath)) {
			Files.delete(directoryPath)
		}
	}
	
	def "String value"() {
		setup:
		def directoryPath = tempDir.resolve("testDirectory")
		def cda = new CreateDirectoryAction(directoryPath)

		expect:
		cda.toString() == "Create directory: " + directoryPath.toString()
	}
}
