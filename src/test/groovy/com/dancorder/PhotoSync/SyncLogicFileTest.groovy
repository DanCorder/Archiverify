package com.dancorder.PhotoSync

import java.nio.file.Path
import java.nio.file.Paths

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence

class SyncLogicTest extends spock.lang.Specification {
	
//	The rules for the synching logic are tabulated below
//	The column meanings are:
//		File1 - The hash generated from the file under root 1
//		Hash1 - The hash stored in the hash file under root 1
//		File2 - The hash generated from the file under root 2
//		Hash2 - The hash stored in the hash file under root 2
//  The values in the columns (A, B, C, D, NULL) show which values match and
//  which are different in each case.
//	
//	File1 & Hash1 match
//	File1 | Hash1 | File2 | Hash2 | Action
//	A     | A     | A     | A     | None
//	A     | A     | A     | B     | Copy hash
//	A     | A     | A     | NULL  | Copy hash
//	A     | A     | B     | A     | Copy file
//	A     | A     | B     | B     | Ask user
//	A     | A     | B     | C     | Copy file and hash
//	A     | A     | B     | NULL  | Copy file and hash
//	A     | A     | NULL  | A     | Copy file
//	A     | A     | NULL  | B     | Copy file and hash
//	A     | A     | NULL  | NULL  | Copy file and hash
//	
//	File1 & Hash1 don't match
//	File1 | Hash1 | File2 | Hash2 | Action
//	A     | B     | A     | A     | see above - AAAB
//	A     | B     | A     | B     | Ask user
//	A     | B     | A     | C     | Ask user
//	A     | B     | A     | NULL  | Ask user
//	A     | B     | B     | A     | Ask user
//	A     | B     | B     | B     | see above - AABA
//	A     | B     | B     | C     | Ask user
//	A     | B     | B     | NULL  | Ask user
//	A     | B     | C     | A     | see above - ABBC
//	A     | B     | C     | B     | Ask user
//	A     | B     | C     | C     | see above - AABC
//	A     | B     | C     | D     | Ask user
//	A     | B     | C     | NULL  | Ask user
//	A     | B     | NULL  | A     | Ask user
//	A     | B     | NULL  | B     | Ask user
//	A     | B     | NULL  | C     | Ask user
//	A     | B     | NULL  | NULL  | Ask user
//	
//	File1 is NULL
//	File1 | Hash1 | File2 | Hash2 | Action
//	NULL  | A     | A     | A     | see above - AANA
//	NULL  | A     | A     | B     | see above - ABNA
//	NULL  | A     | A     | NULL  | Copy file and hash
//	NULL  | A     | B     | A     | see above - ABNB
//	NULL  | A     | B     | B     | see above - AANB
//	NULL  | A     | B     | C     | see above - ABNC
//	NULL  | A     | B     | NULL  | Ask user
//	NULL  | A     | NULL  | A     | Remove hash
//	NULL  | A     | NULL  | B     | Remove hash
//	NULL  | A     | NULL  | NULL  | Remove hash
//	
//	Hash1 is NULL
//	File1 | Hash1 | File2 | Hash2 | Action
//	A     | NULL  | A     | A     | see above - AAAN
//	A     | NULL  | A     | B     | see above - ABAN
//	A     | NULL  | A     | NULL  | Create hash
//	A     | NULL  | B     | A     | see above - ABBN
//	A     | NULL  | B     | B     | see above - AABN
//	A     | NULL  | B     | C     | see above - ABCN
//	A     | NULL  | B     | NULL  | Ask user
//	A     | NULL  | NULL  | A     | see above - NAAN
//	A     | NULL  | NULL  | B     | see above - NABN
//	A     | NULL  | NULL  | NULL  | Create hash and copy file

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static root1 = tempDir.resolve("testRoot1")
	private final static root2 = tempDir.resolve("testRoot2")
	
	private final static filePath = Paths.get("testFile")
	
	private final static hashA = "hashA"
	private final static hashB = "hashB"
	private final static hashC = "hashC"
	private final static hashD = "hashD"
	
	def private FileHashGenerator hashGenerator
	def private Path absolutePath1
	def private Path absolutePath2
	def private FileHashStore store1
	def private FileHashStore store2
	
	def "AAAA"() {
		setup:
		setupScenario(hashA, hashA, hashA, hashA)
		def expectedResult = new ArrayList<Action>()
		def logic = new SyncLogic(hashGenerator)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2, FileExistence.BothPaths)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
	}
	
	def "AAAB"() {
		setup:
		setupScenario(hashA, hashA, hashA, hashB)
		def expectedResult = new ArrayList<Action>()
		def logic = new SyncLogic(hashGenerator)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2, FileExistence.BothPaths)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "AAAB reversed"() {
		setup:
		setupScenario(hashA, hashB, hashA, hashA)
		def expectedResult = new ArrayList<Action>()
		def logic = new SyncLogic(hashGenerator)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2, FileExistence.BothPaths)

		then:
		expectedResult == result
		0 * store2.setHash(_,_)
		1 * store1.setHash(filePath, hashA)
	}
	
	// TODO Remaining file test
	// TODO Directory tests
	
	def private setupScenario(String fileHash1, String storeHash1, String fileHash2, String storeHash2) {
		absolutePath1 = root1.resolve(filePath)
		absolutePath2 = root2.resolve(filePath)
		hashGenerator = Mock(FileHashGenerator)
		hashGenerator.calculateMd5(absolutePath1) >> fileHash1
		hashGenerator.calculateMd5(absolutePath2) >> fileHash2
		
		store1 = Mock(FileHashStore)
		store1.hashExists(filePath) >> true
		store1.getHash(filePath) >> storeHash1
		store2 = Mock(FileHashStore)
		store2.hashExists(filePath) >> true
		store2.getHash(filePath) >> storeHash2
	}
}