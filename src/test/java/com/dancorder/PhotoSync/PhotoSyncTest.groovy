package com.dancorder.PhotoSync;

import spock.lang.*

public class PhotoSyncTest extends spock.lang.Specification {

	def "Null parameters"() {
		when: "A null parameter is passed to PhotoSyncTest"
		PhotoSync.main(null);

		then: "expect UsageException"
		thrown(UsageException)
	}
	
	def "Empty parameters"() {
		when: "A no parameters are passed to PhotoSyncTest"
		PhotoSync.main(new String[0]);

		then: "expect UsageException"
		thrown(UsageException)
	}
}
