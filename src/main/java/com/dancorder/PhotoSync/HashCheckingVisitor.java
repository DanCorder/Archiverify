package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;


class HashCheckingVisitor implements FileVisitor<Path> {
	
	private final FileHashStoreFactory factory;
	private final FileHashGenerator hashGenerator;
	
	private ArrayList<Action> actions = new ArrayList<Action>();
	private FileHashStore store;

	HashCheckingVisitor(FileHashStoreFactory factory, FileHashGenerator hashGenerator) {
		this.factory = factory;
		this.hashGenerator = hashGenerator;
	}
	
	List<Action> getActions() {
		return actions;
	}
	
	@Override
	public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
		if (e != null) {
			actions.add(
					new WarningAction(
						String.format("Directory %s not fully scanned, some files may not have hashes and some subdirectories may not be scanned. %s", path, e)));
		}
		
		if (store != null && store.isDirty()) {
			actions.add(new UpdateHashesAction(store));
		}
		
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
		try {
			store = factory.createFileHashStore(path);
		} catch (Exception e) {
			store = null;
			actions.add(
					new WarningAction(
						String.format("Error caught visiting directory %s this directory will be ignored. %s", path, e)));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		if (store != null) {
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
