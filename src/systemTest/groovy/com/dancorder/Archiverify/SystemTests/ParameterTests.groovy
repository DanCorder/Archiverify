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

package com.dancorder.Archiverify.SystemTests;

import com.dancorder.Archiverify.testHelpers.*
import java.nio.file.Path

public class ParameterTests extends spock.lang.Specification {
	
	private static Path path1Root
	private static Path path2Root
	
	def setup () {
		path1Root = FileSystem.createRootDirectory()
		path2Root = FileSystem.createRootDirectory()
	}
	
	def cleanup() {
		FileSystem.cleanUpDirectory(path1Root)
		FileSystem.cleanUpDirectory(path2Root)
		path1Root = null
		path2Root = null
	}
	
	def "When run with no parameters usage message is shown"() {

		when: "No parameters are passed"
		def result = Run.archiverify()

		then: "Usage instructions are printed"
		result.stdout.contains("usage: Archiverify")
	}
	
	def "When run with no files no actions are found"() {
		
		when: "Archiverify is run with empty roots"
		def result = Run.archiverify(path1Root.toString(), path2Root.toString())
		
		then: "No actions are found"
		result.stdout.contains("Nothing to do")
	}
}
