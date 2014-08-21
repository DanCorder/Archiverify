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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


class HashCheckingVisitor implements FileVisitor<Path> {
	
	private final FileHashStoreFactory factory;
	private final FileHashGenerator hashGenerator;
	
	private ArrayList<Action> actions = new ArrayList<Action>();
	private Dictionary<Path, FileHashStore> hashStoresByDirectory = new Hashtable<Path, FileHashStore>();
	private Dictionary<Path, Set<Path>> visitedFilesByDirectory = new Hashtable<Path, Set<Path>>();

	HashCheckingVisitor(FileHashStoreFactory factory, FileHashGenerator hashGenerator) {
		this.factory = factory;
		this.hashGenerator = hashGenerator;
	}
	
	List<Action> getActions() {
		return actions;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
		try {
			Logger.log("Scanning " + path.toString());
			visitedFilesByDirectory.put(path, new HashSet<Path>());
			hashStoresByDirectory.put(path, factory.createFileHashStore(path));
		} catch (Exception e) {
			hashStoresByDirectory.remove(path);
			actions.add(
					new WarningAction(
						String.format("Error caught visiting directory %s this directory will be ignored. %s", path, e)));
		}
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
		if (e != null) {
			actions.add(
					new WarningAction(
						String.format("Directory %s not fully scanned, some files may not have hashes and some subdirectories may not be scanned. %s", path, e)));
		}
		
		FileHashStore store = hashStoresByDirectory.get(path);
		
		if (store != null) {
			removeHashesForNonExistantFiles(store, visitedFilesByDirectory.get(path));
			if (store.isDirty()) {
				actions.add(new UpdateHashesAction(store));
			}
		}
		
		visitedFilesByDirectory.remove(path);
		hashStoresByDirectory.remove(path);
		
		return FileVisitResult.CONTINUE;
	}

	private void removeHashesForNonExistantFiles(FileHashStore store, Set<Path> filesInDirectory) {
		List<Path> filesInStore = store.getFiles();
		
		Set<Path> filesOnlyInStore = new HashSet<Path>();

		for (Path fileInStore : filesInStore) {
			if (!filesInDirectory.contains(fileInStore)) {
				filesOnlyInStore.add(fileInStore);
			}
		}
		
		for (Path fileOnlyInStore : filesOnlyInStore) {
			store.removeHash(fileOnlyInStore);
		}
	}

	@Override
	public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
		if (factory.isHashFile(filePath)) {
			return FileVisitResult.CONTINUE;
		}
		
		FileHashStore store = hashStoresByDirectory.get(filePath.getParent());
		
		if (store != null) {			
			calculateAndStoreHash(filePath, store);
		}
		
		recordFileWasVisited(filePath);
		
		return FileVisitResult.CONTINUE;
	}

	private void calculateAndStoreHash(Path path, FileHashStore store) throws IOException {
		String calculatedHash = hashGenerator.calculateMd5(path);
		Path fileName = path.getFileName();
		
		if (!store.hashExists(fileName)) {
			store.setHash(fileName, calculatedHash);
		}
		else if (!store.getHash(fileName).equals(calculatedHash)) {
			actions.add(
					new WarningAction(
						String.format("Calculated hash (%s) for file %s does not match stored value (%s)", calculatedHash, path, store.getHash(fileName))));
		}
	}

	private void recordFileWasVisited(Path path) {
		visitedFilesByDirectory.get(path.getParent()).add(path.getFileName());
	}

	@Override
	public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
		actions.add(
				new WarningAction(
					String.format("Couldn't visit file %s this file will be ignored. %s", path, e)));
		
		return FileVisitResult.CONTINUE;
	}
}
