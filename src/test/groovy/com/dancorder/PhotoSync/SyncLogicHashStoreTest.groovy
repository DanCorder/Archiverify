package com.dancorder.PhotoSync


class SyncLogicHashStoreTest extends spock.lang.Specification {
	
//	The rules for the hash store synching logic are simple:
//    - If a hash store has some changes then write it out
//    - Otherwise do nothing
	
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
	
	def private FileHashStore createMockStore(boolean isDirty) {
		def store = Mock(FileHashStore)
		store.isDirty() >> isDirty
		return store
	}
	
	def private ArrayList<Action> createUpdateHashesActions(List<FileHashStore> stores) {
		return new ArrayList<Action>(stores.collect { new UpdateHashesAction(it) })
	}
}