package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeWalker;

public class ParallelFileTreeWalkerTest {
	
	@Test(expected = IllegalArgumentException.class)
	public void testMissingPath1() throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			cleanUpDirectory(tempRootPath1);
			tempRootPath2 = createRootDirectory();
			TestParallelFileTreeVisitor tpftv = new TestParallelFileTreeVisitor();
				
			ParallelFileTreeWalker pftw = new ParallelFileTreeWalker(tempRootPath1, tempRootPath2, tpftv);
			pftw.walk();
		}
		finally {
			cleanUpDirectory(tempRootPath1);
			cleanUpDirectory(tempRootPath2);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMissingPath2() throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			tempRootPath2 = createRootDirectory();
			cleanUpDirectory(tempRootPath2);
			TestParallelFileTreeVisitor tpftv = new TestParallelFileTreeVisitor();
				
			ParallelFileTreeWalker pftw = new ParallelFileTreeWalker(tempRootPath1, tempRootPath2, tpftv);
			pftw.walk();
		}
		finally {
			cleanUpDirectory(tempRootPath1);
			cleanUpDirectory(tempRootPath2);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testMissingBothPaths() throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			cleanUpDirectory(tempRootPath1);
			tempRootPath2 = createRootDirectory();
			cleanUpDirectory(tempRootPath2);
			TestParallelFileTreeVisitor tpftv = new TestParallelFileTreeVisitor();
				
			ParallelFileTreeWalker pftw = new ParallelFileTreeWalker(tempRootPath1, tempRootPath2, tpftv);
			pftw.walk();
		}
		finally {
			cleanUpDirectory(tempRootPath1);
			cleanUpDirectory(tempRootPath2);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullVisitor() throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			tempRootPath2 = createRootDirectory();
				
			ParallelFileTreeWalker pftw = new ParallelFileTreeWalker(tempRootPath1, tempRootPath2, null);
			pftw.walk();
		}
		finally {
			cleanUpDirectory(tempRootPath1);
			cleanUpDirectory(tempRootPath2);
		}
	}
	
	@Test
	public void testEmptyTree() throws IOException {
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				// Do nothing
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testSingleFileBothPathsRoot() throws IOException {
		final Path filePath = Paths.get("testFile");

		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createFile(path1Root, filePath);
				createFile(path2Root, filePath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(filePath, FileExistence.BothPaths));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testSingleFilePath1Root() throws IOException {
		final Path filePath = Paths.get("testFile");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createFile(path1Root, filePath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(filePath, FileExistence.Path1Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testSingleFilePath2Root() throws IOException {
		final Path filePath = Paths.get("testFile");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createFile(path2Root, filePath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(filePath, FileExistence.Path2Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testTwoFilesBothPathsRoot() throws IOException {
		
		final Path file1Path = Paths.get("testFile1");
		final Path file2Path = Paths.get("testFile2");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createFile(path1Root, file1Path);
				createFile(path1Root, file2Path);
				createFile(path2Root, file1Path);
				createFile(path2Root, file2Path);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file1Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file2Path, FileExistence.BothPaths));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testVariousFilesInRoot() throws IOException {
		
		final Path file1Path = Paths.get("testFile1");
		final Path file2Path = Paths.get("testFile2");
		final Path file3Path = Paths.get("testFile3");
		final Path file4Path = Paths.get("testFile4");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createFile(path1Root, file1Path);
				createFile(path2Root, file1Path);
				createFile(path1Root, file2Path);
				createFile(path1Root, file3Path);
				createFile(path2Root, file3Path);
				createFile(path2Root, file4Path);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file1Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file2Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file3Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file4Path, FileExistence.Path2Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testFilesProcessedBeforeDirectories() throws IOException {
		final Path directoryPath1 = Paths.get("aaaa");
		final Path directoryPath2 = Paths.get("bbbb");
		final Path filePath1 = Paths.get("cccc");
		final Path directoryPath3 = Paths.get("dddd");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path1Root, directoryPath1);
				createFile(path1Root, filePath1);
				createDirectory(path1Root, directoryPath2);
				createDirectory(path1Root, directoryPath3);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(filePath1, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directoryPath1, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directoryPath2, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directoryPath3, FileExistence.Path1Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testSubdirectoryBothPaths() throws IOException {
		final Path directoryPath = Paths.get("subDirectory1");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path1Root, directoryPath);
				createDirectory(path2Root, directoryPath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(directoryPath, FileExistence.BothPaths));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testSubdirectoryPath1() throws IOException {
		final Path directoryPath = Paths.get("subDirectory1");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path1Root, directoryPath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(directoryPath, FileExistence.Path1Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testSubdirectoryPath2() throws IOException {
		final Path directoryPath = Paths.get("subDirectory1");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path2Root, directoryPath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(directoryPath, FileExistence.Path2Only));
		
		this.runTest(builder, expected);
	}
	
	private void runTest(FileTreeBuilder fileTreeBuilder, List<MethodCall> expectedResult) throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			tempRootPath2 = createRootDirectory();
			fileTreeBuilder.build(tempRootPath1, tempRootPath2);
			
			TestParallelFileTreeVisitor tpftv = new TestParallelFileTreeVisitor();
			
			ParallelFileTreeWalker pftw = new ParallelFileTreeWalker(tempRootPath1, tempRootPath2, tpftv);
			pftw.walk();

			assertEquals(expectedResult, tpftv.getCalls());
		}
		finally {
			cleanUpDirectory(tempRootPath1);
			cleanUpDirectory(tempRootPath2);
		}
	}
	
	private interface FileTreeBuilder {
		void build(Path path1Root, Path path2Root) throws IOException;
	}
	
	private Path createRootDirectory() throws IOException {
		return Files.createTempDirectory(null);
	}
	
	private Path createFile(Path directory, Path fileName) throws IOException {
		return Files.createFile(directory.resolve(fileName));
	}
	
	private Path createDirectory(Path directory, Path subDirectoryName) throws IOException {
		return Files.createDirectory(directory.resolve(subDirectoryName));
	}
	
	private void cleanUpDirectory(Path directory) throws IOException {
		if (!deleteRecursive(directory.toFile())) {
			throw new IOException("Failed to clean up directory " + directory.toString());
		}
	}
	
	private boolean deleteRecursive(File path) throws FileNotFoundException{
		if (!path.exists()) return true;
		
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}
}
