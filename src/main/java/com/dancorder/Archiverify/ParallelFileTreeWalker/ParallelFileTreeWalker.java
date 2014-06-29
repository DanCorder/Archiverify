package com.dancorder.Archiverify.ParallelFileTreeWalker;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParallelFileTreeWalker {
	
	private final Path root1;
	private final Path root2;
	private final ParallelFileTreeVisitor visitor;

	public ParallelFileTreeWalker(Path path1, Path path2, ParallelFileTreeVisitor visitor) {
		if (path1 == null) {
			throw new IllegalArgumentException("Path 1 can't be null");
		}
		if (path2 == null) {
			throw new IllegalArgumentException("Path 2 can't be null");
		}
		if (visitor == null) {
			throw new IllegalArgumentException("Visitor can't be null");
		}
		if (!Files.exists(path1)) {
			throw new IllegalArgumentException("Directory doesn't exist: " + path1.toString());
		}
		if (!Files.exists(path2)) {
			throw new IllegalArgumentException("Directory doesn't exist: " + path2.toString());
		}
		
		
		root1 = path1;
		root2 = path2;
		this.visitor = visitor;
	}
	
	public void walk() throws Exception {
		Path emptyPath = Paths.get("");
		visitor.preVisitDirectory(emptyPath, FileExistence.BothPaths);
		walkRecursive(emptyPath, emptyPath);
		visitor.postVisitDirectory(emptyPath, FileExistence.BothPaths);
	}
	
	private void walkRecursive(Path relativeDirectory1, Path relativeDirectory2) throws Exception {
		
		Path absoluteDirectoryPath1 = getAbsolutePath(root1, relativeDirectory1);
		Path absoluteDirectoryPath2 = getAbsolutePath(root2, relativeDirectory2);
		
		visitPaths(
			getSortedRelativePaths(absoluteDirectoryPath1, root1, new fileOnlyFilter()),
			getSortedRelativePaths(absoluteDirectoryPath2, root2, new fileOnlyFilter()),
			new FileProcessor());

		visitPaths(
			getSortedRelativePaths(absoluteDirectoryPath1, root1, new directoryOnlyFilter()),
			getSortedRelativePaths(absoluteDirectoryPath2, root2, new directoryOnlyFilter()),
			new DirectoryProcessor());
	}
	
	private void visitPaths(List<Path> root1Paths, List<Path> root2Paths, PathProcessor processor) throws Exception {
		
		int root1PathIndex = 0;
		int root2PathIndex = 0;
		
		Path currentRoot1Path = getNextPath(root1Paths, root1PathIndex);
		Path currentRoot2Path = getNextPath(root2Paths, root2PathIndex);
		
		while (currentRoot1Path != null || currentRoot2Path != null) {
			if (visitOnlyThisPath(currentRoot1Path, currentRoot2Path)) {
				processor.process(currentRoot1Path, null);
				currentRoot1Path = getNextPath(root1Paths, ++root1PathIndex);
			}
			else if (visitOnlyThisPath(currentRoot2Path, currentRoot1Path)) {
				processor.process(null, currentRoot2Path);
				currentRoot2Path = getNextPath(root2Paths, ++root2PathIndex);
			}
			else {
				processor.process(currentRoot1Path, currentRoot2Path);
				currentRoot1Path = getNextPath(root1Paths, ++root1PathIndex);
				currentRoot2Path = getNextPath(root2Paths, ++root2PathIndex);
			}
		}
	}
	
	private Path getAbsolutePath(Path root, Path relativePath) {
		return relativePath == null ? null : root.resolve(relativePath);
	}
	
	private boolean visitOnlyThisPath(Path thisFile, Path otherFile) {
		return otherFile == null ||
			(thisFile != null && thisFile.compareTo(otherFile) < 0);
	}
	
	private Path getNextPath(List<Path> files, int fileIndex) {
		return files.size() > fileIndex ? files.get(fileIndex) : null;
	}
	
	private List<Path> getSortedRelativePaths(Path directory, Path root, DirectoryStream.Filter<Path> filter) throws IOException {
		if (directory == null) return new ArrayList<Path>();
		
		List<Path> ret = getRelativePaths(directory, root, filter);
		
		Collections.sort(ret);
		
		return ret;
	}
	
	private List<Path> getRelativePaths(Path directory, Path root, DirectoryStream.Filter<Path> filter) throws IOException {
	    List<Path> ret = new ArrayList<Path>();
	    
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, filter)) {
			for (Path path : directoryStream) {
				ret.add(root.relativize(path));
			}
		}
	    
	    return ret;
	}
	
	private abstract class PathProcessor {
		public abstract void process(Path relativePath1, Path relativePath2) throws Exception;
		
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
		public void process(Path relativePath1, Path relativePath2) throws IOException {
			visitor.visitFile(getPath(relativePath1, relativePath2), getExistence(relativePath1, relativePath2));
		}
		
	};
	
	private class DirectoryProcessor extends PathProcessor {
		@Override
		public void process(Path relativePath1, Path relativePath2) throws Exception {
			Path path = getPath(relativePath1, relativePath2);
			FileExistence existence = getExistence(relativePath1, relativePath2);
			visitor.preVisitDirectory(path, existence);
			walkRecursive(relativePath1, relativePath2);
			visitor.postVisitDirectory(path, existence);
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
