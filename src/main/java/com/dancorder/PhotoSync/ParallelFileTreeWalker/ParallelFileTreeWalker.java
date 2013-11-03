package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

public class ParallelFileTreeWalker {
	
	private final Path root1;
	private final Path root2;
	private final IParallelFileTreeVisitor visitor;

	public ParallelFileTreeWalker(Path path1, Path path2, IParallelFileTreeVisitor visitor) {
		if (!Files.exists(path1)) {
			throw new IllegalArgumentException("Directory doesn't exist: " + path1.toString());
		}
		if (!Files.exists(path2)) {
			throw new IllegalArgumentException("Directory doesn't exist: " + path2.toString());
		}
		if (visitor == null) {
			throw new IllegalArgumentException("Visitor can't be null");
		}
		
		root1 = path1;
		root2 = path2;
		this.visitor = visitor;
	}
	
	public void walk() throws IOException {
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths);
		
		visitFiles(root1, root2);
	}
	
	private void visitFiles(Path path1, Path path2) throws IOException {
		List<Path> path1Files = getSortedRelativeFilePaths(path1);
		List<Path> path2Files = getSortedRelativeFilePaths(path2);
		
		int path1FileIndex = 0;
		int path2FileIndex = 0;
		
		Path currentPath1File = path1Files.size() > path1FileIndex ? path1Files.get(path1FileIndex) : null;
		Path currentPath2File = path2Files.size() > path2FileIndex ? path2Files.get(path2FileIndex) : null;
		
		while (currentPath1File != null || currentPath2File != null) {
			if (visitOnlyThisFile(currentPath1File, currentPath2File)) {
				visitor.visitFile(currentPath1File, FileExistence.Path1Only);
				currentPath1File = getNextFile(path1Files, ++path1FileIndex);
			}
			else if (visitOnlyThisFile(currentPath2File, currentPath1File)) {
				visitor.visitFile(currentPath2File, FileExistence.Path2Only);
				currentPath2File = getNextFile(path2Files, ++path2FileIndex);
			}
			
			else {
				visitor.visitFile(currentPath1File, FileExistence.BothPaths);
				currentPath1File = getNextFile(path1Files, ++path1FileIndex);
				currentPath2File = getNextFile(path2Files, ++path2FileIndex);
			}
		}
	}
	
	private boolean visitOnlyThisFile(Path thisFile, Path otherFile) {
		return otherFile == null ||
			(thisFile != null && thisFile.compareTo(otherFile) < 0);
	}
	
	private Path getNextFile(List<Path> files, int fileIndex) {
		return files.size() > fileIndex ? files.get(fileIndex) : null;
	}

	private List<Path> getSortedRelativeFilePaths(Path directory) throws IOException {
		List<Path> ret = getContainedFilesRelativePaths(directory);
		
		Collections.sort(ret);
		
		return ret;
	}
	
	private List<Path> getContainedFilesRelativePaths(Path directory) throws IOException {
	    
	    List<Path> ret = new ArrayList<Path>();
	    
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, new fileOnlyFilter())) {
			for (Path path : directoryStream) {
				ret.add(directory.relativize(path)); 
			}
		}
	    
	    return ret;
	}
	
	private class fileOnlyFilter implements DirectoryStream.Filter<Path> {
		@Override
		public boolean accept(Path file) throws IOException {
			return (!Files.isDirectory(file));
		}
	}
}
