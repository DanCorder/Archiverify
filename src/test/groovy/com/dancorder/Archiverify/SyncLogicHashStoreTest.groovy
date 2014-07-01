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

import java.nio.file.Path
import java.nio.file.Paths


class SyncLogicHashStoreTest extends spock.lang.Specification {
	
//	The rules for the hash store synching logic are simple:
//    - If a hash store has some changes then write it out
//    - Otherwise do nothing
	
	private static final hash1 = "testHash"
	private static final hash2 = "testHash2"
	
	private static final file1 = Paths.get("file1")
	private static final file2 = Paths.get("file2")
	
	def "hash stores unchanged"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = createMockStore(false)
		def store2 = createMockStore(false)
		def expectedResult = new ArrayList<Action>()
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
	
	def "hash store 1 changed"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = createMockStore(true)
		def store2 = createMockStore(false)
		def expectedResult = createUpdateHashesActions([store1])
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
	
	def "hash store 2 changed"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = createMockStore(false)
		def store2 = createMockStore(true)
		def expectedResult = createUpdateHashesActions([store2])
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
	
	def "hash both stores changed"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = createMockStore(true)
		def store2 = createMockStore(true)
		def expectedResult = createUpdateHashesActions([store1, store2])
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
	
	def "remove unvisited files from store with no visits"() {
		setup:
		def logic = new SyncLogic(null)
		def store = createStore([(file1):hash1, (file2):hash2])
		
		when:
		def result = logic.removeUnvisitedHashes(store, [])

		then:
		store.getFiles().size()== 0
		store.isDirty()
	}
	
	def "remove unvisited files from store with all visited"() {
		setup:
		def logic = new SyncLogic(null)
		def store = createStore([(file1):hash1, (file2):hash2])
		
		when:
		def result = logic.removeUnvisitedHashes(store, [file1, file2])

		then:
		store.getFiles().size()== 2
		store.hashExists(file1)
		store.hashExists(file2)
		!store.isDirty()
	}
	
	def "remove unvisited files from store with some visits"() {
		setup:
		def logic = new SyncLogic(null)
		def store = createStore([(file1):hash1, (file2):hash2])
		
		when:
		def result = logic.removeUnvisitedHashes(store, [file])

		then:
		store.getFiles().size()== 1
		store.hashExists(file)
		store.isDirty()
		
		where:
		file  | _
		file1 | _
		file2 | _
	}
	
	def private FileHashStore createMockStore(boolean isDirty) {
		def store = Mock(FileHashStore)
		store.isDirty() >> isDirty
		return store
	}
	
	def private FileHashStore createStore(Map<Path, String> hashesInStore) {
		def source = Mock(HashFileSource)
		source.getData() >> hashesInStore.collect { it.value + "\t" + it.key.fileName.toString() }
		
		return new FileHashStore(source)
	}
	
	def private ArrayList<Action> createUpdateHashesActions(List<FileHashStore> stores) {
		return new ArrayList<Action>(stores.collect { new UpdateHashesAction(it) })
	}
}