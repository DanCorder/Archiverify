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

package com.dancorder.Archiverify.ParallelFileTreeWalker

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

public class ParallelFileTreeWalkerTest extends spock.lang.Specification {
	
	private static path1Root
	private static path2Root
	private static visitor
	private static walker
	
	def setup () {
		path1Root = createRootDirectory()
		path2Root = createRootDirectory()
		visitor = Mock(ParallelFileTreeVisitor)
		walker = new ParallelFileTreeWalker(path1Root, path2Root, visitor)
	}
	
	def cleanup() {
		cleanUpDirectory(path1Root)
		cleanUpDirectory(path2Root)
		path1Root = null
		path2Root = null
		visitor = null
		walker = null
	}
	
	
	def "empty tree"() {
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "single file in both roots"() {
		setup:
		def final filePath = Paths.get("testFile")
		createFile(path1Root, filePath)
		createFile(path2Root, filePath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.visitFile(filePath, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "single file in path 1 root"() {
		setup:
		def final filePath = Paths.get("testFile")
		createFile(path1Root, filePath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.visitFile(filePath, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "single file in path 2 root"() {
		setup:
		def final filePath = Paths.get("testFile")
		createFile(path2Root, filePath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.visitFile(filePath, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "two files in both roots"() {
		setup:
		def final file1Path = Paths.get("testFile1")
		def final file2Path = Paths.get("testFile2")
		createFile(path1Root, file1Path)
		createFile(path1Root, file2Path)
		createFile(path2Root, file1Path)
		createFile(path2Root, file2Path)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file1Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file2Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}

	def "various files in both roots"() {
		setup:
		def final file1Path = Paths.get("testFile1")
		def final file2Path = Paths.get("testFile2")
		def final file3Path = Paths.get("testFile3")
		def final file4Path = Paths.get("testFile4")
		createFile(path1Root, file1Path)
		createFile(path2Root, file1Path)
		createFile(path1Root, file2Path)
		createFile(path1Root, file3Path)
		createFile(path2Root, file3Path)
		createFile(path2Root, file4Path)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file1Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file2Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file3Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file4Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "files processed before directories"() {
		setup:
		def final directory1Path = Paths.get("aaaa")
		def final directory2Path = Paths.get("bbbb")
		def final file1Path = Paths.get("cccc")
		def final directory3Path = Paths.get("dddd")
		createDirectory(path1Root, directory1Path)
		createFile(path1Root, file1Path)
		createDirectory(path1Root, directory2Path)
		createDirectory(path1Root, directory3Path)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file1Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(directory1Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory1Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(directory2Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory2Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(directory3Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory3Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "sub directory both paths"() {
		setup:
		def final directoryPath = Paths.get("subDirectory1")
		createDirectory(path1Root, directoryPath)
		createDirectory(path2Root, directoryPath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(directoryPath, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(directoryPath, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "sub directory path 1 only"() {
		setup:
		def final directoryPath = Paths.get("subDirectory1")
		createDirectory(path1Root, directoryPath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(directoryPath, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directoryPath, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}

	def "sub directory path 2 only"() {
		setup:
		def final directoryPath = Paths.get("subDirectory1")
		createDirectory(path2Root, directoryPath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(directoryPath, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(directoryPath, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}

	def "nested sub directories path 1 only"() {
		setup:
		def final level1Path = Paths.get("subDirectory1")
		def final level2Path = level1Path.resolve("subDirectory2")
		def final level3Path = level2Path.resolve("subDirectory3")
		def final level4Path = level3Path.resolve("subDirectory4")
		createDirectory(path1Root, level1Path)
		createDirectory(path1Root, level2Path)
		createDirectory(path1Root, level3Path)
		createDirectory(path1Root, level4Path)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level1Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(level2Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(level3Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(level4Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(level4Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(level3Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(level2Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(level1Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "nested sub directories path 2 only"() {
		setup:
		def final level1Path = Paths.get("subDirectory1")
		def final level2Path = level1Path.resolve("subDirectory2")
		def final level3Path = level2Path.resolve("subDirectory3")
		def final level4Path = level3Path.resolve("subDirectory4")
		createDirectory(path2Root, level1Path)
		createDirectory(path2Root, level2Path)
		createDirectory(path2Root, level3Path)
		createDirectory(path2Root, level4Path)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level1Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(level2Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(level3Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(level4Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(level4Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(level3Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(level2Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(level1Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "nested sub directories both paths"() {
		setup:
		def final level1Path = Paths.get("subDirectory1")
		def final level2Path = level1Path.resolve("subDirectory2")
		def final level3Path = level2Path.resolve("subDirectory3")
		def final level4Path = level3Path.resolve("subDirectory4")
		createDirectory(path1Root, level1Path)
		createDirectory(path1Root, level2Path)
		createDirectory(path1Root, level3Path)
		createDirectory(path1Root, level4Path)
		createDirectory(path2Root, level1Path)
		createDirectory(path2Root, level2Path)
		createDirectory(path2Root, level3Path)
		createDirectory(path2Root, level4Path)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level1Path, FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level2Path, FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level3Path, FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level4Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(level4Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(level3Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(level2Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(level1Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "nested file path 1 only"() {
		setup:
		def final level1Path = Paths.get("subDirectory1")
		def final level2Path = level1Path.resolve("subDirectory2")
		def final level3Path = level2Path.resolve("subDirectory3")
		def final filePath = level3Path.resolve("testFile")
		createDirectory(path1Root, level1Path)
		createDirectory(path1Root, level2Path)
		createDirectory(path1Root, level3Path)
		createFile(path1Root, filePath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level1Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(level2Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(level3Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(filePath, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(level3Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(level2Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(level1Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "nested file path 2 only"() {
		setup:
		def final level1Path = Paths.get("subDirectory1")
		def final level2Path = level1Path.resolve("subDirectory2")
		def final level3Path = level2Path.resolve("subDirectory3")
		def final filePath = level3Path.resolve("testFile")
		createDirectory(path2Root, level1Path)
		createDirectory(path2Root, level2Path)
		createDirectory(path2Root, level3Path)
		createFile(path2Root, filePath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level1Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(level2Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(level3Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(filePath, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(level3Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(level2Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(level1Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}
	
	def "nested file both paths"() {
		setup:
		def final level1Path = Paths.get("subDirectory1")
		def final level2Path = level1Path.resolve("subDirectory2")
		def final level3Path = level2Path.resolve("subDirectory3")
		def final filePath = level3Path.resolve("testFile")
		createDirectory(path1Root, level1Path)
		createDirectory(path1Root, level2Path)
		createDirectory(path1Root, level3Path)
		createFile(path1Root, filePath)
		createDirectory(path2Root, level1Path)
		createDirectory(path2Root, level2Path)
		createDirectory(path2Root, level3Path)
		createFile(path2Root, filePath)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level1Path, FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level2Path, FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(level3Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(filePath, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(level3Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(level2Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(level1Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 0 * _._
	}

	def "various files and directories"() {
		setup:
		def final file1Path = Paths.get("file1")
		def final file2Path = Paths.get("file2")
		def final file3Path = Paths.get("file3")
		def final directory1Path = Paths.get("directory1")
		def final file11Path = directory1Path.resolve("file11")
		def final file12Path = directory1Path.resolve("file12")
		def final file13Path = directory1Path.resolve("file13")
		def final directory11Path = directory1Path.resolve("directory11")
		def final file111Path = directory11Path.resolve("file111")
		def final file112Path = directory11Path.resolve("file112")
		def final file113Path = directory11Path.resolve("file113")
		def final directory12Path = directory1Path.resolve("directory12")
		def final file121Path = directory12Path.resolve("file121")
		def final file122Path = directory12Path.resolve("file122")
		def final file123Path = directory12Path.resolve("file123")
		def final directory13Path = directory1Path.resolve("directory13")
		def final file131Path = directory13Path.resolve("file131")
		def final file132Path = directory13Path.resolve("file132")
		def final file133Path = directory13Path.resolve("file133")
		def final directory2Path = Paths.get("directory2")
		def final file21Path = directory2Path.resolve("file21")
		def final file22Path = directory2Path.resolve("file22")
		def final file23Path = directory2Path.resolve("file23")
		def final directory21Path = directory2Path.resolve("directory21")
		def final file211Path = directory21Path.resolve("file211")
		def final file212Path = directory21Path.resolve("file212")
		def final file213Path = directory21Path.resolve("file213")
		def final directory22Path = directory2Path.resolve("directory22")
		def final file221Path = directory22Path.resolve("file221")
		def final file222Path = directory22Path.resolve("file222")
		def final file223Path = directory22Path.resolve("file223")
		def final directory221Path = directory22Path.resolve("directory221")
		def final file2211Path = directory221Path.resolve("file2211")
		def final file2212Path = directory221Path.resolve("file2212")
		def final file2213Path = directory221Path.resolve("file2213")
		def final directory222Path = directory22Path.resolve("directory222")
		def final file2221Path = directory222Path.resolve("file2221")
		def final file2222Path = directory222Path.resolve("file2222")
		def final file2223Path = directory222Path.resolve("file2223")
		def final directory223Path = directory22Path.resolve("directory223")
		def final file2231Path = directory223Path.resolve("file2231")
		def final file2232Path = directory223Path.resolve("file2232")
		def final file2233Path = directory223Path.resolve("file2233")
		def final directory23Path = directory2Path.resolve("directory23")
		def final file231Path = directory23Path.resolve("file231")
		def final file232Path = directory23Path.resolve("file232")
		def final file233Path = directory23Path.resolve("file233")
		def final directory3Path = Paths.get("directory3")
		def final file31Path = directory3Path.resolve("file31")
		def final file32Path = directory3Path.resolve("file32")
		def final file33Path = directory3Path.resolve("file33")
		
		createFile(path1Root, file1Path)
		createFile(path1Root, file2Path)
		createDirectory(path1Root, directory2Path)
		createDirectory(path1Root, directory21Path)
		createFile(path1Root, file211Path)
		createFile(path1Root, file212Path)
		createDirectory(path1Root, directory22Path)
		createFile(path1Root, file221Path)
		createFile(path1Root, file222Path)
		createFile(path1Root, file223Path)
		createDirectory(path1Root, directory221Path)
		createFile(path1Root, file2211Path)
		createFile(path1Root, file2212Path)
		createFile(path1Root, file2213Path)
		createDirectory(path1Root, directory222Path)
		createFile(path1Root, file2221Path)
		createFile(path1Root, file2222Path)
		createFile(path1Root, file2223Path)
		createDirectory(path1Root, directory223Path)
		createFile(path1Root, file2231Path)
		createFile(path1Root, file2232Path)
		createFile(path1Root, file2233Path)
		createDirectory(path1Root, directory23Path)
		createFile(path1Root, file231Path)
		createFile(path1Root, file232Path)
		createFile(path1Root, file233Path)
		createDirectory(path1Root, directory3Path)
		createFile(path1Root, file31Path)
		createFile(path1Root, file32Path)
		createFile(path1Root, file33Path)

		createFile(path2Root, file1Path)
		createFile(path2Root, file3Path)
		createDirectory(path2Root, directory1Path)
		createFile(path2Root, file11Path)
		createFile(path2Root, file12Path)
		createFile(path2Root, file13Path)
		createDirectory(path2Root, directory11Path)
		createFile(path2Root, file111Path)
		createFile(path2Root, file112Path)
		createFile(path2Root, file113Path)
		createDirectory(path2Root, directory12Path)
		createFile(path2Root, file121Path)
		createFile(path2Root, file122Path)
		createFile(path2Root, file123Path)
		createDirectory(path2Root, directory13Path)
		createFile(path2Root, file131Path)
		createFile(path2Root, file132Path)
		createFile(path2Root, file133Path)
		createDirectory(path2Root, directory2Path)
		createFile(path2Root, file21Path)
		createFile(path2Root, file22Path)
		createFile(path2Root, file23Path)
		createDirectory(path2Root, directory21Path)
		createFile(path2Root, file211Path)
		createFile(path2Root, file213Path)
		createDirectory(path2Root, directory3Path)
		createFile(path2Root, file31Path)
		createFile(path2Root, file32Path)
		createFile(path2Root, file33Path)
		
		when:
		walker.walk()

		then: 1 * visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file1Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file2Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file3Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(directory1Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file11Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file12Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file13Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(directory11Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file111Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file112Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file113Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(directory11Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(directory12Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file121Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file122Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file123Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(directory12Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(directory13Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file131Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file132Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file133Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(directory13Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(directory1Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(directory2Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file21Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file22Path, FileExistence.Path2Only)
		then: 1 * visitor.visitFile(file23Path, FileExistence.Path2Only)
		then: 1 * visitor.preVisitDirectory(directory21Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file211Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file212Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file213Path, FileExistence.Path2Only)
		then: 1 * visitor.postVisitDirectory(directory21Path, FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(directory22Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file221Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file222Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file223Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(directory221Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2211Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2212Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2213Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory221Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(directory222Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2221Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2222Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2223Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory222Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(directory223Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2231Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2232Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file2233Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory223Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory22Path, FileExistence.Path1Only)
		then: 1 * visitor.preVisitDirectory(directory23Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file231Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file232Path, FileExistence.Path1Only)
		then: 1 * visitor.visitFile(file233Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory23Path, FileExistence.Path1Only)
		then: 1 * visitor.postVisitDirectory(directory2Path, FileExistence.BothPaths)
		then: 1 * visitor.preVisitDirectory(directory3Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file31Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file32Path, FileExistence.BothPaths)
		then: 1 * visitor.visitFile(file33Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(directory3Path, FileExistence.BothPaths)
		then: 1 * visitor.postVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		
		then: 0 * _._
	}

	private Path createRootDirectory() throws IOException {
		return Files.createTempDirectory(null)
	}
	
	private Path createFile(Path directory, Path fileName) throws IOException {
		return Files.createFile(directory.resolve(fileName))
	}
	
	private Path createDirectory(Path directory, Path subDirectoryName) throws IOException {
		return Files.createDirectory(directory.resolve(subDirectoryName))
	}
	
	private void cleanUpDirectory(Path directory) throws IOException {
		if (!deleteRecursive(directory.toFile())) {
			throw new IOException("Failed to clean up directory " + directory.toString())
		}
	}
	
	private boolean deleteRecursive(File path) throws FileNotFoundException{
		if (!path.exists()) return true
		
		boolean ret = true
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f)
			}
		}
		return ret && path.delete()
	}
}
