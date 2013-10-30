package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import java.nio.file.Path;

public interface IParallelFileTreeVisitor {
	void preVisitDirectory(Path relativePath, FileExistence existence);
	void visitFile(Path filename, FileExistence existence);
}
