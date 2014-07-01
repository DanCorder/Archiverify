//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2014  Daniel Corder (contact: archiverify@dancorder.com)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.dancorder.Archiverify;

import java.io.IOException;

class WarningAction implements Action {

	private final String warning;

	WarningAction(String warning) {
		if (warning == null || warning.equals("")) {
			throw new IllegalArgumentException("Warning cannot be empty");
		}
		
		this.warning = warning;
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
		WarningAction other = (WarningAction) obj;
		if (warning == null) {
			if (other.warning != null)
				return false;
		} else if (!warning.equals(other.warning))
			return false;
		return true;
	}
}
