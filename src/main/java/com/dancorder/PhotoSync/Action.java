package com.dancorder.PhotoSync;

import java.io.IOException;

interface Action {
	String toString();
	void doAction() throws IOException;
}
