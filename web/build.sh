#!/bin/bash

#This executes the full build and full copy of the target jar 

#mv .git/ .git_backup 2>/dev/null


#you can build it without building the common dependency jar - the general library
#   ./build.sh --nocommon true
#It can be run as followed:
# 	./build.sh
# or
# 	./build.sh --notest true
# 	./build.sh --nocommon true
# 	./build.sh --notest true --nocommon true

#This executes the full build and full copy of the target jar 
mv ../git/ ../git_backup 2>/dev/null

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -w|--nocommon)
    NOCOMMON="$2"
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

function buildCommon {
    #build the web component
    mkdir -p ./lib/ 2>/dev/null -ls
    rm ./lib/DevMaid-common*.jar 2>/dev/null -ls
    cd ../common/
    ./build.sh
    cd ../web/
}

echo "Here are the parameter values: NOCOMMON = ${NOCOMMON}, NOTEST = ${NOTEST}"

if [[ -z "$NOCOMMON" ]];
then
    echo Building common component...
    buildCommon
else
    echo Skipping to build common component...
fi

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
cp target/scala-*/DevMaidWeb*.jar ./release/latest/
cp target/scala-*/DevMaidWeb*.jar ./release/

mkdir -p ./../lib/ 2>/dev/null -ls
cp target/scala-*/DevMaidWeb*.jar ./../lib/

