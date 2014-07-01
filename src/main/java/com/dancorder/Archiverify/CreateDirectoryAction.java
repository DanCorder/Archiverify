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
import java.nio.file.Files;
import java.nio.file.Path;

class CreateDirectoryAction implements Action {

	private final Path directory;
	
	CreateDirectoryAction(Path directory) {
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
