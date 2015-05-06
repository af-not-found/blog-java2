#!/bin/bash


SRC_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DEST_DIR="../../main/resources/public/static"

if [ ! -f "$YUI_JAR" ]; then
    echo env \$YUI_JAR is not defined or jar not found.
    exit 4;
fi;


function minify() {
	name=$1
	ext=${name##*.}
	
	pushd $SRC_DIR/$name > /dev/null
	
	cat *"$ext" > __tmp1
	if [ $? != 0 ]; then
		exit 1
	fi
	
	java -jar "$YUI_JAR" --charset utf-8 --type $ext __tmp1 -o __tmp2
	if [ $? != 0 ]; then
		exit 2
	fi

	mv __tmp2 $DEST_DIR/$name
	if [ $? != 0 ]; then
		echo current dir is `pwd`
		exit 3
	fi
	
	echo `echo *$ext` \=\> "$name"
	rm -f __tmp1 __tmp2
	
	popd > /dev/null
}


if [ -n "$1" ]; then
	DEST_DIR=$2
	minify $1
else
	minify admin.min.js
	minify blog.min.js
	minify blog.min.css
fi;


exit 0
