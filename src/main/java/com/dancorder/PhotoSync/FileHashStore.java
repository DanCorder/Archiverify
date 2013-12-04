package com.dancorder.PhotoSync;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class FileHashStore {
	
	private final Dictionary<Path, String> store = new Hashtable<Path, String>();
	
	public FileHashStore(Reader path1Data, Reader path2Data) throws IOException {
		parseData(path1Data);
		parseData(path2Data);
	}

	private void parseData(Reader data) {
		try (Scanner scanner = new Scanner(data)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				parseLine(line);
			}
		}
	}

	public void write(Writer target) throws IOException {
		PrintWriter writer = new PrintWriter(target);
		List<Path> paths = Collections.list(store.keys());
		Collections.sort(paths);
		for (Path path : paths) {
			writer.println(createLine(path));
		}
	}

	private void parseLine(String line) {
		String[] strings = line.split("\t");
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
