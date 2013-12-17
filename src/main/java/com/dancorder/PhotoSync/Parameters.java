package com.dancorder.PhotoSync;

import java.nio.file.Path;
import java.nio.file.Paths;

class Parameters {

	private final Path path1;
	private final Path path2;
	
	Parameters(String[] args) throws UsageException {
		
		if (args == null || args.length != 2)
		{
			throw new UsageException("Usage: PhotoSync <path1> <path2>");
		}
		
		path1 = Paths.get(args[0]);
		path2 = Paths.get(args[1]);
		
		validatePath(path1);
		validatePath(path2);
	}
	
	private void validatePath(Path path) throws UsageException {
		if (!path.toFile().exists() || !path.toFile().isDirectory()) {
			throw new UsageException("Path must be an existing directory: " + path);
		}		
	}

	Path getPath1() {
		return path1;
	}
	
	Path getPath2() {
		return path2;
	}

}
