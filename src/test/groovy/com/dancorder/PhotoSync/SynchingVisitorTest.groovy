package com.dancorder.PhotoSync

import java.nio.file.Paths

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence

class SynchingVisitorTest extends spock.lang.Specification {

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static root1Absolute = tempDir.resolve("testRoot1")
	private final static root2Absolute = tempDir.resolve("testRoot2")
	private final static subDirRelative = Paths.get("subDir")
	private final static subDir1Absolute = root1Absolute.resolve(subDirRelative)
	private final static subDir2Absolute = root2Absolute.resolve(subDirRelative)
	private final static file1Relative = Paths.get("file1")
	private final defaultFileHashStoreFactory = Mock(FileHashStoreFactory)

	
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
		visitor.getActions().size() == 2
		visitor.getActions()[0] == action
		visitor.getActions()[1] == action2
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
		1 * logic.compareDirectories(subDir1Absolute, subDir2Absolute, existence) >> new ArrayList<Action>()
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
	
	def "Actions returned correctly for compare files call"() {
		setup:
		def action = Mock(Action)
		def action2 = Mock(Action)
		def actions = new ArrayList<Action>()
		actions.add(action)
		actions.add(action2)
		def logic = Mock(SyncLogic)
		logic.compareFiles(_, _, _, _, _) >> actions
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, root1Absolute, root2Absolute)
		
		when:
		visitor.visitFile(file1Relative, FileExistence.BothPaths)
		
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
		visitor.visitFile(file1Relative, existence)
		
		then:
		1 * logic.compareFiles(root1Absolute.resolve(file1Relative),
			                   fileHashStore1,
							   root2Absolute.resolve(file1Relative),
							   fileHashStore2,
							   existence) >> new ArrayList<Action>()
		
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
		visitor.visitFile(subDirRelative.resolve(file1Relative), existence)
		
		then:
		1 * logic.compareFiles(root1Absolute.resolve(subDirRelative).resolve(file1Relative),
							   fileHashStore1,
							   root2Absolute.resolve(subDirRelative).resolve(file1Relative),
							   fileHashStore2,
							   existence) >> new ArrayList<Action>()

		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
}