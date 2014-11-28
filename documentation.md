---
layout: default
title: Documentation
section: documentation
---

# What's it for?

The core use of Archiverify is to make one or more backups of a set of files and subsequently check the original files and backups for corruption.

It does this by scanning the supplied directories in parallel and generating hashes (fingerprints) for any files it finds. If a file is new then the generated hash will be stored in a hash file in the same directory as the new file. If the file already has a hash in the hash file then the stored hash will be checked against the newly calculated hash and if they differ Archiverify will show a warning.

By default Archiverify will scan your files, tell you what it is planning to do and show any warnings, and give you the chance to abort before it does anything.

# Examples

## Usage
You can get a list of options by running Archiverify with no parameters.

{% highlight bat %}
    Java -jar Archiverify-v1.0.0.jar
{% endhighlight %}

## First run
Imagine you have a directory "c:\originals" containing some files that you want to backup, you have never run Archiverify on it before. You connect a backup drive as e: and create a new empty folder called "backup". So your directories look like:

    originals          backup
    - file1.txt
    - directory1
      - file2.txt

You run:

{% highlight bat %}
    Java -jar Archiverify-v1.0.0.jar c:\originals e:\backup
{% endhighlight %}

Archiverify will run and once it has finished scanning c:\originals it will tell you that it is going to copy all the files from c:\originals to e:\backup and that it is going to write hash files to all the directories it has found. If you then tell Archiverify to proceed you will end up with:

    originals          backup
    - file1.txt        - file1.txt
    - hashfile.txt     - hashfile.txt
    - directory1       - directory1
      - file2.txt        - file2.txt
      - hashfile.txt     - hashfile.txt

##  Corrupt file
In this example you've done at least one backup previously so your files look like this:

    originals          backup
    - file1.txt        - file1.txt
    - hashfile.txt     - hashfile.txt
    - directory1       - directory1
      - file2.txt        - file2.txt
      - hashfile.txt     - hashfile.txt

However file2 in backup\directory1 has got corrupted somehow. So if you run Archiverify it will compare the files to the stored hashes and warn you that file2 is corrupt. If the stored hashes in both directories match each other and also match the hash calculated from originals\directory1\file2.txt then Archiverify will copy file2.txt from originals to backup to overwrite the corrupt file. The newly copied file is then scanned to calculate its hash and ensure that the copy was successful.

## Single directory mode
Archiverify can be run against a single directory to either generate new hashes, or to compare the files to pre-existing hashes. Archiverify will warn you if a file doesn't match a pre-existing hash.

{% highlight bat %}
    Java -jar Archiverify-v1.0.0.jar -s c:\originals
{% endhighlight %}

# Limitations

Archiverfiy is designed to work on sets of files that only ever added to e.g. a music collection or a set of original digital photos. It does not currently have a concept of a master copy so it treats both sets of files equally when comparing and add files from either directory to the other. This concept may be added in the future, but for now the only files Archiverify will delete are its own hash files. So, if you want to remove something from your backup you will need to delete it yourself from both the original location and the backup location.

