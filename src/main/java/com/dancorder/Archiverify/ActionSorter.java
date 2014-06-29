package com.dancorder.Archiverify;

import java.util.ArrayList;
import java.util.List;

public class ActionSorter {
	private List<Action> actions;
	
	ActionSorter(List<Action> actions) {
		this.actions = actions != null ? actions : new ArrayList<Action>();
	}
	
	// We want to move warnings to the top of the list but otherwise preserve ordering
	List<Action> sortForDisplay() {
		ArrayList<Action> warnings = new ArrayList<Action>();
		ArrayList<Action> nonWarnings = new ArrayList<Action>();
		
		for (Action action : actions) {
			if (action instanceof WarningAction) {
				warnings.add(action);
			}
			else {
				nonWarnings.add(action);
			}
		}
		
		warnings.addAll(nonWarnings);
		
		return warnings;
	}
}
