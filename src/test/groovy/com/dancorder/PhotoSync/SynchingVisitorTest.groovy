package com.dancorder.PhotoSync

import java.nio.file.Paths

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence


class SynchingVisitorTest extends spock.lang.Specification {

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static root1Absolute = tempDir.resolve("testRoot1")
	private final static root2Absolute = tempDir.resolve("testRoot2")
	private final static subDirRelative = Paths.get("subDir")
	private final static subDirAbsoluteRoot1 = root1Absolute.resolve(subDirRelative)
	private final static subDirAbsoluteRoot2 = root2Absolute.resolve(subDirRelative)
	private final static subSubDirRelative = subDirRelative.resolve("subSubDir")
	private final static subDir2Relative = Paths.get("subDir2")
	private final static fileRelative = Paths.get("rootFile")
	private final static subDirFileRelative = subDirRelative.resolve("subDirFile")
	private final static subDir2FileRelative = subDir2Relative.resolve("subDir2File")
	private final static subSubDirFileRelative = subSubDirRelative.resolve("subSubDirFile")
	private static store1
	private static store2
	private static defaultFileHashStoreFactory
	
	def setup() {
		store1 = Mock(FileHashStore)
		store2 = Mock(FileHashStore)
		defaultFileHashStoreFactory = Mock(FileHashStoreFactory)
		defaultFileHashStoreFactory.createFileHashStore(_) >>> [store1, store2]
	}
	
	def "Actions returned correctly for compare directories call"() {
		setup:
		def action = Mock(Action)
		def action2 = Mock(Action)
		def actions = new ArrayList<Action>()
		actions.add(action)
		actions.add(action2)
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> actions
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		
		then:
		visitor.getActions().size() == 3
		visitor.getActions()[0] == action
		visitor.getActions()[1] == action2
		visitor.getActions()[2] == new UpdateHashesAction(store1, store2)
	}
	
	def "Hashes updated when visiting directory"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> new ArrayList<Action>()
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		
		then:
		visitor.getActions().size() == 1
		visitor.getActions()[0] == new UpdateHashesAction(store1, store2)
	}
	
	def "Hashes updated after directory synching"() {
		setup:
		def action = Mock(Action)
		def actions = new ArrayList<Action>()
		actions.add(action)
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> actions
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		
		then:
		visitor.getActions().size() == 2
		visitor.getActions()[0] == action
		visitor.getActions()[1] == new UpdateHashesAction(store1, store2)
	}
	
	def "Parameters passed correctly to compare directories for root directory"() {
		setup:
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		
		then:
		1 * logic.compareDirectories(root1Absolute, root2Absolute, FileExistence.BothPaths) >> new ArrayList<Action>()
	}
	
	def "Parameters passed correctly to compare directories for sub directories"() {
		setup:
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(subDirRelative, existence)
		
		then:
		1 * logic.compareDirectories(subDirAbsoluteRoot1, subDirAbsoluteRoot2, existence) >> new ArrayList<Action>()
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
	
	def "Logic exception in directory comparison converted to warning"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_,_,_) >> { throw new NullPointerException("Test exception") }
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		def result = visitor.getActions()
		
		then:
		result.size() == 1
		result[0] instanceof WarningAction
	}
	
	def "Logic exception in directory comparison stops processing of children but not siblings"() {
		setup:
		def logic = Mock(SyncLogic)		
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subDirFileRelative, FileExistence.BothPaths)
		visitor.preVisitDirectory(subSubDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subSubDirFileRelative, FileExistence.BothPaths)
		visitor.preVisitDirectory(subDir2Relative, FileExistence.BothPaths)
		visitor.visitFile(subDir2FileRelative, FileExistence.BothPaths)
		
		then:
		1 * logic.compareDirectories(subDirAbsoluteRoot1,subDirAbsoluteRoot2,_) >> { throw new NullPointerException("Test exception") }
		0 * logic.compareFiles(root1Absolute.resolve(subDirFileRelative), _ , root2Absolute.resolve(subDirFileRelative), _) >> new ArrayList<Action>()
		0 * logic.compareDirectories(root1Absolute.resolve(subSubDirRelative) ,root2Absolute.resolve(subSubDirRelative), _) >> new ArrayList<Action>()
		0 * logic.compareFiles(root1Absolute.resolve(subSubDirFileRelative), _ , root2Absolute.resolve(subSubDirFileRelative), _) >> new ArrayList<Action>()
		1 * logic.compareDirectories(root1Absolute.resolve(subDir2Relative), root2Absolute.resolve(subDir2Relative), _) >> new ArrayList<Action>()
		1 * logic.compareFiles(root1Absolute.resolve(subDir2FileRelative), _ , root2Absolute.resolve(subDir2FileRelative), _) >> new ArrayList<Action>()
	}
	
	def "Logic exception in file comparison converted to warning"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_,_,_) >> new ArrayList<Action>()
		logic.compareFiles(_,_,_,_) >> { throw new NullPointerException("Test exception") }
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(fileRelative, FileExistence.BothPaths)
		def result = visitor.getActions()
		
		then:
		result.size() == 2
		result[0] instanceof UpdateHashesAction
		result[1] instanceof WarningAction
	}
	
	def "Actions returned correctly for compare files call"() {
		setup:
		def action = Mock(Action)
		def action2 = Mock(Action)
		def actions = new ArrayList<Action>()
		actions.add(action)
		actions.add(action2)
		def logic = Mock(SyncLogic)
		logic.compareFiles(_, _, _, _) >> actions
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.visitFile(fileRelative, FileExistence.BothPaths)
		
		then:
		visitor.getActions().size() == 2
		visitor.getActions()[0] == action
		visitor.getActions()[1] == action2
	}
	
	def "Parameters passed correctly to compare files in root"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> new ArrayList<Action>()
		def fileHashStore1 = Mock(FileHashStore)
		def fileHashStore2 = Mock(FileHashStore)
		def fileHashStoreFactory = Mock(FileHashStoreFactory)
		fileHashStoreFactory.createFileHashStore(root1Absolute) >> fileHashStore1
		fileHashStoreFactory.createFileHashStore(root2Absolute) >> fileHashStore2
		def visitor = new SynchingVisitor(logic, fileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		visitor.visitFile(fileRelative, existence)
		
		then:
		1 * logic.compareFiles(root1Absolute.resolve(fileRelative),
			                   fileHashStore1,
							   root2Absolute.resolve(fileRelative),
							   fileHashStore2) >> new ArrayList<Action>()
	
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
	
	def "Parameters passed correctly to compare files in subdirectory"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> new ArrayList<Action>()
		def fileHashStore1 = Mock(FileHashStore)
		def fileHashStore2 = Mock(FileHashStore)
		def fileHashStoreFactory = Mock(FileHashStoreFactory)
		fileHashStoreFactory.createFileHashStore(root1Absolute.resolve(subDirRelative)) >> fileHashStore1
		fileHashStoreFactory.createFileHashStore(root2Absolute.resolve(subDirRelative)) >> fileHashStore2
		def visitor = new SynchingVisitor(logic, fileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(Paths.get(""), FileExistence.BothPaths)
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subDirRelative.resolve(fileRelative), existence)
		
		then:
		1 * logic.compareFiles(root1Absolute.resolve(subDirRelative).resolve(fileRelative),
							   fileHashStore1,
							   root2Absolute.resolve(subDirRelative).resolve(fileRelative),
							   fileHashStore2) >> new ArrayList<Action>()

		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
	
	def "Hash files not compared"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.visitFile(Paths.get(HashFileSource.HASH_FILE_NAME), FileExistence.BothPaths)
		
		then:
		0 * logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
}