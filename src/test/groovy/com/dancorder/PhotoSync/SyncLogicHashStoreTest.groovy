package com.dancorder.PhotoSync


class SyncLogicHashStoreTest extends spock.lang.Specification {
	
//	The rules for the hash store synching logic are simple:
//    - If a hash store has some changes then write it out
//    - Otherwise do nothing

	def "hash stores unchanged"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = Mock(FileHashStore)
		def store2 = Mock(FileHashStore)
		store1.isDirty() >> false;
		store2.isDirty() >> false;
		def expectedResult = new ArrayList<Action>()
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
	
	def "hash store 1 changed"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = Mock(FileHashStore)
		def store2 = Mock(FileHashStore)
		store1.isDirty() >> true;
		store2.isDirty() >> false;
		def expectedResult = new ArrayList<Action>()
		def action = new UpdateHashesAction(store1)
		expectedResult.add(action)
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
	
	def "hash store 2 changed"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = Mock(FileHashStore)
		def store2 = Mock(FileHashStore)
		store1.isDirty() >> false;
		store2.isDirty() >> true;
		def expectedResult = new ArrayList<Action>()
		def action = new UpdateHashesAction(store2)
		expectedResult.add(action)
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
	
	def "hash both stores changed"() {
		setup:
		def logic = new SyncLogic(null)
		def store1 = Mock(FileHashStore)
		def store2 = Mock(FileHashStore)
		store1.isDirty() >> true;
		store2.isDirty() >> true;
		def expectedResult = new ArrayList<Action>()
		def action1 = new UpdateHashesAction(store1)
		def action2 = new UpdateHashesAction(store2)
		expectedResult.add(action1)
		expectedResult.add(action2)
		
		when:
		def result = logic.checkHashStores(store1, store2)

		then:
		expectedResult == result
	}
}