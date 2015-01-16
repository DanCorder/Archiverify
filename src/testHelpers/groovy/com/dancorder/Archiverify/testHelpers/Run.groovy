//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2015  Daniel Corder (contact: archiverify@dancorder.com)
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

package com.dancorder.Archiverify.testHelpers


class Run {
	public static RunResult archiverify(String... params) {
		def command = [ "java", "-jar", FileSystem.getJarFile().toString() ]
		params.each() { command << it }
		
		def commandArray = command.toArray(new String[command.size()]);
		
		def process = Runtime.getRuntime().exec(commandArray, null, FileSystem.getBuildOutputDirectory().toFile())
		process.waitFor()
		
		return new RunResult(process)
	}
}
