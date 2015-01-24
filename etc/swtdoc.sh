#!/bin/sh

if [ "$1" ]; then
	SWTSRC=$1;
else 
	SWTSRC="`pwd`/../lib/swt_linux/swtsrc.zip";
fi

unzip $SWTSRC -d /tmp

SP=/tmp
cd $SP

javadoc -source 1.4 -sourcepath $SP -public -d /tmp/swt -subpackages `find $SP/org -type d | perl -ne '$_ =~ s|/tmp/||; $_ =~ s|\.?/|.|g;  print; ' `