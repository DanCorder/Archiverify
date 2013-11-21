package com.dancorder.PhotoSync

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;

import spock.lang.*

class SynchingVisitorTest extends spock.lang.Specification{

	private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
	private final Path root1 = tempDir.resolve("testRoot1");
	private final Path root2 = tempDir.resolve("testRoot2");
	
	def "no visits"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>()
		
		when:
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)		

		then:
		expectedResult.equals(visitor.getActions())
	}
	
	def "root directory"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when:
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths);

		then:
		expectedResult.equals(visitor.getActions())
	}
	
	def "file present both directories"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when:
		visitor.visitFile(Paths.get("testFile"), FileExistence.BothPaths);

		then:
		expectedResult.equals(visitor.getActions())
	}
	
	def "file present path 1"() {
		setup:
		Path filePath = Paths.get("testFile");
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root1.resolve(filePath), root2.resolve(filePath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when:
		visitor.visitFile(filePath, FileExistence.Path1Only);

		then:
		expectedResult.equals(visitor.getActions())
	}
	
	def "file present path 2"() {
		setup:
		Path filePath = Paths.get("testFile");
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root2.resolve(filePath), root1.resolve(filePath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when:
		visitor.visitFile(filePath, FileExistence.Path2Only);

		then:
		expectedResult.equals(visitor.getActions())
	}
	
	def "directory present both paths"() {
		setup:
		List<Action> expectedResult = new ArrayList<Action>();
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when:
		visitor.preVisitDirectory(Paths.get("testDirectory"), FileExistence.BothPaths);

		then:
		expectedResult.equals(visitor.getActions())
	}
	
	def "directory present path 1"() {
		setup:
		Path directoryPath = Paths.get("testDirectory");
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new CreateDirectoryAction(root2.resolve(directoryPath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when:
		visitor.preVisitDirectory(directoryPath, FileExistence.Path1Only);

		then:
		expectedResult.equals(visitor.getActions())
	}
	
	def "directory present path 2"() {
		setup:
		Path directoryPath = Paths.get("testDirectory");
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new CreateDirectoryAction(root1.resolve(directoryPath)));
		SynchingVisitor visitor = new SynchingVisitor(root1, root2)
		
		when:
		visitor.preVisitDirectory(directoryPath, FileExistence.Path2Only);

		then:
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
		
		when:
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		visitor.preVisitDirectory(rootPath, FileExistence.BothPaths);
		visitor.visitFile(file1Path, FileExistence.Path1Only);
		visitor.preVisitDirectory(directory1Path, FileExistence.Path2Only);
		visitor.visitFile(file2Path, FileExistence.Path2Only);
		visitor.preVisitDirectory(directory1Path, FileExistence.BothPaths);
		visitor.visitFile(file3Path, FileExistence.BothPaths);
		
		then:
		expectedResult.equals(visitor.getActions())
	}
	
}