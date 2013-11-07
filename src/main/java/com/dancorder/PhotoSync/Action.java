package com.dancorder.PhotoSync;

import java.io.IOException;

public interface Action {
	public String toString();
	public void doAction() throws IOException;
}
