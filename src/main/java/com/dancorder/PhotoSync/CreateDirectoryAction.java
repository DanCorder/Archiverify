package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateDirectoryAction implements Action {

	private final Path directory;
	
	public CreateDirectoryAction(Path directory) {
		if (directory == null) {
			throw new IllegalArgumentException("Directory cannot be null");
		}
		if (!directory.isAbsolute()) {
			throw new IllegalArgumentException("Path must be absloute");
		}
		
		this.directory = directory;
	}
	
	@Override
	public String toString() {
		return "Create directory: " + directory.toString();
	}
	
	@Override
	public void doAction() throws IOException {
		Files.createDirectories(directory);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((directory == null) ? 0 : directory.hashCode());
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
		CreateDirectoryAction other = (CreateDirectoryAction) obj;
		if (directory == null) {
			if (other.directory != null)
				return false;
		} else if (!directory.equals(other.directory))
			return false;
		return true;
	}
}
