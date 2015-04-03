//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2014  Daniel Corder (contact: archiverify@dancorder.com)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.dancorder.Archiverify;

import java.security.Permission

public class ArchiverifyTest extends spock.lang.Specification {
	//qq:DCC better as a system test?
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
