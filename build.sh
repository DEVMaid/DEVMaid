#!/bin/bash

# Copyright (c) 2015 Ken Wu
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#
# -----------------------------------------------------------------------------
#
# Author: Ken Wu
# Date: 2015 November
#

#you can build it without building the web dependency jar - the web component
#   ./build.sh --noweb true
#Or you can build it without running the unit tests
#	./build.sh --notest true
#Or both
#   ./build.sh --notest true --nocommon true
#	./build.sh --notest true --noweb true
#   ./build.sh --notest true --nocommon true --noweb true

#This executes the full build and full copy of the target jar 
mv .git/ .git_backup 2>/dev/null

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -w|--noweb)
    NOWEB="$2"
    shift # past argument
    ;;
    -t|--notest)
    NOTEST="$2"
    shift # past argument
    ;;
    --default)
    DEFAULT=YES
    ;;
    *)
            # unknown option
    ;;
esac
shift # past argument or value
done

echo "Here are the parameter values: NOWEB = ${NOWEB}, NOTEST = ${NOTEST}"

#function buildCommon {
#    #build the web component
#    rm ./lib/DevMaid-common*.jar 2>/dev/null -ls
#    cd common/
#    ./build.sh
#    cd ../
#}

function buildWeb {
	#build the web component
	rm ./lib/DevMaidWeb*.jar 2>/dev/null -ls
	cd web/
	./build.sh
	cd ../
}

#if [[ -z "$NOCOMMON" ]];
#then
#    echo Building common component...
#    buildCommon
#else
#    echo Skipping to build common component...
#fi

if [[ -z "$NOWEB" ]];
then
	echo Building web component...
	buildWeb
else
	echo Skipping to build web component...
fi

if [[ -z "$NOTEST" ]];
then
	echo Executing the build with all unit tests...
	sbt clean assembly
else
	echo Executing the build without any unit test...
	sbt 'set test in assembly := {}' clean assembly
fi

rm ./release/latest/DEV*.jar 2>/dev/null -ls
cp target/scala-*/DEV*.jar ./release/
cp target/scala-*/DEV*.jar ./release/latest/
