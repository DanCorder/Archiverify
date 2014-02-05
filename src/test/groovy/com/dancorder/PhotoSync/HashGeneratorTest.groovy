package com.dancorder.PhotoSync;

import spock.lang.*

class HashGeneratorTest extends spock.lang.Specification {

	def "hash generation"() {
		setup: "correct hash value calculated by http://onlinemd5.com/"
		def generator = new HashGenerator()
		def data = new ByteArrayInputStream("testData".getBytes("ISO-8859-1"))
		def expected = [0x3A,0x76,0x0F,0xAE,0x78,0x4D,0x30,0xA1,0xB5,0x0E,0x30,0x4E,0x97,0xA1,0x73,0x55] as byte[]
		
		when: "a hash is generated"
		def hash = generator.calculateMd5(data)

		then: "it is the correct hash"
		hash == expected
	}

}
