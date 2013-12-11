package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class FileHashStore {
	
	private final Dictionary<Path, String> store = new Hashtable<Path, String>();
	
	public FileHashStore(List<String> path1Data, List<String> path2Data) throws IOException {
		parseData(path1Data);
		parseData(path2Data);
	}

	private void parseData(List<String> data) {
		for (String line : data) {
			parseLine(line);
		}
	}

	public List<String> getData() throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		List<Path> paths = Collections.list(store.keys());
		Collections.sort(paths);
		for (Path path : paths) {
			lines.add(createLine(path));
		}
		
		return lines;
	}

	private void parseLine(String line) {
		String[] strings = line.split("\t");
		
		if (strings.length != 2) {
			return;
		}
		
		Path path = Paths.get(strings[1]);
		String hash = strings[0];
		
		storeValues(path, hash);
	}

	private void storeValues(Path path, String hash) {
		if (hashExists(path)) {
			if (!store.get(path).equals(hash)) {
				throw new IllegalArgumentException("Multiple hashes " + hash + " and " + store.get(path) + " exist for " + path.toString());
			}
		}
		else {
			store.put(path, hash);
		}
	}
	
	private String createLine(Path key) {
		return store.get(key) + "\t" + key.toString();
	}
	
	public boolean hashExists(Path filePath) {
		return store.get(filePath) != null;
	}
	
	public String getHash(Path filePath) {
		return store.get(filePath);
	}
}
