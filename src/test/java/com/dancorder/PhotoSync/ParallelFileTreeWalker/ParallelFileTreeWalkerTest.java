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
	
	@Test
	public void testEmptyTree() throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			tempRootPath2 = createRootDirectory();
			TestParallelFileTreeVisitor tpftv = new TestParallelFileTreeVisitor();
				
			ParallelFileTreeWalker pftw = new ParallelFileTreeWalker(tempRootPath1, tempRootPath2, tpftv);
			pftw.walk();
			
			List<MethodCall> expected = new ArrayList<MethodCall>();
			expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
			assertEquals(expected, tpftv.getCalls());
			
		}
		finally {
			cleanUpDirectory(tempRootPath1);
			cleanUpDirectory(tempRootPath2);
		}
	}
	
	@Test
	public void testSingleFileBothPathsRoot() throws IOException {
		Path tempRootPath1 = null;
		Path tempRootPath2 = null;
		
		try {
			tempRootPath1 = createRootDirectory();
			tempRootPath2 = createRootDirectory();
			Path filePath1 = createFile(tempRootPath1, "testFile");
			createFile(tempRootPath2, filePath1.getFileName().toString());
			TestParallelFileTreeVisitor tpftv = new TestParallelFileTreeVisitor();
			
			ParallelFileTreeWalker pftw = new ParallelFileTreeWalker(tempRootPath1, tempRootPath2, tpftv);
			pftw.walk();
			
			List<MethodCall> expected = new ArrayList<MethodCall>();
			expected.add(new MethodCallPreVisitDirectory(Paths.get(""), FileExistence.BothPaths));
			expected.add(new MethodCallVisitFile(filePath1.getFileName(), FileExistence.BothPaths));
			assertEquals(expected, tpftv.getCalls());
		}
		finally {
			cleanUpDirectory(tempRootPath1);
			cleanUpDirectory(tempRootPath2);
		}
	}
	
	private Path createRootDirectory() throws IOException {
		return Files.createTempDirectory(null);
	}
	
	private Path createFile(Path directory, String fileName) throws IOException {
		return Files.createFile(directory.resolve(fileName));
	}
	
	private void cleanUpDirectory(Path directory) throws IOException {
		if (!deleteRecursive(directory.toFile())) {
			throw new IOException("Failed to clean up directory " + directory.toString());
		}
	}
	
	private boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }
}
