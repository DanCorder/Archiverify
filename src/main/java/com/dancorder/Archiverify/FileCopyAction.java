package com.dancorder.Archiverify;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class FileCopyAction implements Action {

	protected final Path from;
	protected final Path to;
	protected final String goodHash;
	protected final FileHashGenerator hashGenerator;
	
	FileCopyAction(Path from, Path to, String goodHash, FileHashGenerator hashGenerator) {
		if (from == null) {
			throw new IllegalArgumentException("From path must not be null");
		}
		if (to == null) {
			throw new IllegalArgumentException("To path must not be null");
		}
		if (goodHash == null) {
			throw new IllegalArgumentException("Hash must not be null");
		}
		if (hashGenerator == null) {
			throw new IllegalArgumentException("Hash generator must not be null");
		}
		if (!from.isAbsolute()) {
			throw new IllegalArgumentException("From path must be absolute: " + from.toString());
		}
		if (!to.isAbsolute()) {
			throw new IllegalArgumentException("To path must be absolute: " + to.toString());
		}
		
		this.from = from;
		this.to = to;
		this.goodHash = goodHash;
		this.hashGenerator = hashGenerator;
	}
	
	@Override
	public void doAction() throws Exception {
		Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
		
		String calculatedHash = hashGenerator.calculateMd5(to);
		
		if (!goodHash.equals(calculatedHash)) {
			throw new Exception(String.format(
					"File copy failed. Hash for file copied to %s (%s) doesn't match source hash (%s)",
					to,
					calculatedHash,
					goodHash));
		}
	}
	
	@Override
	public String toString() {
		return String.format("Copy %s to %s with hash %s", from, to, goodHash);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result
				+ ((goodHash == null) ? 0 : goodHash.hashCode());
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
		if (goodHash == null) {
			if (other.goodHash != null)
				return false;
		} else if (!goodHash.equals(other.goodHash))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}
}
