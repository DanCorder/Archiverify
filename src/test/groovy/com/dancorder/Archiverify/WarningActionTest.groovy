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
