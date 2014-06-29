package com.dancorder.Archiverify;

import java.io.IOException;

class UpdateHashesAction implements Action {
	private final FileHashStore store;

	UpdateHashesAction(FileHashStore store) {
		if (store == null) {
			throw new IllegalArgumentException("Hash store cannot be null");
		}
		
		this.store = store;
	}
	
	@Override
	public void doAction() throws IOException {
		store.write();
	}
	
	@Override
	public String toString() {
		return String.format("Write hashes to %s", store.getDirectory());
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
