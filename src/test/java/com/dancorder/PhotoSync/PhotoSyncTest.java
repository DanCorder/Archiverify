package com.dancorder.PhotoSync;

import java.io.IOException;

import org.junit.Test;

import com.dancorder.PhotoSync.PhotoSync;
import com.dancorder.PhotoSync.UsageException;

public class PhotoSyncTest {

	@Test(expected = UsageException.class)
	public void testNullParameters() throws UsageException, IOException {
		PhotoSync.main(null);
	}
	
	@Test(expected = UsageException.class)
	public void testNoParameters() throws UsageException, IOException {
		PhotoSync.main(new String[0]);
	}

}
