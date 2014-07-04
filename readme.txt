Archiverify is a command line Java application that is meant to be used to:
- Compare two copies of an archive and copy new files
- Generate and check hashes for files in the archives
- Highlight and (where possible) fix corrupt files
It also contains other related features that can be discovered from the usage message

To build the code and run the tests:
- Install Gradle - http://www.gradle.org/downloads
- From the root folder (where this file is) run "gradle build"
  - If you are on Windows and get an error about running git then you need to update git to a version that includes a windows git.exe (rather than git.cmd). e.g. msysgit v1.8.0 or later.
- The jar file is built to build/libs/

When building new versions commit to git first as the git hash (or tag) is automatically used to name the jar file.