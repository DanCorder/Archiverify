package com.dancorder.PhotoSync

import java.nio.file.Path
import java.nio.file.Paths

class SyncLogicTest extends spock.lang.Specification {
	
//	The rules for the synching logic are tabulated below
//	The column meanings are:
//		File1 - The hash generated from the file under root 1
//		Hash1 - The hash stored in the hash file under root 1
//		File2 - The hash generated from the file under root 2
//		Hash2 - The hash stored in the hash file under root 2
//  The values in the columns (A, B, C, D, NULL) show which values match and
//  which are different in each case.
//  Each combination needs to be tested with file1/hash1 and file2/hash2 switched
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

	def private static Path absolutePath1
	def private static Path absolutePath2
	def private FileHashStore store1
	def private FileHashStore store2
	def private SyncLogic logic
	def private List<Action> expectedResult
	
	def "AAAA"() {
		setup:
		setupScenario(hashA, hashA, hashA, hashA)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
	}
	
	def "AAAB"() {
		setup:
		setupScenario(hashA, hashA, hashA, hashB)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "AAAB reversed"() {
		setup:
		setupScenario(hashA, hashB, hashA, hashA)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		1 * store1.setHash(filePath, hashA)
		0 * store2.setHash(_,_)
	}
	
	def "AAAN"() {
		setup:
		setupScenario(hashA, hashA, hashA, null)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "AAAN reversed"() {
		setup:
		setupScenario(hashA, null, hashA, hashA)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		1 * store1.setHash(filePath, hashA)
		0 * store2.setHash(_,_)
	}
	
	def "AABA and reversed"() {
		setup:
		setupScenario(hash1, hash2, hash3, hash4)
		expectedResult.add(new FileCopyAction(from, to))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
		
		where:
		hash1 | hash2 | hash3 | hash4 | from          | to
		hashA | hashA | hashB | hashA | absolutePath1 | absolutePath2
		hashB | hashA | hashA | hashA | absolutePath2 | absolutePath1
	}
	
	def "AABB"() {
		setup:
		setupScenario(hashA, hashA, hashB, hashB)
		expectedResult.add(new SyncWarningAction(absolutePath1, hashA, hashA, absolutePath2, hashB, hashB))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
	}
	
	def "AABC"() {
		setup:
		setupScenario(hashA, hashA, hashB, hashC)
		expectedResult.add(new FileCopyAction(absolutePath1, absolutePath2))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "AABC reversed"() {
		setup:
		setupScenario(hashB, hashC, hashA, hashA)
		expectedResult.add(new FileCopyAction(absolutePath2, absolutePath1))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		1 * store1.setHash(filePath, hashA)
		0 * store2.setHash(_,_)
	}
	
	def "AABN"() {
		setup:
		setupScenario(hashA, hashA, hashB, null)
		expectedResult.add(new FileCopyAction(absolutePath1, absolutePath2))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "AABN reversed"() {
		setup:
		setupScenario(hashB, null, hashA, hashA)
		expectedResult.add(new FileCopyAction(absolutePath2, absolutePath1))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		1 * store1.setHash(filePath, hashA)
		0 * store2.setHash(_,_)
	}
	
	def "AANA and reversed"() {
		setup:
		setupScenario(hash1, hash2, hash3, hash4)
		expectedResult.add(new FileCopyAction(from, to))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
		
		where:
		hash1 | hash2 | hash3 | hash4 | from          | to
		hashA | hashA | null  | hashA | absolutePath1 | absolutePath2
		null  | hashA | hashA | hashA | absolutePath2 | absolutePath1
	}
	
	def "AANB"() {
		setup:
		setupScenario(hashA, hashA, null, hashB)
		expectedResult.add(new FileCopyAction(absolutePath1, absolutePath2))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "AANB reversed"() {
		setup:
		setupScenario(null, hashB, hashA, hashA)
		expectedResult.add(new FileCopyAction(absolutePath2, absolutePath1))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		1 * store1.setHash(filePath, hashA)
		0 * store2.setHash(_,_)
	}
	
	def "AANN"() {
		setup:
		setupScenario(hashA, hashA, null, null)
		expectedResult.add(new FileCopyAction(absolutePath1, absolutePath2))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}

	def "AANN reversed"() {
		setup:
		setupScenario(null, null, hashA, hashA)
		expectedResult.add(new FileCopyAction(absolutePath2, absolutePath1))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		1 * store1.setHash(filePath, hashA)
		0 * store2.setHash(_,_)
	}
	
	def "ABXX tests"() {
		setup:
		setupScenario(hash1, hash2, hash3, hash4)
		def warning1 = new SyncWarningAction(absolutePath1, hash1, hash2, absolutePath2, hash3, hash4)
		def warning2 = new SyncWarningAction(absolutePath2, hash3, hash4, absolutePath1, hash1, hash2)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result.size() == 1
		result[0] == warning1 || result[0] == warning2
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
		
		where:
		hash1 | hash2 | hash3 | hash4
		hashA | hashB | hashA | hashB // ABAB no reverse
		hashA | hashB | hashA | hashC // ABAC no reverse
		hashA | hashB | hashA | null  // ABAN
		hashA | null  | hashA | hashB // ABAN reverse
		hashA | hashB | hashB | hashA // ABBA no reverse
		hashA | hashB | hashB | hashC // ABBC
		hashB | hashC | hashA | hashB // ABBC reverse
		hashA | hashB | hashB | null  // ABBN
		hashB | null  | hashA | hashB // ABBN reverse
		hashA | hashB | hashC | hashB // ABCB no reverse
		hashC | hashB | hashC | hashD // ABCD no reverse
		hashA | hashB | hashC | null  // ABCN
		hashC | null  | hashA | hashB // ABCN reverse
		hashA | hashB | null  | hashA // ABNA
		null  | hashA | hashA | hashB // ABNA reverse
		hashA | hashB | null  | hashB // ABNB
		null  | hashB | hashA | hashB // ABNB reverse
		hashA | hashB | null  | hashC // ABNC
		null  | hashC | hashA | hashB // ABNC reverse
		hashA | hashB | null  | null  // ABNN
		null  | null  | hashA | hashB // ABNN reverse
	}
	
	
	def "NAAN"() {
		setup:
		setupScenario(null, hashA, hashA, null)
		expectedResult.add(new FileCopyAction(absolutePath2, absolutePath1))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		0 * store1.setHash(_,_)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "NAAN reversed"() {
		setup:
		setupScenario(hashA, null, null, hashA)
		expectedResult.add(new FileCopyAction(absolutePath1, absolutePath2))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		expectedResult == result
		1 * store1.setHash(filePath, hashA)
		0 * store2.setHash(_,_)
	}
	
	def "NABN"() {
		setup:
		setupScenario(null, hashA, hashB, null)
		expectedResult.add(new SyncWarningAction(absolutePath1, null, hashA, absolutePath2, hashB, null))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
	}
	
	def "NABN reversed"() {
		setup:
		setupScenario(hashB, null, null, hashA)
		expectedResult.add(new SyncWarningAction(absolutePath2, null, hashA, absolutePath1, hashB, null))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
	}
	
	def "NANA and NANB"() {
		setup:
		setupScenario(null, hash1, null, hash2)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		1 * store1.removeHash(filePath)
		1 * store2.removeHash(filePath)
		
		where:
		hash1 | hash2
		hashA | hashA
		hashA | hashB
	}
	
	def "NANN"() {
		setup:
		setupScenario(null, hashA, null, null)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		1 * store1.removeHash(filePath)
		0 * store2.removeHash(filePath)
	}
	
	def "NANN reversed"() {
		setup:
		setupScenario(null, null, null, hashA)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		0 * store1.removeHash(filePath)
		1 * store2.removeHash(filePath)
	}
	
	def "ANAN"() {
		setup:
		setupScenario(hashA, null, hashA, null)
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		1 * store1.setHash(filePath, hashA)
		1 * store2.setHash(filePath, hashA)
	}
	
	def "ANBN"() {
		setup:
		setupScenario(hashA, null, hashB, null)
		expectedResult.add(new SyncWarningAction(absolutePath1, hashA, null, absolutePath2, hashB, null))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		0 * store1.setHash(_,_)
		0 * store2.setHash(_,_)
	}
	
	def "ANNN"() {
		setup:
		setupScenario(hashA, null, null, null)
		expectedResult.add(new FileCopyAction(absolutePath1, absolutePath2))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		1 * store1.setHash(filePath, hashA)
		1 * store2.setHash(filePath, hashA)
	}

	def "ANNN reversed"() {
		setup:
		setupScenario(null, null, hashA, null)
		expectedResult.add(new FileCopyAction(absolutePath2, absolutePath1))
		
		when:
		def result = logic.compareFiles(absolutePath1, store1, absolutePath2, store2)

		then:
		result == expectedResult
		1 * store1.setHash(filePath, hashA)
		1 * store2.setHash(filePath, hashA)
	}
	
	def private setupScenario(String fileHash1, String storeHash1, String fileHash2, String storeHash2) {
		absolutePath1 = root1.resolve(filePath)
		absolutePath2 = root2.resolve(filePath)
		def hashGenerator = Mock(FileHashGenerator)
		hashGenerator.calculateMd5(absolutePath1) >> fileHash1
		hashGenerator.calculateMd5(absolutePath2) >> fileHash2
		logic = new SyncLogic(hashGenerator)
		
		store1 = Mock(FileHashStore)
		setupStore(storeHash1, store1)
		store2 = Mock(FileHashStore)
		setupStore(storeHash2, store2)
		
		expectedResult = new ArrayList();
	}

	private setupStore(String storeHash, FileHashStore store) {
		if (storeHash == null) {
			store.hashExists(filePath) >> false
			store.getHash(filePath) >> null
		}
		else {
			store.hashExists(filePath) >> true
			store.getHash(filePath) >> storeHash
		}
	}
}