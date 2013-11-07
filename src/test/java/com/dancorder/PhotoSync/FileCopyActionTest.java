package com.dancorder.PhotoSync;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileCopyActionTest {
	private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
	private Path tempFile;
	
	@Before
	public void createTempFile() throws IOException {
		tempFile = Files.createTempFile(null, null);
	}
	
	@After
	public void deleteTempFile() throws IOException {
		Files.delete(tempFile);
		tempFile = null;
	}
	
	@Test (expected = InvalidParameterException.class)
	public void testFromPathNotNull() {
		new FileCopyAction(null, tempFile);
	}
	
	@Test (expected = InvalidParameterException.class)
	public void testToPathNotNull() {
		new FileCopyAction(tempFile, null);
	}
	
	@Test (expected = InvalidParameterException.class)
	public void testFromPathIsAbsolute() {
		Path relativePath = Paths.get("testFile");
		
		new FileCopyAction(relativePath, tempFile);
	}
	
	@Test (expected = InvalidParameterException.class)
	public void testToPathIsAbsolute() {
		Path relativePath = Paths.get("testFile");
		
		new FileCopyAction(tempFile, relativePath);
	}

	@Test
	public void testDoAction() throws IOException {
		Path to = tempDir.resolve("toFile");
		
		FileCopyAction fca = new FileCopyAction(tempFile, to);

		try {
			fca.doAction();
			
			assertTrue(Files.exists(to));
		}
		finally {
			if (Files.exists(to)) {
				Files.delete(to);
			}
		}
	}

	@Test
	public void testToString() {
		Path to = tempDir.resolve("toFile");
		
		FileCopyAction fca = new FileCopyAction(tempFile, to);
		
		String expected = "Copy " + tempFile.toString() + " to " + to.toString();
		
		assertEquals(expected, fca.toString());
	}

}
