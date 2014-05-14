package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;


class HashCheckingVisitor implements FileVisitor<Path> {
	
	private final FileHashStoreFactory factory;
	private final FileHashGenerator hashGenerator;
	
	private ArrayList<Action> actions = new ArrayList<Action>();
	private Dictionary<Path, FileHashStore> stores = new Hashtable<Path, FileHashStore>();

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
			stores.put(path, factory.createFileHashStore(path));
		} catch (Exception e) {
			stores.remove(path);
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
		
		FileHashStore store = stores.get(path);
		if (store != null && store.isDirty()) {
			actions.add(new UpdateHashesAction(store));
		}
		
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		FileHashStore store = stores.get(path.getParent());
		if (store != null && !HashFileSource.isHashFile(path)) {
			String calculatedHash = hashGenerator.calculateMd5(path);
			Path fileName = path.getFileName();
			
			if (!store.hashExists(fileName)) {
				store.setHash(fileName, calculatedHash);
			}
			else if (!store.getHash(fileName).equals(calculatedHash)) {
				actions.add(
						new WarningAction(
							String.format("Calculard hash (%s) for file %s does not match stored value (%s)", calculatedHash, path, store.getHash(fileName))));
			}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
		actions.add(
				new WarningAction(
					String.format("Couldn't visit file %s this file will be ignored. %s", path, e)));
		
		return FileVisitResult.CONTINUE;
	}
}
