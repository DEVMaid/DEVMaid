#!/bin/bash

if [ "$#" -gt "0" ]; then
	runCommand="sbt \"$SBT_TEST_ARG\""
	echo "runCommand: $runCommand"
	eval "$runCommand"
	
else
	echo "no SBT_TEST_ARG: $SBT_TEST_ARG"
	sbt test
fi

