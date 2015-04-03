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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

class FileHashStore {

	private final Dictionary<Path, String> store = new Hashtable<Path, String>();
	private final HashFileSource source;
	private boolean isDirty;

	FileHashStore(HashFileSource source) throws Exception {
		this.source = source;
		parseData(source);
		isDirty = false;
	}
	
	Path getDirectory() {
		return source.getDirectory();
	}

	boolean hashExists(Path fileName) {
		return getHash(fileName) != null;
	}

	String getHash(Path fileName) {
		return store.get(fileName);
	}

	void setHash(Path fileName, String hash) {
		String currentValue = store.get(fileName);
		if (currentValue == null || !currentValue.equals(hash)) {
			store.put(fileName, hash);
			isDirty = true;
		}
	}
	
	void removeHash(Path filename) {
		if (store.get(filename) != null) {
			store.remove(filename);
			isDirty = true;
		}
	}

	void write() throws IOException {
		source.writeData(getFileData());
	}
	
	boolean isDirty() {
		return isDirty || source.requiresRewriting();
	}
	
	List<Path> getFiles() {
		return Collections.list(store.keys());
	}

	private List<String> getFileData() {
		ArrayList<String> lines = new ArrayList<String>();
		List<Path> paths = Collections.list(store.keys());
		Collections.sort(paths);
		for (Path path : paths) {
			lines.add(createLine(path));
		}

		return lines;
	}

	private void parseData(HashFileSource source) throws Exception {
		List<String> data = source.getData();
		for (String line : data) {
			parseLine(line);
		}
	}

	private void parseLine(String line) throws Exception {
		String[] strings = line.split("\t");

		if (strings.length != 2) {
			return;
		}

		Path path = Paths.get(strings[1]);
		String hash = strings[0];

		storeValue(path, hash);
	}

	private void storeValue(Path path, String hash) throws Exception {
		if (hashExists(path)) {
			if (!store.get(path).equals(hash)) {
				throw new Exception("Multiple hashes " + hash + " and " + store.get(path) + " exist for " + path.toString());
			}
		}
		else {
			setHash(path, hash);
		}
	}

	private String createLine(Path key) {
		return store.get(key) + "\t" + key.toString();
	}
}
