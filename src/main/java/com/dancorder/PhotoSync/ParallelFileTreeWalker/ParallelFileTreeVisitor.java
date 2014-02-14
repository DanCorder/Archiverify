package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import java.nio.file.Path;

public interface ParallelFileTreeVisitor {
	void preVisitDirectory(Path relativePath, FileExistence existence) throws Exception;
	void visitFile(Path filename, FileExistence existence);
}
