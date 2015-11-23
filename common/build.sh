#!/bin/bash

#This executes the full build and full copy of the target jar 

#mv .git/ .git_backup 2>/dev/null

#It can be run as followed:
# 	./build.sh
# or
# 	./build.sh --notest true

while [[ $# > 1 ]]
do
key="$1"

case $key in
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

if [[ -z "$NOTEST" ]];
then
	echo Executing the build with all unit tests...
	sbt clean assembly 
else
	echo Executing the build without any unit test...
	sbt 'set test in assembly := {}' clean assembly 
fi

mkdir ./release 2>/dev/null -ls
rm -rf ./release/latest/ 2>/dev/null -ls
mkdir -p ./release/latest/ 2>/dev/null -ls
cp target/scala-*/DevMaid-common*.jar ./release/latest/
cp target/scala-*/DevMaid-common*.jar ./release/

mkdir -p ./../lib/ 2>/dev/null -ls

#Copy the dependency jar to the project web folder
cp target/scala-*/DevMaid-common*.jar ./../web/lib/

#Copy the dependency jar to the project root folder
cp target/scala-*/DevMaid-common*.jar ./../lib/

