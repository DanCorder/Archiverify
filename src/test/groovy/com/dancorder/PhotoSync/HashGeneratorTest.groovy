package com.dancorder.PhotoSync;

class HashGeneratorTest extends spock.lang.Specification {

	def "hash generation"() {
		setup: "correct hash value calculated by http://onlinemd5.com/"
		def generator = new HashGenerator()
		def data = new ByteArrayInputStream("testData".getBytes("ISO-8859-1"))
		def expected = "3a760fae784d30a1b50e304e97a17355"
		
		when: "a hash is generated"
		def hash = generator.calculateMd5(data)

		then: "it is the correct hash"
		hash == expected
	}

}
