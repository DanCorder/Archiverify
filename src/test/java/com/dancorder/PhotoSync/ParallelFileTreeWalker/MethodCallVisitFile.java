package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import java.nio.file.Path;

public class MethodCallVisitFile extends MethodCall {
	private Path filename;
	private FileExistence existence;

	public MethodCallVisitFile(Path filename, FileExistence existence) {
		this.filename = filename;
		this.existence = existence;
	}
	
	public Path getRelativePath() {
		return filename;
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
				+ ((filename == null) ? 0 : filename.hashCode());
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
		MethodCallVisitFile other = (MethodCallVisitFile) obj;
		if (existence != other.existence)
			return false;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}
}
