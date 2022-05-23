#!/bin/sh
PRG="$0"
PRGDIR=`dirname "$PRG"`
EXECUTABLE=spring-boot-server.sh
exec "$PRGDIR"/"$EXECUTABLE" start "$@"