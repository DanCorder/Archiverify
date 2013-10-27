package com.dancorder.PhotoSync.test;

import org.junit.Test;

import com.dancorder.PhotoSync.PhotoSync;
import com.dancorder.PhotoSync.UsageException;

public class PhotoSyncTest {

	@Test(expected = UsageException.class)
	public void testNoParameters() throws UsageException {
		PhotoSync.main(null);
	}

}
