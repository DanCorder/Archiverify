package com.dancorder.Archiverify.ParallelFileTreeWalker;

import java.nio.file.Path;

public interface ParallelFileTreeVisitor {
	void preVisitDirectory(Path relativePath, FileExistence existence);
	void visitFile(Path filename, FileExistence existence);
	void postVisitDirectory(Path relativePath, FileExistence existence);
}
