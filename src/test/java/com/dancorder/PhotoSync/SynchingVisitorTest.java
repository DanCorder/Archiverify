package com.dancorder.PhotoSync;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;

public class SynchingVisitorTest {
	private final Path root1 = Paths.get("testRoot1");
	private final Path root2 = Paths.get("testRoot2");

	@Test
	public void testNoVisits() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
				
		List<Action> expectedResult = new ArrayList<Action>();
		
		assertEquals(expectedResult, visitor.getActions());
	}
	
	@Test
	public void testRootDirectory() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths);
		
		List<Action> expectedResult = new ArrayList<Action>();
		
		assertEquals(expectedResult, visitor.getActions());
	}
	
	@Test
	public void testFilePresentBothPaths() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		visitor.visitFile(Paths.get("testFile"), FileExistence.BothPaths);
		
		List<Action> expectedResult = new ArrayList<Action>();
		
		assertEquals(expectedResult, visitor.getActions());
	}
	
	@Test
	public void testFilePresentPath1() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		Path filePath = Paths.get("testFile");
		
		visitor.visitFile(filePath, FileExistence.Path1Only);
		
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root1.resolve(filePath), root2.resolve(filePath)));
		
		assertEquals(expectedResult, visitor.getActions());
	}
	
	@Test
	public void testFilePresentPath2() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		Path filePath = Paths.get("testFile");
		
		visitor.visitFile(filePath, FileExistence.Path2Only);
		
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root2.resolve(filePath), root1.resolve(filePath)));
		
		assertEquals(expectedResult, visitor.getActions());
	}
	
	@Test
	public void testDirectoryPresentBothPaths() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		visitor.preVisitDirectory(Paths.get("testDirectory"), FileExistence.BothPaths);
		
		List<Action> expectedResult = new ArrayList<Action>();
		
		assertEquals(expectedResult, visitor.getActions());
	}

	@Test
	public void testFilePresentDirectory1() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		Path directoryPath = Paths.get("testDirectory");
		
		visitor.preVisitDirectory(directoryPath, FileExistence.Path1Only);
		
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new CreateDirectoryAction(root2.resolve(directoryPath)));
		
		assertEquals(expectedResult, visitor.getActions());
	}
	
	@Test
	public void testFilePresentDirectory2() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);
		
		Path directoryPath = Paths.get("testDirectory");
		
		visitor.preVisitDirectory(directoryPath, FileExistence.Path2Only);
		
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new CreateDirectoryAction(root1.resolve(directoryPath)));
		
		assertEquals(expectedResult, visitor.getActions());
	}
	
	@Test
	public void testFilesAndDirectories() {
		SynchingVisitor visitor = new SynchingVisitor(root1, root2);

		Path rootPath = Paths.get("");
		Path file1Path = Paths.get("file1");
		Path directory1Path = Paths.get("testDirectory1");
		Path file2Path = directory1Path.resolve("file2");
		Path directory2Path = Paths.get("testDirectory2");
		Path file3Path = directory2Path.resolve("file3");
		
		visitor.preVisitDirectory(rootPath, FileExistence.BothPaths);
		visitor.visitFile(file1Path, FileExistence.Path1Only);
		visitor.preVisitDirectory(directory1Path, FileExistence.Path2Only);
		visitor.visitFile(file2Path, FileExistence.Path2Only);
		visitor.preVisitDirectory(directory1Path, FileExistence.BothPaths);
		visitor.visitFile(file3Path, FileExistence.BothPaths);
		
		List<Action> expectedResult = new ArrayList<Action>();
		expectedResult.add(new FileCopyAction(root1.resolve(file1Path), root2.resolve(file1Path)));
		expectedResult.add(new CreateDirectoryAction(root1.resolve(directory1Path)));
		expectedResult.add(new FileCopyAction(root2.resolve(file2Path), root1.resolve(file2Path)));
		
		assertEquals(expectedResult, visitor.getActions());
	}
}
