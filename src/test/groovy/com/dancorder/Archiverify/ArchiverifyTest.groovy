package com.dancorder.Archiverify;

import java.security.Permission


public class ArchiverfiyTest extends spock.lang.Specification {
	
	def setupSpec() {
		// Tests will throw an ExitException if System.exit() is called rather than exiting the test runner.
		System.setSecurityManager(new NoExitSecurityManager());
	}
	
	def cleanupSpec() {
		System.setSecurityManager(null);
	}
	
	def "Invalid parameters cause exit with error"() {
		when: "A null parameter is passed to ArchiverfiyTest"	
		Archiverify.main(null);

		then: "expect ExitException"
		def e = thrown(ExitException)
		e.getStatus() != 0
		
		where:
		parameters    | _
		null          | _
		new String[0] | _
	}
	
	private static class ExitException extends SecurityException
	{
		public final int status;
		public ExitException(int status)
		{
			super("Application attempted to exit with code: " + status);
			this.status = status;
		}
		
		public int getStatus() {
			return status;
		}
	}
	
	private static class NoExitSecurityManager extends SecurityManager
	{
		@Override
		public void checkPermission(Permission perm)
		{
			// allow anything.
		}
		@Override
		public void checkPermission(Permission perm, Object context)
		{
			// allow anything.
		}
		@Override
		public void checkExit(int status)
		{
			super.checkExit(status);
			throw new ExitException(status);
		}
	}
}
