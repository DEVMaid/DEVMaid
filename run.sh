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

#There are serval ways to run the scripts, for example: 
#	./run.sh
#	./run.sh --config /etc/DEVMaid/config.json
#	./run.sh --webonly true
#	./run.sh --coreonly true

#mv .git/ .git_backup 2>/dev/null

while [[ $# > 1 ]]
do
key="$1"

case $key in
    -d|--debug)
    DEBUG="$2"
    shift # past argument
    ;;
    -c|--config)
    CONFIG="$2"
    shift # past argument
    ;;
    -w|--webonly)
    WEBONLY="$2"
    shift # past argument
    ;;
    -a|--coreonly)
    COREONLY="$2"
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

PARAMS=""
if [ -z "$CONFIG" ]
then
	echo "--config option is not set...so setting the default configuration file to: /etc/DEVMaid/config.json"
	CONFIG="/etc/DEVMaid/config.json"
else
	echo "Using this configuration file: $CONFIG"
fi
PARAMS="$PARAMS --config $CONFIG"

if [ ! -z "$WEBONLY" ]
then
	echo "--webonly option is set..."
	PARAMS="$PARAMS --webonly $WEBONLY"
fi

if [ ! -z "$COREONLY" ]
then
	echo "--coreonly option is set..."
	PARAMS="$PARAMS --coreonly $COREONLY"
fi

function deployWebapp {
    jar xf ./release/latest/DEVMaid*.jar webapp 
    rm -rf /etc/DEVMaid/webapp
    mkdir -p /etc/DEVMaid/webapp
    mv webapp /etc/DEVMaid/
}

deployWebapp

if [[ -z "$DEBUG" ]];
then
    echo Starting the core component in normal mode...
else
    echo Starting the web component in debug mode...
    DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8088"
    #DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8001,server=y  suspend=y"
fi


echo "PARAMS lists: $PARAMS"
eval "java $DEBUG -jar ./release/latest/DEVMaid*.jar $PARAMS"