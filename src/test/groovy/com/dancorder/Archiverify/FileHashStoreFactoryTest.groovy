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
import com.dancorder.Archiverify.testHelpers.*

class FileHashStoreFactoryTest extends spock.lang.Specification {

	private final static dir = FileSystem.getTempDirectory().resolve("temp1")
	private final static readFile = Paths.get("readFile")
	private final static writeFile = Paths.get("writeFile")

	def "Create a FileHashStore"() {
		setup:
		def factory = new FileHashStoreFactory(readFile, writeFile)
		
		when:
		def store = factory.createFileHashStore(dir)
		
		then:
		store.getDirectory() == dir
	}
	
	def "Hash files recognised"() {
		setup:
		def factory = new FileHashStoreFactory(readFile, writeFile)
		
		expect:
		factory.isHashFile(dir.resolve("readFile"))
		factory.isHashFile(dir.resolve("writeFile"))
		!factory.isHashFile(dir.resolve("otherFile"))
	}
}