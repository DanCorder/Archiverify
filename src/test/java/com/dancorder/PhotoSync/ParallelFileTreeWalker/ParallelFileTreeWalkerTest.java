package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ParallelFileTreeWalkerTest {
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
	
	@Test
	public void testNestedSubdirectoriesPath1() throws IOException {
		final Path level1Path = Paths.get("subDirectory1");
		final Path level2Path = level1Path.resolve("subDirectory2");
		final Path level3Path = level2Path.resolve("subDirectory3");
		final Path level4Path = level3Path.resolve("subDirectory4");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path1Root, level1Path);
				createDirectory(path1Root, level2Path);
				createDirectory(path1Root, level3Path);
				createDirectory(path1Root, level4Path);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level1Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(level2Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(level3Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(level4Path, FileExistence.Path1Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testNestedSubdirectoriesPath2() throws IOException {
		final Path level1Path = Paths.get("subDirectory1");
		final Path level2Path = level1Path.resolve("subDirectory2");
		final Path level3Path = level2Path.resolve("subDirectory3");
		final Path level4Path = level3Path.resolve("subDirectory4");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path2Root, level1Path);
				createDirectory(path2Root, level2Path);
				createDirectory(path2Root, level3Path);
				createDirectory(path2Root, level4Path);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level1Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(level2Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(level3Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(level4Path, FileExistence.Path2Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testNestedSubdirectoriesBothPaths() throws IOException {
		final Path level1Path = Paths.get("subDirectory1");
		final Path level2Path = level1Path.resolve("subDirectory2");
		final Path level3Path = level2Path.resolve("subDirectory3");
		final Path level4Path = level3Path.resolve("subDirectory4");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path1Root, level1Path);
				createDirectory(path1Root, level2Path);
				createDirectory(path1Root, level3Path);
				createDirectory(path1Root, level4Path);
				createDirectory(path2Root, level1Path);
				createDirectory(path2Root, level2Path);
				createDirectory(path2Root, level3Path);
				createDirectory(path2Root, level4Path);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level1Path, FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level2Path, FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level3Path, FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level4Path, FileExistence.BothPaths));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testNestedFilePath1() throws IOException {
		final Path level1Path = Paths.get("subDirectory1");
		final Path level2Path = level1Path.resolve("subDirectory2");
		final Path level3Path = level2Path.resolve("subDirectory3");
		final Path filePath = level3Path.resolve("testFile");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path1Root, level1Path);
				createDirectory(path1Root, level2Path);
				createDirectory(path1Root, level3Path);
				createFile(path1Root, filePath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level1Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(level2Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(level3Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(filePath, FileExistence.Path1Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testNestedFilePath2() throws IOException {
		final Path level1Path = Paths.get("subDirectory1");
		final Path level2Path = level1Path.resolve("subDirectory2");
		final Path level3Path = level2Path.resolve("subDirectory3");
		final Path filePath = level3Path.resolve("testFile");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path2Root, level1Path);
				createDirectory(path2Root, level2Path);
				createDirectory(path2Root, level3Path);
				createFile(path2Root, filePath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level1Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(level2Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(level3Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(filePath, FileExistence.Path2Only));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testNestedFileBothPaths() throws IOException {
		final Path level1Path = Paths.get("subDirectory1");
		final Path level2Path = level1Path.resolve("subDirectory2");
		final Path level3Path = level2Path.resolve("subDirectory3");
		final Path filePath = level3Path.resolve("testFile");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createDirectory(path1Root, level1Path);
				createDirectory(path1Root, level2Path);
				createDirectory(path1Root, level3Path);
				createFile(path1Root, filePath);
				createDirectory(path2Root, level1Path);
				createDirectory(path2Root, level2Path);
				createDirectory(path2Root, level3Path);
				createFile(path2Root, filePath);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level1Path, FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level2Path, FileExistence.BothPaths));
		expected.add(new MethodCallPreVisitDirectory(level3Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(filePath, FileExistence.BothPaths));
		
		this.runTest(builder, expected);
	}
	
	@Test
	public void testNestedFilesAndDirectories() throws IOException {
		final Path file1Path = Paths.get("file1");
		final Path file2Path = Paths.get("file2");
		final Path file3Path = Paths.get("file3");
		final Path directory1Path = Paths.get("directory1");
		final Path file11Path = directory1Path.resolve("file11");
		final Path file12Path = directory1Path.resolve("file12");
		final Path file13Path = directory1Path.resolve("file13");
		final Path directory11Path = directory1Path.resolve("directory11");
		final Path file111Path = directory11Path.resolve("file111");
		final Path file112Path = directory11Path.resolve("file112");
		final Path file113Path = directory11Path.resolve("file113");
		final Path directory12Path = directory1Path.resolve("directory12");
		final Path file121Path = directory12Path.resolve("file121");
		final Path file122Path = directory12Path.resolve("file122");
		final Path file123Path = directory12Path.resolve("file123");
		final Path directory13Path = directory1Path.resolve("directory13");
		final Path file131Path = directory13Path.resolve("file131");
		final Path file132Path = directory13Path.resolve("file132");
		final Path file133Path = directory13Path.resolve("file133");
		final Path directory2Path = Paths.get("directory2");
		final Path file21Path = directory2Path.resolve("file21");
		final Path file22Path = directory2Path.resolve("file22");
		final Path file23Path = directory2Path.resolve("file23");
		final Path directory21Path = directory2Path.resolve("directory21");
		final Path file211Path = directory21Path.resolve("file211");
		final Path file212Path = directory21Path.resolve("file212");
		final Path file213Path = directory21Path.resolve("file213");
		final Path directory22Path = directory2Path.resolve("directory22");
		final Path file221Path = directory22Path.resolve("file221");
		final Path file222Path = directory22Path.resolve("file222");
		final Path file223Path = directory22Path.resolve("file223");
		final Path directory221Path = directory22Path.resolve("directory221");
		final Path file2211Path = directory221Path.resolve("file2211");
		final Path file2212Path = directory221Path.resolve("file2212");
		final Path file2213Path = directory221Path.resolve("file2213");
		final Path directory222Path = directory22Path.resolve("directory222");
		final Path file2221Path = directory222Path.resolve("file2221");
		final Path file2222Path = directory222Path.resolve("file2222");
		final Path file2223Path = directory222Path.resolve("file2223");
		final Path directory223Path = directory22Path.resolve("directory223");
		final Path file2231Path = directory223Path.resolve("file2231");
		final Path file2232Path = directory223Path.resolve("file2232");
		final Path file2233Path = directory223Path.resolve("file2233");
		final Path directory23Path = directory2Path.resolve("directory23");
		final Path file231Path = directory23Path.resolve("file231");
		final Path file232Path = directory23Path.resolve("file232");
		final Path file233Path = directory23Path.resolve("file233");
		final Path directory3Path = Paths.get("directory3");
		final Path file31Path = directory3Path.resolve("file31");
		final Path file32Path = directory3Path.resolve("file32");
		final Path file33Path = directory3Path.resolve("file33");
		
		FileTreeBuilder builder = new FileTreeBuilder() {
			public void build(Path path1Root, Path path2Root) throws IOException {
				createFile(path1Root, file1Path);
				createFile(path1Root, file2Path);
				createDirectory(path1Root, directory2Path);
				createDirectory(path1Root, directory21Path);
				createFile(path1Root, file211Path);
				createFile(path1Root, file212Path);
				createDirectory(path1Root, directory22Path);
				createFile(path1Root, file221Path);
				createFile(path1Root, file222Path);
				createFile(path1Root, file223Path);
				createDirectory(path1Root, directory221Path);
				createFile(path1Root, file2211Path);
				createFile(path1Root, file2212Path);
				createFile(path1Root, file2213Path);
				createDirectory(path1Root, directory222Path);
				createFile(path1Root, file2221Path);
				createFile(path1Root, file2222Path);
				createFile(path1Root, file2223Path);
				createDirectory(path1Root, directory223Path);
				createFile(path1Root, file2231Path);
				createFile(path1Root, file2232Path);
				createFile(path1Root, file2233Path);
				createDirectory(path1Root, directory23Path);
				createFile(path1Root, file231Path);
				createFile(path1Root, file232Path);
				createFile(path1Root, file233Path);
				createDirectory(path1Root, directory3Path);
				createFile(path1Root, file31Path);
				createFile(path1Root, file32Path);
				createFile(path1Root, file33Path);

				createFile(path2Root, file1Path);
				createFile(path2Root, file3Path);
				createDirectory(path2Root, directory1Path);
				createFile(path2Root, file11Path);
				createFile(path2Root, file12Path);
				createFile(path2Root, file13Path);
				createDirectory(path2Root, directory11Path);
				createFile(path2Root, file111Path);
				createFile(path2Root, file112Path);
				createFile(path2Root, file113Path);
				createDirectory(path2Root, directory12Path);
				createFile(path2Root, file121Path);
				createFile(path2Root, file122Path);
				createFile(path2Root, file123Path);
				createDirectory(path2Root, directory13Path);
				createFile(path2Root, file131Path);
				createFile(path2Root, file132Path);
				createFile(path2Root, file133Path);
				createDirectory(path2Root, directory2Path);
				createFile(path2Root, file21Path);
				createFile(path2Root, file22Path);
				createFile(path2Root, file23Path);
				createDirectory(path2Root, directory21Path);
				createFile(path2Root, file211Path);
				createFile(path2Root, file213Path);
				createDirectory(path2Root, directory3Path);
				createFile(path2Root, file31Path);
				createFile(path2Root, file32Path);
				createFile(path2Root, file33Path);
			}
		};
		
		List<MethodCall> expected = new ArrayList<MethodCall>();
		expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file1Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file2Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file3Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(directory1Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file11Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file12Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file13Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(directory11Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file111Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file112Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file113Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(directory12Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file121Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file122Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file123Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(directory13Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file131Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file132Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file133Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(directory2Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file21Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file22Path, FileExistence.Path2Only));
		expected.add(new MethodCallVisitFile(file23Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(directory21Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file211Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file212Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file213Path, FileExistence.Path2Only));
		expected.add(new MethodCallPreVisitDirectory(directory22Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file221Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file222Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file223Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directory221Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2211Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2212Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2213Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directory222Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2221Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2222Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2223Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directory223Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2231Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2232Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file2233Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directory23Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file231Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file232Path, FileExistence.Path1Only));
		expected.add(new MethodCallVisitFile(file233Path, FileExistence.Path1Only));
		expected.add(new MethodCallPreVisitDirectory(directory3Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file31Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file32Path, FileExistence.BothPaths));
		expected.add(new MethodCallVisitFile(file33Path, FileExistence.BothPaths));
		
		this.runTest(builder, expected);
	}
	
	private void runTest(FileTreeBuilder fileTreeBuilder, List<MethodCall> expectedResult) throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			tempRootPath2 = createRootDirectory();
			fileTreeBuilder.build(tempRootPath1, tempRootPath2);
			
			MockParallelFileTreeVisitor tpftv = new MockParallelFileTreeVisitor();
			
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
