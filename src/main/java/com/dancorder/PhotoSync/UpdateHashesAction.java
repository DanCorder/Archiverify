package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class UpdateHashesAction implements Action {
	private final FileHashStore store;

	UpdateHashesAction(FileHashStore hashes) {
		if (hashes == null) {
			throw new IllegalArgumentException("Hash store cannot be null");
		}
		
		store = hashes;
	}
	
	@Override
	public void doAction() throws IOException {
		store.write();
	}
	
	@Override
	public String toString() {
		List<Path> directories = store.getDirectories();
		return "Write hashes to " + directories.get(0).toString() + " and " + directories.get(1).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((store == null) ? 0 : store.hashCode());
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
		UpdateHashesAction other = (UpdateHashesAction) obj;
		if (store == null) {
			if (other.store != null)
				return false;
		} else if (!store.equals(other.store))
			return false;
		return true;
	}
}
