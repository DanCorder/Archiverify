package com.dancorder.PhotoSync;

import java.nio.file.Paths


public class UpdateHashesActionTest extends spock.lang.Specification {
	def "Null FileHashStore"() {
		when: "A null parameter is passed"
		new UpdateHashesAction(store1, store2)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		store1              | store2
		null                | null
		null                | Mock(FileHashStore)
		Mock(FileHashStore) | null
	}

	def "Do action"() {
		setup: "Create the action"
		def store1 = Mock(FileHashStore)
		def store2 = Mock(FileHashStore)
		def action = new UpdateHashesAction(store1, store2)

		when: "it does the action"
		action.doAction()

		then: "the hash store is written out"
		1 * store1.write()
		1 * store2.write()
		then: 0 * _._
	}

	def "String value"() {
		setup: "Create the action"
		def store1 = Mock(FileHashStore)
		def store2 = Mock(FileHashStore)
		store1.getDirectory() >> Paths.get("dir1")
		store2.getDirectory() >> Paths.get("dir2")
		def action = new UpdateHashesAction(store1, store2)

		expect:
		action.toString() == "Write hashes to dir1 and dir2"
	}
}