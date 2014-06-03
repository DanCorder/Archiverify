package com.dancorder.PhotoSync;


public class WarningActionTest extends spock.lang.Specification {

	def "Null or empty warning"() {
		when: "A null parameter is passed"
		new WarningAction(warning)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		warning        | _
		null           | _
		new String("") | _
	}

	def "String value"() {
		setup:
		def warning = "test warning"
		def action = new WarningAction(warning)

		expect:
		action.toString() == warning
	}
}
