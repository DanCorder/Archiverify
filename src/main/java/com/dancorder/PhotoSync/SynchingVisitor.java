package com.dancorder.PhotoSync;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence;
import com.dancorder.PhotoSync.ParallelFileTreeWalker.ParallelFileTreeVisitor;

class SynchingVisitor implements ParallelFileTreeVisitor {

	private final Path root1;
	private final Path root2;
	private final ArrayList<Action> actions = new ArrayList<Action>();
	private final FileHashGenerator fileHashGenerator;
	private final FileHashStoreFactory fileHashStoreFactory;
	private FileHashStore hashStore1;
	private FileHashStore hashStore2;
	
	SynchingVisitor(FileHashGenerator generator, FileHashStoreFactory factory, Path root1, Path root2) {
		fileHashGenerator = generator;
		fileHashStoreFactory = factory;
		this.root1 = root1;
		this.root2 = root2;
	}

	@Override
	public void preVisitDirectory(Path directoryPath, FileExistence existence) throws Exception {
		if (existence == FileExistence.Path1Only) {
			actions.add(new CreateDirectoryAction(root2.resolve(directoryPath)));
		}
		else if (existence == FileExistence.Path2Only) {
			actions.add(new CreateDirectoryAction(root1.resolve(directoryPath)));
		}

		hashStore1 = fileHashStoreFactory.createFileHashStore(root1.resolve(directoryPath));
		hashStore2 = fileHashStoreFactory.createFileHashStore(root2.resolve(directoryPath));
		actions.add(new UpdateHashesAction(hashStore1, hashStore2));
	}

	@Override
	public void visitFile(Path filePath, FileExistence existence) throws IOException {
		Path absolutePath1 = root1.resolve(filePath);
		Path absolutePath2 = root2.resolve(filePath);
		String hash = "";

		if (existence == FileExistence.Path1Only) {
			actions.add(new FileCopyAction(absolutePath1, absolutePath2));
			hash = fileHashGenerator.calculateMd5(absolutePath1);
		}
		else if (existence == FileExistence.Path2Only) {
			actions.add(new FileCopyAction(absolutePath2, absolutePath1));
			hash = fileHashGenerator.calculateMd5(absolutePath2);
		}
		else {
			hash = fileHashGenerator.calculateMd5(absolutePath1);
		}
		
		hashStore1.addHash(filePath.getFileName(), hash);
		hashStore2.addHash(filePath.getFileName(), hash);
	}
	
	List<Action> getActions() {
		return actions;
	}
}
