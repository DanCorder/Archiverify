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

import com.dancorder.Archiverify.ParallelFileTreeWalker.ParallelFileTreeWalker.PathProcessor;
import com.dancorder.Archiverify.testHelpers.*

class ParametersTest extends spock.lang.Specification {

	private final static rootPath = FileSystem.getTempDirectory().toString()
	private final static relativeRootPath = "relativePath"
	
	def "null parameter"() {
		when: "A null constructor parameter"
		def underTest = new Parameters(null)

		then:
		parametersAreInvalid(underTest)
	}
	
	def "empty parameter"() {
		when: "An empty constructor parameter"
		def underTest = new Parameters(new String[0])

		then:
		parametersAreInvalid(underTest)
	}
	
	def "single path"() {
		when: "A single value passed to constructor"
		def underTest = new Parameters( ["param1"] as String[] )
		
		then:
		parametersAreInvalid(underTest)
	}
	
	def "three paths"() {
		when: "Three values passed to constructor"
		def underTest = new Parameters( [ "param1", "param2", "param3" ] as String[] )

		then:
		parametersAreInvalid(underTest)
	}
	
	def "invalid first path"() {
		when: "The first parameter isn't a valid path"
		def underTest = new Parameters( [ "invalid1", rootPath ] as String[] )

		then:
		parametersAreInvalid(underTest)
	}
	
	def "invalid second path"() {
		when: "The second parameter isn't a valid path"
		def underTest = new Parameters( [ rootPath, "invalid2" ] as String[] )

		then:
		parametersAreInvalid(underTest)
	}
	
	def "relative paths are converted to absolute"() {
		given: "A relative directory path"
		def rootDir = FileSystem.getArchiverifyDirectory()
		def tempDir = FileSystem.createDirectoryIn(rootDir)
		def tempDirRelative = rootDir.relativize(tempDir).toString()
		
		when: "Relative paths are supplied"
		def underTest = new Parameters( [ tempDirRelative, tempDirRelative ] as String[] )
		
		then: "Paths are converted to absolute paths"
		underTest.isValid()
		underTest.getPath1().isAbsolute()
		underTest.getPath2().isAbsolute()
		
		cleanup:
		FileSystem.cleanUpDirectory(tempDir)
	}
	
	def "valid paths, no params"() {
		when: "Valid paths are supplied"
		def underTest = new Parameters( [ rootPath, rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getExecuteActions() == false
		underTest.getIsSingleDirectoryMode() == false
		underTest.getPath1().toString() == rootPath
		underTest.getPath2().toString() == rootPath
		underTest.getReadFile().toString() == ".hashes"
		underTest.getWriteFile().toString() == ".hashes"
	}
	
	def "test automatically execute actions"() {
		when:
		def underTest = new Parameters( [ "-y", rootPath, rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getExecuteActions() == true
		underTest.getPath1().toString() == rootPath
		underTest.getPath2().toString() == rootPath
	}
	
	def "single directory mode with one path"() {
		when:
		def underTest = new Parameters( [ "-s", rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getIsSingleDirectoryMode() == true
		underTest.getPath1().toString() == rootPath
	}
	
	def "single directory mode with two paths"() {
		when:
		def underTest = new Parameters( [ "-s", rootPath, rootPath ] as String[] )
		
		then:
		parametersAreInvalid(underTest)
	}
	
	def "alternate file names"() {
		when:
		def underTest = new Parameters( [ "-f", "testFileName", rootPath, rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getReadFile().toString() == "testFileName"
		underTest.getWriteFile().toString() == "testFileName"
	}
	
	def "alternate file name blank is invalid"() {
		when:
		def underTest = new Parameters( [ "-f", "", rootPath, rootPath ] as String[] )
		
		then:
		parametersAreInvalid(underTest)
	}
	
	def "alternate read file names"() {
		when:
		def underTest = new Parameters( [ "-fr", "testFileName", rootPath, rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getReadFile().toString() == "testFileName"
		underTest.getWriteFile().toString() == ".hashes"
	}
	
	def "alternate read file name blank is invalid"() {
		when:
		def underTest = new Parameters( [ "-fr", "", rootPath, rootPath ] as String[] )
		
		then:
		parametersAreInvalid(underTest)
	}
	
	def "alternate write file names"() {
		when:
		def underTest = new Parameters( [ "-fw", "testFileName", rootPath, rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getReadFile().toString() == ".hashes"
		underTest.getWriteFile().toString() == "testFileName"
	}
	
	def "alternate write file name blank is invalid"() {
		when:
		def underTest = new Parameters( [ "-w", "", rootPath, rootPath ] as String[] )
		
		then:
		parametersAreInvalid(underTest)
	}
	
	def "alternate read and write file names take priority"() {
		when:
		def underTest = new Parameters( [ "-f", "badFileName", "-fr", "testReadFileName", "-fw", "testWriteFileName", rootPath, rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getReadFile().toString() == "testReadFileName"
		underTest.getWriteFile().toString() == "testWriteFileName"
	}
	
	private void parametersAreInvalid(params) {
		assert !params.isValid()
		assert params.getErrorMessage() != null
		assert params.getErrorMessage().trim().length() > 0
	}
}
