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


public class ActionSorterTest extends spock.lang.Specification {

	def "Sort nothing for display"() {
		setup:
		def sorter = new ActionSorter(input)
		
		when: "Sorting a null or empty list"
		def output = sorter.sortForDisplay()		

		then: "return an empty list"
		output != null
		output.size() == 0

		where:
		input           | _
		null            | _
		new ArrayList() | _
	}
	
	def "Sort warning actions to start of list"() {
		setup:
		def input = new ArrayList<Action>();
		input.add(action1);
		input.add(action2);
		def sorter = new ActionSorter(input)
		
		when: "Sorting a null or empty list"
		def output = sorter.sortForDisplay()

		then: "return an empty list"
		output != null
		output.size() == 2
		output[0] instanceof WarningAction
		output[1] instanceof UpdateHashesAction

		where:
		action1                           | action2
		new WarningAction("test")         | new UpdateHashesAction(Mock(FileHashStore))
		new UpdateHashesAction(Mock(FileHashStore)) | new WarningAction("test")
	}
}
