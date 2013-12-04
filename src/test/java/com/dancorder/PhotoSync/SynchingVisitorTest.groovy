package com.dancorder.PhotoSync

import java.nio.file.Path
import java.nio.file.Paths

import spock.lang.*

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence

class SynchingVisitorTest extends spock.lang.Specification {

	private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final Path root1 = tempDir.resolve("testRoot1")
	private final Path root2 = tempDir.resolve("testRoot2")
	
	private final Path testFilePath = Paths.get("testFile")
	private final Path testDirectoryPath = Paths.get("testDirectory")
	
	def "no visits"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>()
		
		when: "A new visitor is created"
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)		

		then: "No actions are created"
		expectedResult.equals(visitor.getActions())
	}
	
	def "root directory"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when: "It visits the root directory"
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths);

		then: "No actions are created"
		expectedResult.equals(visitor.getActions())
	}
	
	def "file present both directories"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when: "A file exists in both roots"
		visitor.visitFile(testFilePath, FileExistence.BothPaths);

		then: "No actions are created"
		expectedResult.equals(visitor.getActions())
	}
	
	// TODO: Make this test compile
	// TODO: worry about character encoding somewhere
//	def "hash mismatch in path 2"() {
//		setup: "A file exists in both roots but the hash doesn't match in path 1"
//		List<Action> expectedResult = new ArrayList<Action>()
//		def hashCollection = Mock(fileHashStore)
//		// Mock when hashExists => true
//		// Mock hashMatches(root1.resolve(testFilePath)) => true
//		// Mock hashMatches(root2.resolve(testFilePath)) => false
//		expectedResult.add(new FixHashMismatchAction(root1.resolve(testFilePath), "goodHash", root2.resolve(testFilePath), "badHash"))
//		SynchingVisitor visitor = new SynchingVisitor(root1, root2, hashCollection)
//		
//		when: "it visits the file"
//		visitor.visitFile(testFilePath, FileExistence.BothPaths);
//
//		then: "a hash mismatch action is created"
//		expectedResult.equals(visitor.getActions())
//	}
	
	def "file present path 1"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root1.resolve(testFilePath), root2.resolve(testFilePath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when: "A file exists only in root 1"
		visitor.visitFile(testFilePath, FileExistence.Path1Only);

		then: "An action is created to copy from root 1 to root 2"
		expectedResult.equals(visitor.getActions())
	}
	
	def "file present path 2"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root2.resolve(testFilePath), root1.resolve(testFilePath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when: "A file exists only in root 2"
		visitor.visitFile(testFilePath, FileExistence.Path2Only);

		then: "An action is created to copy from root 2 to root 1"
		expectedResult.equals(visitor.getActions())
	}
	
	def "directory present both paths"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when: "A directory exists in both roots"
		visitor.preVisitDirectory(testDirectoryPath, FileExistence.BothPaths);

		then: "No actions are created"
		expectedResult.equals(visitor.getActions())
	}
	
	def "directory present path 1"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new CreateDirectoryAction(root2.resolve(testDirectoryPath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when: "A directory exists only in root 1"
		visitor.preVisitDirectory(testDirectoryPath, FileExistence.Path1Only);

		then: "An action is created to create directory under root 2"
		expectedResult.equals(visitor.getActions())
	}
	
	def "directory present path 2"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new CreateDirectoryAction(root1.resolve(testDirectoryPath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when: "A directory exists only in root 2"
		visitor.preVisitDirectory(testDirectoryPath, FileExistence.Path2Only);

		then: "An action is created to create directory under root 1"
		expectedResult.equals(visitor.getActions())
	}
		
	def "files and directories together"() {
		setup:
		Path rootPath = Paths.get("");
		Path file1Path = Paths.get("file1");
		Path directory1Path = Paths.get("testDirectory1");
		Path file2Path = directory1Path.resolve("file2");
		Path directory2Path = Paths.get("testDirectory2");
		Path file3Path = directory2Path.resolve("file3");
		
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root1.resolve(file1Path), root2.resolve(file1Path)));
		expectedResult.add(new CreateDirectoryAction(root1.resolve(directory1Path)));
		expectedResult.add(new FileCopyAction(root2.resolve(file2Path), root1.resolve(file2Path)));
		
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		when: "It visits a number of files and directories"
		visitor.preVisitDirectory(rootPath, FileExistence.BothPaths);
		visitor.visitFile(file1Path, FileExistence.Path1Only);
		visitor.preVisitDirectory(directory1Path, FileExistence.Path2Only);
		visitor.visitFile(file2Path, FileExistence.Path2Only);
		visitor.preVisitDirectory(directory1Path, FileExistence.BothPaths);
		visitor.visitFile(file3Path, FileExistence.BothPaths);
		
		then: "The correct actions are created in the correct order"
		expectedResult.equals(visitor.getActions())
	}
	
}