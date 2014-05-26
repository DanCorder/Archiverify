package com.dancorder.PhotoSync;

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
		return isDirty;
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
