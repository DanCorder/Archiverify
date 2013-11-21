package com.dancorder.PhotoSync

import spock.lang.*

class ParametersTest extends spock.lang.Specification{

	private final String rootPath = File.listRoots()[0].getAbsolutePath();
	
	def "null parameter"() {
		when: "A null constructor parameter"
		new Parameters(null)

		then: "expect UsageException"
		thrown(UsageException)
	}
	
	def "empty parameter"() {
		when: "An empty constructor parameter"
		new Parameters(new String[0])

		then: "expect UsageException"
		thrown(UsageException)
	}
	
	def "single parameter"() {
		when: "A single value passed to constructor"
		new Parameters( ["param1"] as String[] )

		then: "expect UsageException"
		thrown(UsageException)
	}
	
	def "three parameters"() {
		when: "Three values passed to constructor"
		new Parameters( [ "param1", "param2", "param3" ] as String[] )

		then: "expect UsageException"
		thrown(UsageException)
	}
	
	def "invalid first value"() {
		when: "The first parameter isn't a valid path"
		new Parameters( [ "invalid1", rootPath ] as String[] )

		then: "expect UsageException"
		thrown(UsageException)
	}
	
	def "invalid second value"() {
		when: "The second parameter isn't a valid path"
		new Parameters( [ rootPath, "invalid2" ] as String[] )

		then: "expect UsageException"
		thrown(UsageException)
	}
	
	def "valid values"() {
		when: "Valid paths are supplied"
		Parameters params = new Parameters( [ rootPath, rootPath ] as String[] )
		
		then:
		params.getPath1().toString().equals(rootPath);
		params.getPath2().toString().equals(rootPath);
	}
}
