package com.dancorder.PhotoSync

import java.nio.file.Paths

import spock.lang.*

import com.dancorder.PhotoSync.Action.Action
import com.dancorder.PhotoSync.Action.CreateDirectoryAction
import com.dancorder.PhotoSync.Action.FileCopyAction
import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence

class SynchingVisitorTest extends spock.lang.Specification {

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static root1 = tempDir.resolve("testRoot1")
	private final static root2 = tempDir.resolve("testRoot2")
	
	private final static testFilePath = Paths.get("testFile")
	private final static testDirectoryPath = Paths.get("testDirectory")
	
	def "no visits"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		
		when: "A new visitor is created"
		def visitor = new SynchingVisitor(root1, root2)		

		then: "No actions are created"
		expectedResult == visitor.getActions()
	}
	
	def "root directory"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "It visits the root directory"
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)

		then: "No actions are created"
		expectedResult == visitor.getActions()
	}
	
	def "file present both directories"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "A file exists in both roots"
		visitor.visitFile(testFilePath, FileExistence.BothPaths)

		then: "No actions are created"
		expectedResult == visitor.getActions()
	}
	
	def "file present path 1"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new FileCopyAction(root1.resolve(testFilePath), root2.resolve(testFilePath)))
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "A file exists only in root 1"
		visitor.visitFile(testFilePath, FileExistence.Path1Only)

		then: "An action is created to copy from root 1 to root 2"
		expectedResult == visitor.getActions()
	}
	
	def "file present path 2"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new FileCopyAction(root2.resolve(testFilePath), root1.resolve(testFilePath)))
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "A file exists only in root 2"
		visitor.visitFile(testFilePath, FileExistence.Path2Only)

		then: "An action is created to copy from root 2 to root 1"
		expectedResult == visitor.getActions()
	}
	
	def "directory present both paths"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "A directory exists in both roots"
		visitor.preVisitDirectory(testDirectoryPath, FileExistence.BothPaths)

		then: "No actions are created"
		expectedResult == visitor.getActions()
	}
	
	def "directory present path 1"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new CreateDirectoryAction(root2.resolve(testDirectoryPath)))
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "A directory exists only in root 1"
		visitor.preVisitDirectory(testDirectoryPath, FileExistence.Path1Only)

		then: "An action is created to create directory under root 2"
		expectedResult == visitor.getActions()
	}
	
	def "directory present path 2"() {
		setup:
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new CreateDirectoryAction(root1.resolve(testDirectoryPath)))
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "A directory exists only in root 2"
		visitor.preVisitDirectory(testDirectoryPath, FileExistence.Path2Only)

		then: "An action is created to create directory under root 1"
		expectedResult == visitor.getActions()
	}
		
	def "files and directories together"() {
		setup:
		def rootPath = Paths.get("")
		def file1Path = Paths.get("file1")
		def directory1Path = Paths.get("testDirectory1")
		def file2Path = directory1Path.resolve("file2")
		def directory2Path = Paths.get("testDirectory2")
		def file3Path = directory2Path.resolve("file3")
		
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new FileCopyAction(root1.resolve(file1Path), root2.resolve(file1Path)))
		expectedResult.add(new CreateDirectoryAction(root1.resolve(directory1Path)))
		expectedResult.add(new FileCopyAction(root2.resolve(file2Path), root1.resolve(file2Path)))
		
		def visitor = new SynchingVisitor(root1, root2)
		
		when: "It visits a number of files and directories"
		visitor.preVisitDirectory(rootPath, FileExistence.BothPaths)
		visitor.visitFile(file1Path, FileExistence.Path1Only)
		visitor.preVisitDirectory(directory1Path, FileExistence.Path2Only)
		visitor.visitFile(file2Path, FileExistence.Path2Only)
		visitor.preVisitDirectory(directory1Path, FileExistence.BothPaths)
		visitor.visitFile(file3Path, FileExistence.BothPaths)
		
		then: "The correct actions are created in the correct order"
		expectedResult == visitor.getActions()
	}
	
}