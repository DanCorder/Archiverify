package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import static org.junit.Assert.*;

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
			Files.delete(tempRootPath1);
			Files.delete(tempRootPath2);
		}
	}
	
	private Path createRootDirectory() throws IOException {
		return Files.createTempDirectory(null);
	}
}
