package com.dancorder.PhotoSync;

import java.io.File;

class Parameters {

	private final File path1;
	private final File path2;
	
	public Parameters(String[] args) throws UsageException {
		
		if (args == null || args.length != 2)
		{
			throw new UsageException("Usage: PhotoSync <path1> <path2>");
		}
		
		path1 = new File(args[0]);
		path2 = new File(args[1]);
		
		validatePath(path1);
		validatePath(path2);
	}
	
	private void validatePath(File path) throws UsageException {
		if (!path.exists() || !path.isDirectory()) {
			throw new UsageException("Path must be an existing directory: " + path);
		}		
	}

	public File getPath1() {
		return path1;
	}
	
	public File getPath2() {
		return path2;
	}

}
