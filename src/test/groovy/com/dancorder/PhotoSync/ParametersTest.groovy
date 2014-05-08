package com.dancorder.PhotoSync

class ParametersTest extends spock.lang.Specification {

	private final static rootPath = File.listRoots()[0].getAbsolutePath()
	
	def "null parameter"() {
		when: "A null constructor parameter"
		def underTest = new Parameters(null)

		then:
		parametersAreInvalid(underTest)
	}
	
	def "empty parameter"() {
		when: "An empty constructor parameter"
		def underTest = new Parameters(new String[0])

		then:
		parametersAreInvalid(underTest)
	}
	
	def "single parameter"() {
		when: "A single value passed to constructor"
		def underTest = new Parameters( ["param1"] as String[] )
		
		then:
		parametersAreInvalid(underTest)
	}
	
	def "three parameters"() {
		when: "Three values passed to constructor"
		def underTest = new Parameters( [ "param1", "param2", "param3" ] as String[] )

		then:
		parametersAreInvalid(underTest)
	}
	
	def "invalid first path"() {
		when: "The first parameter isn't a valid path"
		def underTest = new Parameters( [ "invalid1", rootPath ] as String[] )

		then:
		parametersAreInvalid(underTest)
	}
	
	def "invalid second path"() {
		when: "The second parameter isn't a valid path"
		def underTest = new Parameters( [ rootPath, "invalid2" ] as String[] )

		then:
		parametersAreInvalid(underTest)
	}
	
	def "valid values"() {
		when: "Valid paths are supplied"
		Parameters params = new Parameters( [ rootPath, rootPath ] as String[] )
		
		then:
		params.isValid()
		params.getPath1().toString() == rootPath
		params.getPath2().toString() == rootPath
	}
	
	private void parametersAreInvalid(params) {
		assert !params.isValid()
		assert params.getErrorMessage() != null
		assert params.getErrorMessage().trim().length() > 0
	}
}
