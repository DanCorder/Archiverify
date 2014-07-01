package com.dancorder.Archiverify

import java.nio.file.*

class HashCheckingVisitorTest extends spock.lang.Specification {

	private static final testHash = "testHash"
	private static final testHash2 = "testHash2"
	private static final badHash = "badHash"
	private static final testRootFileName = "testRootFileName"
	private static final testRootFileName2 = "testRootFileName2"
	private static final testSubDirFileName = "testSubDirFileName"
	private static final testSubDirFileName2 = "testSubDirFileName2"
	
	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static rootAbsolute = tempDir.resolve("testRoot")
	private final static subDirRelative = Paths.get("subDir")
	private final static subDirAbsolute = rootAbsolute.resolve(subDirRelative)
	private final static rootFileAbsolute = rootAbsolute.resolve(testRootFileName)
	private final static rootFileAbsolute2 = rootAbsolute.resolve(testRootFileName2)
	private final static subDirFileAbsolute = subDirAbsolute.resolve(testSubDirFileName)
	private final static subDirFileAbsolute2 = subDirAbsolute.resolve(testSubDirFileName2)
	private final static rootHashFileAbsolute = rootAbsolute.resolve("hashes.txt")
	private final static subDirHashFileAbsolute = subDirAbsolute.resolve("hashes.txt")
	
	private static store
	private static defaultFileHashStoreFactory
	private static defaultHashGenerator
	
	private def createScenario() {
		createScenario([:], [:])
	}
	
	private def createScenario(Map<Path, String> hashesInStore, Map<Path, String> hashesToGenerate) {
		
		store = createFakeFileHashStore(hashesInStore)
		
		defaultFileHashStoreFactory = Mock(FileHashStoreFactory)
		defaultFileHashStoreFactory.createFileHashStore(_) >> store
		
		defaultHashGenerator = createMockHashGenerator(hashesToGenerate)
	}
	
	private def createFakeFileHashStore(Map<Path, String> hashesInStore) {
		def source = Mock(HashFileSource)
		source.getData() >> hashesInStore.collect { it.value + "\t" + it.key.fileName.toString() }
		
		return new FileHashStore(source)
	}
	
	private def createMockHashGenerator(Map<Path, String> hashesToGenerate) {
		def ret = Mock(FileHashGenerator)
		for (path in hashesToGenerate.keySet()) {
			ret.calculateMd5(path) >> hashesToGenerate[path]
		}
		
		return ret;
	}
	
	def "No actions generated when no files visited"() {
		setup:
		createScenario()
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		def expected = new ArrayList<Action>()
		
		when:
		def ret1 = underTest.preVisitDirectory(path, null)
		def ret2 = underTest.postVisitDirectory(path, null)
		def actions = underTest.getActions() 
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		actions == expected

		where:
		path           | _
		rootAbsolute   | _
		subDirAbsolute | _
	}
	
	def "No actions generated when matching file visited"() {
		setup:
		createScenario([(filePath):testHash], [(filePath):testHash])
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		def expected = new ArrayList<Action>()
		
		when:
		def ret1 = underTest.preVisitDirectory(dirPath, null)
		def ret2 = underTest.visitFile(filePath, null)
		def ret3 = underTest.postVisitDirectory(dirPath, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		ret3 == FileVisitResult.CONTINUE
		actions == expected

		where:
		dirPath        | filePath
		rootAbsolute   | rootFileAbsolute
		subDirAbsolute | subDirFileAbsolute
	}
	
	def "Warning action generated when non-matching files visited"() {
		setup:
		createScenario([(filePath):testHash], [(filePath):badHash])
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		
		when:
		def ret1 = underTest.preVisitDirectory(dirPath, null)
		def ret2 = underTest.visitFile(filePath, null)
		def ret3 = underTest.postVisitDirectory(dirPath, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		ret3 == FileVisitResult.CONTINUE
		actions.size() == 1
		actions[0] instanceof WarningAction

		where:
		dirPath        | filePath
		rootAbsolute   | rootFileAbsolute
		subDirAbsolute | subDirFileAbsolute
	}
	
	def "Action generated when new files visited"() {
		setup:
		createScenario([:], [(filePath):testHash])
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		def expected = new ArrayList<Action>()
		expected.add(new UpdateHashesAction(store))
		
		when:
		def ret1 = underTest.preVisitDirectory(dirPath, null)
		def ret2 = underTest.visitFile(filePath, null)
		def ret3 = underTest.postVisitDirectory(dirPath, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		ret3 == FileVisitResult.CONTINUE
		actions == expected
		store.hashExists(filePath.getFileName())
		store.getHash(filePath.getFileName()) == testHash
		store.getFiles().size() == 1

		where:
		dirPath        | filePath
		rootAbsolute   | rootFileAbsolute
		subDirAbsolute | subDirFileAbsolute
	}
	
	def "Exception in pre visit directory converted to warning"() {
		setup:
		createScenario()
		defaultFileHashStoreFactory = Mock(FileHashStoreFactory)
		defaultFileHashStoreFactory.createFileHashStore(_) >> { throw new NullPointerException("Test exception") }
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		
		when:
		def ret1 = underTest.preVisitDirectory(rootAbsolute, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		actions.size() == 1
		actions[0] instanceof WarningAction
	}
	
	def "Hashes.txt ignored"() {
		setup:
		createScenario([:], [(filePath):testHash])
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		
		when:
		def ret1 = underTest.preVisitDirectory(dirPath, null)
		def ret2 = underTest.visitFile(filePath, null)
		def ret3 = underTest.postVisitDirectory(dirPath, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		ret3 == FileVisitResult.CONTINUE
		actions.size() == 0
		
		where:
		dirPath        | filePath
		rootAbsolute   | rootHashFileAbsolute
		subDirAbsolute | subDirHashFileAbsolute
	}

	def "Exception in pre visit directory doesn't cause errors for files"() {
		setup:
		createScenario([:], [(subDirFileAbsolute):testHash])
		defaultFileHashStoreFactory = Mock(FileHashStoreFactory)
		defaultFileHashStoreFactory.createFileHashStore(rootAbsolute) >> { throw new NullPointerException("Test exception") }
		defaultFileHashStoreFactory.createFileHashStore(subDirAbsolute) >> store
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		
		when:
		def ret1 = underTest.preVisitDirectory(rootAbsolute, null)
		def ret2 = underTest.visitFile(rootFileAbsolute, null)
		def ret3 = underTest.preVisitDirectory(subDirAbsolute, null)
		def ret4 = underTest.visitFile(subDirFileAbsolute, null)
		def ret5 = underTest.postVisitDirectory(subDirAbsolute, null)
		def ret6 = underTest.postVisitDirectory(rootAbsolute, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		ret3 == FileVisitResult.CONTINUE
		ret4 == FileVisitResult.CONTINUE
		ret5 == FileVisitResult.CONTINUE
		ret6 == FileVisitResult.CONTINUE
		actions.size() == 2
		actions[0] instanceof WarningAction
		actions[1] == new UpdateHashesAction(store) 
	}
	
	def "Correct hash stores written out after visiting subdirectory"() {
		setup:
		createScenario([:], [(subDirFileAbsolute):testHash, (rootFileAbsolute):testHash])
		def rootStore = createFakeFileHashStore([:])
		def subDirStore = createFakeFileHashStore([:])
		def fileHashStoreFactory = Mock(FileHashStoreFactory)
		fileHashStoreFactory.createFileHashStore(rootAbsolute) >> rootStore
		fileHashStoreFactory.createFileHashStore(subDirAbsolute) >> subDirStore
		def underTest = new HashCheckingVisitor(fileHashStoreFactory, defaultHashGenerator)
		
		when:
		def ret1 = underTest.preVisitDirectory(rootAbsolute, null)
		def ret2 = underTest.visitFile(rootFileAbsolute, null)
		def ret3 = underTest.preVisitDirectory(subDirAbsolute, null)
		def ret4 = underTest.visitFile(subDirFileAbsolute, null)
		def ret5 = underTest.postVisitDirectory(subDirAbsolute, null)
		def ret6 = underTest.postVisitDirectory(rootAbsolute, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		ret3 == FileVisitResult.CONTINUE
		ret4 == FileVisitResult.CONTINUE
		ret5 == FileVisitResult.CONTINUE
		ret6 == FileVisitResult.CONTINUE
		actions.size() == 2
		actions[0] == new UpdateHashesAction(subDirStore)
		actions[1] == new UpdateHashesAction(rootStore)
	}
	
	def "visitFileFailed converted to warning"() {
		setup:
		createScenario()
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		
		when:
		def ret1 = underTest.visitFileFailed(rootAbsolute, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		actions.size() == 1
		actions[0] instanceof WarningAction
	}
	
	def "Exception in post visit directory converted to warning"() {
		setup:
		createScenario()
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		
		when:
		def ret1 = underTest.postVisitDirectory(rootAbsolute, new IOException("test exception"))
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		actions.size() == 1
		actions[0] instanceof WarningAction
	}
	
	def "All hashes removed when no files present"() {
		setup:
		createScenario([(filePath1):testHash, (filePath2):testHash2], [:])
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		def expected = new ArrayList<Action>()
		expected.add(new UpdateHashesAction(store))
		
		when:
		def ret1 = underTest.preVisitDirectory(dirPath, null)
		def ret2 = underTest.postVisitDirectory(dirPath, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		actions == expected
		!store.hashExists(filePath1.getFileName())
		!store.hashExists(filePath2.getFileName())
		store.getFiles().size() == 0

		where:
		dirPath        | filePath1          | filePath2
		rootAbsolute   | rootFileAbsolute   | rootFileAbsolute2
		subDirAbsolute | subDirFileAbsolute | subDirFileAbsolute2
	}
	
	def "Only non visited hashes removed"() {
		setup:
		createScenario([(filePath1):testHash, (filePath2):testHash2], [(filePath1):testHash])
		def underTest = new HashCheckingVisitor(defaultFileHashStoreFactory, defaultHashGenerator)
		def expected = new ArrayList<Action>()
		expected.add(new UpdateHashesAction(store))
		
		when:
		def ret1 = underTest.preVisitDirectory(dirPath, null)
		def ret2 = underTest.visitFile(filePath1, null)
		def ret3 = underTest.postVisitDirectory(dirPath, null)
		def actions = underTest.getActions()
		
		then:
		ret1 == FileVisitResult.CONTINUE
		ret2 == FileVisitResult.CONTINUE
		ret3 == FileVisitResult.CONTINUE
		actions == expected
		store.hashExists(filePath1.getFileName())
		!store.hashExists(filePath2.getFileName())
		store.getFiles().size() == 1

		where:
		dirPath        | filePath1          | filePath2
		rootAbsolute   | rootFileAbsolute   | rootFileAbsolute2
		subDirAbsolute | subDirFileAbsolute | subDirFileAbsolute2
	}
}