Archiverify is a command line Java application that is meant to be used to:
- Compare two copies of an archive and copy new files
- Generate and check hashes for files in the archives
- Highlight and (where possible) fix corrupt files
It also contains other related features that can be discovered from the usage message

To build the code and run the tests:
- From the root folder (where this file is) run "gradlew buildRelease" on Windows or "./gradlew buildRelease" on Mac/Linux
  - If you are on Windows and get an error about running git then you need to update git to a version that includes a windows git.exe (rather than git.cmd). e.g. msysgit v1.8.0 or later. Alternatively you can look at getVersionTag() in build.gradle in commit cc2dd7224652a6961f0423339fb3b50f3e71f2fc which works with old versions of git.
- The jar file is built to build/libs/

When building new versions for distribution you should commit to git first as the git hash (or tag) is automatically used to name the jar file.


Dependencies
Archiverify uses a number of open source libraries and tools.

The compiled application uses the libraries listed in the "Compile" dependecies section of the gradle.build file as well as One-Jar - http://one-jar.sourceforge.net/.

During compilation gradle uses the gradle-one-jar plugin - https://github.com/rholder/gradle-one-jar. A copy of the plugin is saved in the gradleplugins folder for convenience.

The "testCompile" dependencies section of the gradle.build file lists the libraries and tools used to run Archiverify's automated tests.