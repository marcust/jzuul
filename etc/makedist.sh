#!/bin/sh

BASE=`pwd`/../
cd $BASE
echo "Working in $BASE";

echo -n "Enter Release version: "
read VERSION

if [ ! -d dist ]; then
	echo "Didn't find dist dir, running ant distro!"
	ant distros;
fi

if [ ! -d dist-$VERSION ]; then
	mkdir dist-$VERSION;
fi

for i in dist/jzuul*; do 
	echo $i;
	FILENAME=`basename $i | perl -ne 'print join "\n", ($_ =~ /([^.]+)\./)'`
	SUFFIX=`echo $i | perl -ne 'print join "\n", ($_ =~ /\.(.+)/)'`
	mv -v $i dist-$VERSION/$FILENAME-$VERSION.$SUFFIX
done
