#!/bin/sh

CLASSPATH=$CLASSPATH:./lib/swt_linux/swt.jar:./lib/swt_linux/swt-pi.jar:./lib/jdom.jar

MAIN=org.jzuul.gdk.Main
GCJ="gcj --main=$MAIN"

GDKDIR=org/jzuul/gdk
SWTDIR=$GDKDIR/swt
FILES="$GDKDIR/Main.java `find $SWTDIR -name *.java`"

echo "Classpath:"
echo $CLASSPATH
echo
echo "Compiling Files:"
echo $FILES

$GCJ --classpath $CLASSPATH $EXTRA -o GameEditor $FILES