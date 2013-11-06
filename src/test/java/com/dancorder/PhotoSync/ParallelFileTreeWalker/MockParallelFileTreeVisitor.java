package com.dancorder.PhotoSync.ParallelFileTreeWalker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MockParallelFileTreeVisitor implements ParallelFileTreeVisitor {

	private List<MethodCall> methodCalls = new ArrayList<MethodCall>();
	
	public MockParallelFileTreeVisitor() {
	}

	@Override
	public void preVisitDirectory(Path relativePath, FileExistence existence) {
		methodCalls.add(new MethodCallPreVisitDirectory(relativePath, existence));
	}

	@Override
	public void visitFile(Path filename, FileExistence existence) {
		methodCalls.add(new MethodCallVisitFile(filename, existence));
	}
	
	public List<MethodCall> getCalls() {
		return methodCalls;
	}

}
