package com.dancorder.PhotoSync;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

import org.junit.Test;

public class CreateDirectoryActionTest {
	private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

	@Test (expected = InvalidParameterException.class)
	public void testDirectoryNotNull() {
		new CreateDirectoryAction(null);
	}
	
	@Test (expected = InvalidParameterException.class)
	public void testDirectoryIsAbsolute() {
		Path directoryPath = Paths.get("testDirectory");
		
		new CreateDirectoryAction(directoryPath);
	}
	
	@Test
	public void testDescription() {
		Path directoryPath = tempDir.resolve("testDirectory");
		
		Action cda = new CreateDirectoryAction(directoryPath);
		
		String expected = "Create directory: " + directoryPath.toString();
		
		assertEquals(expected, cda.toString());
	}
	
	@Test
	public void testAction() throws IOException {
		Path directoryPath = tempDir.resolve("testDirectory");
		
		Action cda = new CreateDirectoryAction(directoryPath);
		
		try {
			cda.doAction();
			
			assertTrue(Files.exists(directoryPath));
		}
		finally {
			if (directoryPath.toFile().exists()) {
				directoryPath.toFile().delete();
			}
		}
	}

}
