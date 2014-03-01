package com.dancorder.PhotoSync;

import java.io.IOException;

class UpdateHashesAction implements Action {
	private final FileHashStore store1;
	private final FileHashStore store2;

	UpdateHashesAction(FileHashStore hashes1, FileHashStore hashes2) {
		if (hashes1 == null) {
			throw new IllegalArgumentException("Hash store cannot be null");
		}
		if (hashes2 == null) {
			throw new IllegalArgumentException("Hash store cannot be null");
		}
		
		store1 = hashes1;
		store2 = hashes2;
	}
	
	@Override
	public void doAction() throws IOException {
		store1.write();
		store2.write();
	}
	
	@Override
	public String toString() {
		return "Write hashes to " + store1.getDirectory().toString() + " and " + store2.getDirectory().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((store1 == null) ? 0 : store1.hashCode());
		result = prime * result + ((store2 == null) ? 0 : store2.hashCode());
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
		if (store1 == null) {
			if (other.store1 != null)
				return false;
		} else if (!store1.equals(other.store1))
			return false;
		if (store2 == null) {
			if (other.store2 != null)
				return false;
		} else if (!store2.equals(other.store2))
			return false;
		return true;
	}
}
