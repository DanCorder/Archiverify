package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ParallelFileTreeWalker {
	
	private Path root1;
	private Path root2;
	private IParallelFileTreeVisitor visitor;

	public ParallelFileTreeWalker(Path path1, Path path2, IParallelFileTreeVisitor visitor) {
		root1 = path1;
		root2 = path2;
		this.visitor = visitor;
	}
	
	public void walk() {
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths);
	}
}
