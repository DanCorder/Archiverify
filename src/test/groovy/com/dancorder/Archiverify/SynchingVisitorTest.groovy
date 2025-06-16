//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2014  Daniel Corder (contact: archiverify@dancorder.com)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.dancorder.Archiverify

import java.nio.file.Paths

import com.dancorder.Archiverify.ParallelFileTreeWalker.FileExistence
import com.dancorder.Archiverify.testHelpers.*

class SynchingVisitorTest extends spock.lang.Specification {

	private final static tempDir = FileSystem.getTempDirectory()
	private final static root1Absolute = tempDir.resolve("testRoot1")
	private final static root2Absolute = tempDir.resolve("testRoot2")
	private final static rootDirRelative = Paths.get("")
	private final static subDirRelative = Paths.get("subDir")
	private final static subDirAbsoluteRoot1 = root1Absolute.resolve(subDirRelative)
	private final static subDirAbsoluteRoot2 = root2Absolute.resolve(subDirRelative)
	private final static subSubDirRelative = subDirRelative.resolve("subSubDir")
	private final static subDir2Relative = Paths.get("subDir2")
	private final static fileRelative = Paths.get("rootFile")
	private final static subDirFileRelative = subDirRelative.resolve("subDirFile")
	private final static subDir2FileRelative = subDir2Relative.resolve("subDir2File")
	private final static subSubDirFileRelative = subSubDirRelative.resolve("subSubDirFile")
	private final static hashFile = Paths.get(".hashes")
	private static store1
	private static store2
	private static defaultFileHashStoreFactory
	
	def setup() {
		store1 = Mock(FileHashStore)
		store2 = Mock(FileHashStore)
		defaultFileHashStoreFactory = Spy(FileHashStoreFactory, constructorArgs: [hashFile, hashFile])
		defaultFileHashStoreFactory.createFileHashStore(_) >>> [store1, store2]
	}
	
	def "Actions returned correctly for check hash stores call"() {
		setup:
		def action = Mock(Action)
		def action2 = Mock(Action)
		def actions = new ArrayList<Action>()
		actions.add(action)
		actions.add(action2)
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_,_,_) >> new ArrayList()
		logic.checkHashStores(store1, store2) >> actions
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		visitor.postVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		
		then:
		visitor.getActions().size() == 2
		visitor.getActions()[0] == action
		visitor.getActions()[1] == action2
	}
	
	def "Logic exception in hash store comparison converted to warning"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.checkHashStores(_,_) >> { throw new NullPointerException("Test exception") }
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.postVisitDirectory(subDirRelative, FileExistence.BothPaths)
		def result = visitor.getActions()
		
		then:
		result.size() == 1
		result[0] instanceof WarningAction
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
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		
		then:
		visitor.getActions().size() == 2
		visitor.getActions()[0] == action
		visitor.getActions()[1] == action2
	}
	
	def "Parameters passed correctly to compare directories for root directory"() {
		setup:
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		
		then:
		1 * logic.compareDirectories(root1Absolute, root2Absolute, FileExistence.BothPaths) >> new ArrayList<Action>()
	}
	
	def "Parameters passed correctly to compare directories for sub directories"() {
		setup:
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
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
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		def result = visitor.getActions()
		
		then:
		result.size() == 1
		result[0] instanceof WarningAction
	}
	
	def "Logic exception in directory comparison stops processing of children but not siblings"() {
		setup:
		def store3 = Mock(FileHashStore)
		def store4 = Mock(FileHashStore)
		defaultFileHashStoreFactory = Mock(FileHashStoreFactory)
		defaultFileHashStoreFactory.createFileHashStore(_) >>> [store1, store2, store3, store4]
		
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subDirFileRelative, FileExistence.BothPaths)
		visitor.preVisitDirectory(subSubDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subSubDirFileRelative, FileExistence.BothPaths)
		visitor.postVisitDirectory(subSubDirRelative, FileExistence.BothPaths)
		visitor.postVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.preVisitDirectory(subDir2Relative, FileExistence.BothPaths)
		visitor.visitFile(subDir2FileRelative, FileExistence.BothPaths)
		visitor.postVisitDirectory(subDir2Relative, FileExistence.BothPaths)
		
		then: 1 * logic.compareDirectories(subDirAbsoluteRoot1,subDirAbsoluteRoot2,_) >> { throw new NullPointerException("Test exception") }
		then: 0 * logic.compareFiles(root1Absolute.resolve(subDirFileRelative), _ , root2Absolute.resolve(subDirFileRelative), _) >> new ArrayList<Action>()
		then: 0 * logic.compareDirectories(root1Absolute.resolve(subSubDirRelative) ,root2Absolute.resolve(subSubDirRelative), _) >> new ArrayList<Action>()
		then: 0 * logic.compareFiles(root1Absolute.resolve(subSubDirFileRelative), _ , root2Absolute.resolve(subSubDirFileRelative), _) >> new ArrayList<Action>()
		then: 0 * logic.removeUnvisitedHashes(_,_)
		then: 0 * logic.checkHashStores(_,_) >> new ArrayList<Action>()
		then: 1 * logic.compareDirectories(root1Absolute.resolve(subDir2Relative), root2Absolute.resolve(subDir2Relative), _) >> new ArrayList<Action>()
		then: 1 * logic.compareFiles(root1Absolute.resolve(subDir2FileRelative), _ , root2Absolute.resolve(subDir2FileRelative), _) >> new ArrayList<Action>()
		then: 1 * logic.removeUnvisitedHashes(store3, [subDir2FileRelative.getFileName()])
		then: 1 * logic.removeUnvisitedHashes(store4, [subDir2FileRelative.getFileName()])
		then: 1 * logic.checkHashStores(_,_) >> new ArrayList<Action>()
	}
	
	def "Logic exception in file comparison converted to warning"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_,_,_) >> new ArrayList<Action>()
		logic.compareFiles(_,_,_,_) >> { throw new NullPointerException("Test exception") }
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(fileRelative, FileExistence.BothPaths)
		def result = visitor.getActions()
		
		then:
		result.size() == 1
		result[0] instanceof WarningAction
	}
	
	def "Actions returned correctly for compare files call"() {
		setup:
		def action = Mock(Action)
		def action2 = Mock(Action)
		def actions = new ArrayList<Action>()
		actions.add(action)
		actions.add(action2)
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_,_,_) >> new ArrayList<Action>()
		logic.compareFiles(_, _, _, _) >> actions
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
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
		def visitor = new SynchingVisitor(logic, fileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
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
		def visitor = new SynchingVisitor(logic, fileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
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
	
	def "Correct hash store written out after subdirectory"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> new ArrayList<Action>()
		logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		def fileHashStore1 = Mock(FileHashStore)
		def fileHashStore2 = Mock(FileHashStore)
		def fileHashStore3 = Mock(FileHashStore)
		def fileHashStore4 = Mock(FileHashStore)
		def fileHashStoreFactory = Mock(FileHashStoreFactory)
		fileHashStoreFactory.createFileHashStore(root1Absolute) >> fileHashStore1
		fileHashStoreFactory.createFileHashStore(root2Absolute) >> fileHashStore2
		fileHashStoreFactory.createFileHashStore(root1Absolute.resolve(subDirRelative)) >> fileHashStore3
		fileHashStoreFactory.createFileHashStore(root2Absolute.resolve(subDirRelative)) >> fileHashStore4
		def visitor = new SynchingVisitor(logic, fileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		visitor.visitFile(fileRelative, FileExistence.BothPaths)
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subDirRelative.resolve(fileRelative), FileExistence.BothPaths)
		visitor.postVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.postVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		
		then:
		1 * logic.checkHashStores(fileHashStore1, fileHashStore2) >> new ArrayList<Action>()
		1 * logic.checkHashStores(fileHashStore3, fileHashStore4) >> new ArrayList<Action>()
	}
	
	def "Hash files not compared"() {
		setup:
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.visitFile(hashFile, FileExistence.BothPaths)
		
		then:
		0 * logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}

	def "Ignored extension not compared"() {
		setup:
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, [ "test" ] as String[], root1Absolute, root2Absolute)
		def excludedFile = Paths.get("file.test")
		
		when:
		visitor.visitFile(excludedFile, FileExistence.BothPaths)
		
		then:
		0 * logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}

	def "Ignored extensions not compared"() {
		setup:
		def logic = Mock(SyncLogic)
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, [ "test1", "test2" ] as String[], root1Absolute, root2Absolute)
		def excludedFile1 = Paths.get("file.test1")
		def excludedFile2 = Paths.get("file.test2")
		
		when:
		visitor.visitFile(excludedFile1, FileExistence.BothPaths)
		visitor.visitFile(excludedFile2, FileExistence.BothPaths)
		
		then:
		0 * logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
	
	def "store checked with correct visited files in root"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> new ArrayList<Action>()
		logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
		
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		visitor.visitFile(fileRelative, existence)
		visitor.postVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		
		then:
		1 * logic.removeUnvisitedHashes(store1, [fileRelative])
		1 * logic.removeUnvisitedHashes(store2, [fileRelative])
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
	
	def "store checked with correct visited files in subdirectory"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> new ArrayList<Action>()
		logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
				
		when:
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subDirFileRelative, existence)
		visitor.postVisitDirectory(subDirRelative, FileExistence.BothPaths)
		
		then:
		1 * logic.removeUnvisitedHashes(store1, [subDirFileRelative.getFileName()])
		1 * logic.removeUnvisitedHashes(store2, [subDirFileRelative.getFileName()])
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
	
	def "store checked with correct visited files with subdirectory"() {
		setup:
		def logic = Mock(SyncLogic)
		logic.compareDirectories(_, _, _) >> new ArrayList<Action>()
		logic.compareFiles(_, _, _, _) >> new ArrayList<Action>()

		def store3 = Mock(FileHashStore)
		def store4 = Mock(FileHashStore)
		defaultFileHashStoreFactory = Mock(FileHashStoreFactory)
		defaultFileHashStoreFactory.createFileHashStore(_) >>> [store1, store2, store3, store4]
		
		def visitor = new SynchingVisitor(logic, defaultFileHashStoreFactory, new String[0], root1Absolute, root2Absolute)
				
		when:
		visitor.preVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		visitor.visitFile(fileRelative, existence)
		visitor.preVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.visitFile(subDirFileRelative, existence)
		visitor.postVisitDirectory(subDirRelative, FileExistence.BothPaths)
		visitor.postVisitDirectory(rootDirRelative, FileExistence.BothPaths)
		
		then:
		1 * logic.removeUnvisitedHashes(store1, [fileRelative])
		1 * logic.removeUnvisitedHashes(store2, [fileRelative])
		1 * logic.removeUnvisitedHashes(store3, [subDirFileRelative.getFileName()])
		1 * logic.removeUnvisitedHashes(store4, [subDirFileRelative.getFileName()])
		
		where:
		existence               | _
		FileExistence.BothPaths | _
		FileExistence.Path1Only | _
		FileExistence.Path2Only | _
	}
}