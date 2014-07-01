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
