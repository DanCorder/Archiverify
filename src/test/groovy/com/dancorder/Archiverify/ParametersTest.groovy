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


class ParametersTest extends spock.lang.Specification {

	private final static rootPath = File.listRoots()[0].getAbsolutePath()
	
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
	
	def "valid paths, no params"() {
		when: "Valid paths are supplied"
		def underTest = new Parameters( [ rootPath, rootPath ] as String[] )
		
		then:
		underTest.isValid()
		underTest.getExecuteActions() == false
		underTest.getIsSingleDirectoryMode() == false
		underTest.getPath1().toString() == rootPath
		underTest.getPath2().toString() == rootPath
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
	
	def "single directory mode with one two paths"() {
		when:
		def underTest = new Parameters( [ "-s", rootPath, rootPath ] as String[] )
		
		then:
		parametersAreInvalid(underTest)
	}
	
	private void parametersAreInvalid(params) {
		assert !params.isValid()
		assert params.getErrorMessage() != null
		assert params.getErrorMessage().trim().length() > 0
	}
}
