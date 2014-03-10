package com.dancorder.PhotoSync;

import java.io.IOException;

public class SyncWarningAction implements Action {

	private final String warning;

	SyncWarningAction(String warning) {
		if (warning == null || warning == "") {
			throw new IllegalArgumentException("Warning cannot be empty");
		}
		
		this.warning = "WARNING: " + warning;
	}
	
	@Override
	public void doAction() throws IOException {
		// Nothing to do here.
	}
	
	@Override
	public String toString() {
		return warning;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((warning == null) ? 0 : warning.hashCode());
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
		SyncWarningAction other = (SyncWarningAction) obj;
		if (warning == null) {
			if (other.warning != null)
				return false;
		} else if (!warning.equals(other.warning))
			return false;
		return true;
	}
}
