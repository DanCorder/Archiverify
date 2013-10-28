package com.dancorder.PhotoSync;

import org.junit.Test;

import com.dancorder.PhotoSync.PhotoSync;
import com.dancorder.PhotoSync.UsageException;

public class PhotoSyncTest {

	@Test(expected = UsageException.class)
	public void testNullParameters() throws UsageException {
		PhotoSync.main(null);
	}
	
	@Test(expected = UsageException.class)
	public void testNoParameters() throws UsageException {
		PhotoSync.main(new String[0]);
	}

}
