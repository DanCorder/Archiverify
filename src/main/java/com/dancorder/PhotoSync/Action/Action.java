package com.dancorder.PhotoSync.Action;

import java.io.IOException;

public interface Action {
	String toString();
	void doAction() throws IOException;
}
