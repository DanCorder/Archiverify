package com.dancorder.PhotoSync;

import java.nio.file.Paths


public class UpdateHashesActionTest extends spock.lang.Specification {
	def "Null FileHashStore"() {
		when: "A null parameter is passed"
		new UpdateHashesAction(null)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)
	}
	
	def "Do action"() {
		setup: "Create the action"
		def store = Mock(FileHashStore)
		def action = new UpdateHashesAction(store)
		
		when: "it does the action"
		action.doAction()
		
		then: "the hash store is written out"
		1 * store.write()
	}
	
	def "String value"() {
		setup: "Create the action"
		def store = Mock(FileHashStore)
		store.getDirectories() >> [Paths.get("dir1"), Paths.get("dir2")]
		def action = new UpdateHashesAction(store)
		
		expect:
		action.toString() == "Write hashes to dir1 and dir2"
	}
}