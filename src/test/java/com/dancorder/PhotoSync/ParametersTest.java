package com.dancorder.PhotoSync;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ParametersTest {

	@Test (expected = UsageException.class)
	public void testNullParameters() throws UsageException {
		new Parameters(null);
	}
	
	@Test (expected = UsageException.class)
	public void testEmptyParameters() throws UsageException {
		new Parameters(new String[0]);
	}
	
	@Test (expected = UsageException.class)
	public void testSingleParameter() throws UsageException {
		new Parameters(new String[] { "param1" });
	}
	
	@Test (expected = UsageException.class)
	public void testThreeParameters() throws UsageException {
		new Parameters(new String[] { "param1", "param2", "param3" });
	}
	
	@Test (expected = UsageException.class)
	public void testInvalidFirstParameter() throws UsageException {
		String rootPath = File.listRoots()[0].getAbsolutePath();
		
		new Parameters(new String[] { "invalid1", rootPath });
	}
	
	@Test (expected = UsageException.class)
	public void testInvalidSecondParameter() throws UsageException {
		String rootPath = File.listRoots()[0].getAbsolutePath();
		
		new Parameters(new String[] { rootPath, "invalid2" });
	}
	
	@Test
	public void testValidParameters() throws UsageException {
		String rootPath = File.listRoots()[0].getAbsolutePath();
		
		Parameters params = new Parameters(new String[] { rootPath, rootPath });
		
		assertEquals(rootPath, params.getPath1().getAbsolutePath());
		assertEquals(rootPath, params.getPath2().getAbsolutePath());
	}
}
