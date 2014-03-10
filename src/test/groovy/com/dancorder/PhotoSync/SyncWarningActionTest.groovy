package com.dancorder.PhotoSync;


public class SyncWarningActionTest extends spock.lang.Specification {

	def "Null or empty warning"() {
		when: "A null parameter is passed"
		new SyncWarningAction(warning)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		warning | _
		null    | _
		""      | _
	}

	def "String value"() {
		setup:
		def warning = "test warning"
		def swa = new SyncWarningAction(warning)

		expect:
		swa.toString() == "WARNING: " + warning
	}
}
