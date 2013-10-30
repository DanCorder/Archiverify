package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import java.nio.file.Path;

public class MethodCallPreVisitDirectory extends MethodCall {
	private Path relativePath;
	private FileExistence existence;

	public MethodCallPreVisitDirectory(Path relativePath, FileExistence existence) {
		this.relativePath = relativePath;
		this.existence = existence;
	}
	
	public Path getRelativePath() {
		return relativePath;
	}
	
	public FileExistence getExistence() {
		return existence;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((existence == null) ? 0 : existence.hashCode());
		result = prime * result
				+ ((relativePath == null) ? 0 : relativePath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodCallPreVisitDirectory other = (MethodCallPreVisitDirectory) obj;
		if (existence != other.existence)
			return false;
		if (relativePath == null) {
			if (other.relativePath != null)
				return false;
		} else if (!relativePath.equals(other.relativePath))
			return false;
		return true;
	}
}
