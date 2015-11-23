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
# Date: 2015 October
# 
#you can run it with full build and test
#   ./run-test.sh
#Or you run it without building the dependency
#   ./run-test.sh --noweb true

#Or you run it with only a single test
#   ./run-test.sh -t ConfigurationTest-ConvertFromTestJsonFile_invalidFormat_duplicateSourceFolder

#Or both
#   ./run-test.sh --noweb true -t ConfigurationTest-ConvertFromTestJsonFile_invalidFormat_duplicateSourceFolder

mv .git/ .git_backup 2>/dev/null

if [ ! -d ./lib/ ]; then
	mkdir ./lib/ 
fi

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -d|--debug)
    DEBUG="$2"
    shift # past argument
    ;;
    -w|--noweb)
    NOWEB="$2"
    shift # past argument
    ;;
    -t|--test)
    TESTSINGLE="$2"
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

#build the common component
#cd common
#./build.sh
#cd ..

function buildWeb {
    #build the web component
    rm ./lib/DevMaidWeb*.jar 2>/dev/null -ls
    cd web/
    ./build.sh
    cd ../
}

if [[ -z "$NOWEB" ]];
then
    echo Building web component...
    buildWeb
else
    echo Skipping to build web component...
fi

#build the web component
#cd web
#./build.sh
#cd ..

if [[ -z "$DEBUG" ]];
then
    echo Running in normal mode...
    export JAVA_OPTS=""
else
    echo Running in debug mode...
    export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8088"
fi

if [[ -z "$TESTSINGLE" ]];
then
    export SBT_TEST_ARG=""
    echo "No argument is passed, so I am going to execute all tests..."
    ./tests/test.sh
else
    echo "A specific test is mentioned, so I am going to execute this test..."
    cd tests/
    source $TESTSINGLE
    cd ../
    ./tests/test.sh "$SBT_TEST_ARG"
fi


