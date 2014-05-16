package com.dancorder.PhotoSync;


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
