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
	private final HashFileSource source1;
	private final HashFileSource source2;

	FileHashStore(HashFileSource path1Source, HashFileSource path2Source) throws Exception {
		source1 = path1Source;
		source2 = path2Source;
		parseData(source1);
		parseData(source2);
	}

	void write() throws IOException {
		List<String> lines = getFileData();

		source1.writeData(lines);
		source2.writeData(lines);
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

		storeValues(path, hash);
	}

	private void storeValues(Path path, String hash) throws Exception {
		if (hashExists(path)) {
			if (!store.get(path).equals(hash)) {
				throw new Exception("Multiple hashes " + hash + " and " + store.get(path) + " exist for " + path.toString());
			}
		}
		else {
			store.put(path, hash);
		}
	}

	private String createLine(Path key) {
		return store.get(key) + "\t" + key.toString();
	}

	boolean hashExists(Path filePath) {
		return store.get(filePath) != null;
	}

	String getHash(Path filePath) {
		return store.get(filePath);
	}
}
