#!/bin/bash

DEMO=$1
PORT=$2

DIR="$( cd "$( dirname "$0" )" && pwd )"

#location of the server binary
SERVER_LOCATION=test/appengine-java-sdk-repacked/bin/dev_appserver.sh
#parent directory of the demos
DEMO_DIRECTORY=demos

if [ -z "$DEMO" ]
then
	echo "usage: $0 <demo-name> [port]"
	exit 1
fi

#run the server binary, with port if specified by user
if [ -z "$PORT" ]
then
	$SERVER_LOCATION "$DIR/$DEMO_DIRECTORY/$DEMO/war"
else
	$SERVER_LOCATION --port=$PORT "$DIR/$DEMO_DIRECTORY/$DEMO/war"
fi
