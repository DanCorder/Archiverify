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
		root1 = path1;
		root2 = path2;
		this.visitor = visitor;
	}
	
	public void walk() throws IOException {
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths);
		
		List<Path> path1Files = getSortedFiles(root1);
		List<Path> path2Files = getSortedFiles(root2);
		
		
		if (path1Files.size() > 0 || path2Files.size() > 0) {
		
			Path relativeFilePath1 = root1.relativize(path1Files.get(0));
			Path relativeFilePath2 = root2.relativize(path2Files.get(0));
			
			if (relativeFilePath1.equals(relativeFilePath2)) {
				visitor.visitFile(relativeFilePath1, FileExistence.BothPaths);
			}
		}
	}
	
	private List<Path> getSortedFiles(Path directory) throws IOException {
		List<Path> ret = new ArrayList<Path>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
			for (Path p : ds) {
				ret.add(p); 
			}
		}
		
		Collections.sort(ret);
		
		return ret;
	}
}
