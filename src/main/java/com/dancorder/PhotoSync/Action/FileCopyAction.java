package com.dancorder.PhotoSync.Action;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;

public class FileCopyAction implements Action {

	private final Path from;
	private final Path to;
	
	public FileCopyAction(Path from, Path to) {
		if (from == null) {
			throw new InvalidParameterException("From path must not be null");
		}
		if (to == null) {
			throw new InvalidParameterException("To path must not be null");
		}
		if (!from.isAbsolute()) {
			throw new InvalidParameterException("From path must be absolute: " + from.toString());
		}
		if (!to.isAbsolute()) {
			throw new InvalidParameterException("To path must be absolute: " + to.toString());
		}
		
		this.from = from;
		this.to = to;
	}
	
	@Override
	public void doAction() throws IOException {
		Files.copy(from, to);
	}
	
	@Override
	public String toString() {
		return "Copy " + from.toString() + " to " + to.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		FileCopyAction other = (FileCopyAction) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

}
