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

class FileHashStoreFactoryTest extends spock.lang.Specification {

	private final static dir = Paths.get(System.getProperty("java.io.tmpdir")).resolve("temp1")

	def "Create a FileHashStore"() {
		setup:
		def factory = new FileHashStoreFactory()
		
		when:
		def store = factory.createFileHashStore(dir)
		
		then:
		store.getDirectory() == dir
	}
}