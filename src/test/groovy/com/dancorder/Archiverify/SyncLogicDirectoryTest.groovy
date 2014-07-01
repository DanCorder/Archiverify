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

package com.dancorder.Archiverify

import java.nio.file.Paths

import com.dancorder.Archiverify.ParallelFileTreeWalker.FileExistence

class SyncLogicDirectoryTest extends spock.lang.Specification {
	
//	The rules for the directory synching logic are simple:
//    - If a directory exists in both roots do nothing
//    - If a directory exists in only one root then create it in the other

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static absolutePath1 = tempDir.resolve("testRoot1").resolve("dir1")
	private final static absolutePath2 = tempDir.resolve("testRoot2").resolve("dir1")
	
	def "directory exists in both roots"() {
		setup:
		def logic = new SyncLogic(null)
		def expectedResult = new ArrayList<Action>()
		
		when:
		def result = logic.compareDirectories(absolutePath1, absolutePath2, FileExistence.BothPaths)

		then:
		expectedResult == result
	}
	
	def "directory exists in root 1"() {
		setup:
		def logic = new SyncLogic(null)
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new CreateDirectoryAction(absolutePath2))
		
		when:
		def result = logic.compareDirectories(absolutePath1, absolutePath2, FileExistence.Path1Only)

		then:
		expectedResult == result
	}
	
	def "directory exists in root 2"() {
		setup:
		def logic = new SyncLogic(null)
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new CreateDirectoryAction(absolutePath1))
		
		when:
		def result = logic.compareDirectories(absolutePath1, absolutePath2, FileExistence.Path2Only)

		then:
		expectedResult == result
	}
}