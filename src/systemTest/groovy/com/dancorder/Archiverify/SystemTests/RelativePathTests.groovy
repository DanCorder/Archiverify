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
import java.nio.file.*

public class RelativePathTests extends spock.lang.Specification {
   
   private static Path path1Root
   private static Path path2Root
   private static Path path1RootRelative
   private static Path path2RootRelative
   private static Path buildOutputDirectory = FileSystem.getBuildOutputDirectory()
   
   def setup () {
       path1Root = FileSystem.createDirectoryIn(buildOutputDirectory)
       path2Root = FileSystem.createDirectoryIn(buildOutputDirectory)
	   path1RootRelative = buildOutputDirectory.relativize(path1Root)
	   path2RootRelative = buildOutputDirectory.relativize(path2Root)
   }
   
   def cleanup() {
       FileSystem.cleanUpDirectory(path1Root)
       FileSystem.cleanUpDirectory(path2Root)
       path1Root = null
       path2Root = null
	   path1RootRelative = null
	   path2RootRelative = null
   }
   
   def "Empty roots don't cause errors"() {
       when: "Archiverify is run with empty roots"
       def result = Run.archiverify(path1RootRelative.toString(), path2RootRelative.toString(), "-y")
       
       then: "No errors found"
       !result.stdout.toLowerCase().contains("error")
   }
   
   def "Subdirectories don't cause errors"() {
       given: "Empty subdirectories"
       FileSystem.createDirectoryIn(path1Root, "test")
       FileSystem.createDirectoryIn(path2Root, "test")
       
       when: "Archiverify is run"
       def result = Run.archiverify(path1RootRelative.toString(), path2RootRelative.toString(), "-y")

       then: "No errors found"
       !result.stdout.toLowerCase().contains("error")
   }
   
   def "Files don't cause errors"() {
       given: "Files in subdirectories"
       def path1SubDir = FileSystem.createDirectoryIn(path1Root, "test")
       def path2SubDir = FileSystem.createDirectoryIn(path2Root, "test")
       FileSystem.createFileIn(path1SubDir, "testFile")
       FileSystem.createFileIn(path2SubDir, "testFile")
       
       when: "Archiverify is run"
       def result = Run.archiverify(path1RootRelative.toString(), path2RootRelative.toString(), "-y")
       
       then: "No errors found"
       !result.stdout.toLowerCase().contains("error")
   }
   
   def "Directory creation doesn't cause errors"() {
       given: "Files in subdirectories"
       def path1SubDir = FileSystem.createDirectoryIn(path1Root, "test")
       
       when: "Archiverify is run"
       def result = Run.archiverify(path1RootRelative.toString(), path2RootRelative.toString(), "-y")

       then: "No errors found"
       !result.stdout.toLowerCase().contains("error")
   }
   
   def "File copy doesn't cause errors"() {
       given: "Files in subdirectories"
       def path1SubDir = FileSystem.createDirectoryIn(path1Root, "test")
       def path2SubDir = FileSystem.createDirectoryIn(path2Root, "test")
       FileSystem.createFileIn(path1SubDir, "testFile")
       
       when: "Archiverify is run"
	   def result = Run.archiverify(path1RootRelative.toString(), path2RootRelative.toString(), "-y")
       
       then: "No errors found"
       !result.stdout.toLowerCase().contains("error")
   }
}