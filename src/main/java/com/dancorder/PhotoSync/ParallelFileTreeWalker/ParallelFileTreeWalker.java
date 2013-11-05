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
		Path emptyPath = Paths.get("");
		visitor.preVisitDirectory(emptyPath, FileExistence.BothPaths);
		walkRecursive(emptyPath, emptyPath);
	}
	
	public void walkRecursive(Path relativeDirectory1, Path relativeDirectory2) throws IOException {
		
		Path absoluteDirectoryPath1 = getAbsolutePath(root1, relativeDirectory1);
		Path absoluteDirectoryPath2 = getAbsolutePath(root2, relativeDirectory2);
		
		visitPath(getSortedRelativeFilePaths(absoluteDirectoryPath1), getSortedRelativeFilePaths(absoluteDirectoryPath2), new FileProcessor()); 		
		visitPath(getSortedRelativeDirectories(absoluteDirectoryPath1), getSortedRelativeDirectories(absoluteDirectoryPath2), new DirectoryProcessor());
	}
	
	private void visitPath(List<Path> root1Paths, List<Path> root2Paths, PathProcessor processor) throws IOException {
		
		int root1PathIndex = 0;
		int root2PathIndex = 0;
		
		Path currentRoot1Path = getNextFile(root1Paths, root1PathIndex);
		Path currentRoot2Path = getNextFile(root2Paths, root2PathIndex);
		
		while (currentRoot1Path != null || currentRoot2Path != null) {
			if (visitOnlyThisFile(currentRoot1Path, currentRoot2Path)) {
				processor.process(currentRoot1Path, null);
				currentRoot1Path = getNextFile(root1Paths, ++root1PathIndex);
			}
			else if (visitOnlyThisFile(currentRoot2Path, currentRoot1Path)) {
				processor.process(null, currentRoot2Path);
				currentRoot2Path = getNextFile(root2Paths, ++root2PathIndex);
			}
			
			else {
				processor.process(currentRoot1Path, currentRoot2Path);
				currentRoot1Path = getNextFile(root1Paths, ++root1PathIndex);
				currentRoot2Path = getNextFile(root2Paths, ++root2PathIndex);
			}
		}
	}
	
	private Path getAbsolutePath(Path root, Path relativePath) {
		return relativePath == null ? null : root.resolve(relativePath);
	}
	
	private boolean visitOnlyThisFile(Path thisFile, Path otherFile) {
		return otherFile == null ||
			(thisFile != null && thisFile.compareTo(otherFile) < 0);
	}
	
	private Path getNextFile(List<Path> files, int fileIndex) {
		return files.size() > fileIndex ? files.get(fileIndex) : null;
	}

	private List<Path> getSortedRelativeFilePaths(Path directory) throws IOException {
		if (directory == null) return new ArrayList<Path>();
		
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
	
	private List<Path> getSortedRelativeDirectories(Path directory) throws IOException {
		if (directory == null) return new ArrayList<Path>();
		
		List<Path> ret = getSubDirectoryRelativePaths(directory);
		
		Collections.sort(ret);
		
		return ret;
	}
	
	private List<Path> getSubDirectoryRelativePaths(Path directory) throws IOException {
	    List<Path> ret = new ArrayList<Path>();
	    
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, new directoryOnlyFilter())) {
			for (Path path : directoryStream) {
				ret.add(directory.relativize(path)); 
			}
		}
	    
	    return ret;
	}
	
	private abstract class PathProcessor {
		public abstract void process(Path relativePath1, Path relativePath2) throws IOException;
		
		protected Path getPath(Path relativePath1, Path relativePath2) {
			return relativePath1 != null ? relativePath1 : relativePath2;
		}
		
		protected FileExistence getExistence(Path relativePath1, Path relativePath2) {
			FileExistence ret = FileExistence.Path1Only;
			if (relativePath1 == null) {
				ret = FileExistence.Path2Only;
			}
			else if (relativePath2 != null) {
				ret = FileExistence.BothPaths;
			}
			return ret;
		}
	}
	
	private class FileProcessor extends PathProcessor {
		@Override
		public void process(Path relativePath1, Path relativePath2) {
			visitor.visitFile(getPath(relativePath1, relativePath2), getExistence(relativePath1, relativePath2));
		}
		
	};
	
	private class DirectoryProcessor extends PathProcessor {
		@Override
		public void process(Path relativePath1, Path relativePath2) throws IOException {
			visitor.preVisitDirectory(getPath(relativePath1, relativePath2), getExistence(relativePath1, relativePath2));
			walkRecursive(relativePath1, relativePath2);
		}
	};
	
	private class fileOnlyFilter implements DirectoryStream.Filter<Path> {
		@Override
		public boolean accept(Path file) throws IOException {
			return (!Files.isDirectory(file));
		}
	}
	
	private class directoryOnlyFilter implements DirectoryStream.Filter<Path> {
		@Override
		public boolean accept(Path file) throws IOException {
			return (Files.isDirectory(file));
		}
	}
}
