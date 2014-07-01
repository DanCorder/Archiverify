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

import java.nio.file.Paths

public class UpdateHashesActionTest extends spock.lang.Specification {
	def "Null FileHashStore"() {
		when: "A null parameter is passed"
		new UpdateHashesAction(null)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)
	}

	def "Do action"() {
		setup: "Create the action"
		def store = Mock(FileHashStore)
		def action = new UpdateHashesAction(store)

		when: "it does the action"
		action.doAction()

		then: "the hash store is written out"
		1 * store.write()
		then: 0 * _._
	}

	def "String value"() {
		setup: "Create the action"
		def store = Mock(FileHashStore)
		store.getDirectory() >> Paths.get("dir1")
		def action = new UpdateHashesAction(store)

		expect:
		action.toString() == "Write hashes to dir1"
	}
}