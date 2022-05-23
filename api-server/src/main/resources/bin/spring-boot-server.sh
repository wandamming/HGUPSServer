#!/bin/sh
serverName="hgups"
jarNamePrefix="api-server"
usage="Usage: ${serverName}.sh (start|stop)"

# if no args specified, show usage
if [ $# -ne 1 ]; then
  echo $usage
  exit 1
fi

prg=$0
startStop=$1
shift

# Get standard environment variables
prgDir=`dirname "$prg"`
#serverHome=`cd "$prgDir/.." >/dev/null; pwd`
serverHome="/opt/crafttime/hgups/api-server"
logDir="$serverHome/logs"
configPath="$serverHome/conf"
serverClassPath="$serverHome/lib"
serverPid="$configPath/$serverName.pid"

jarName=`ls $serverHome/lib | grep ^${jarNamePrefix}`

if [ -z "${jarName}" ];
then
      echo "main jarfile not found in $serverHome/lib"
      exit 1
fi
echo "find main jarName:${jarName}"
if [ -r "$serverHome/bin/setenv.sh" ]; then
  . "$serverHome/bin/setenv.sh"
else
  JAVA_OPTS="-d64 -server -Xms4g -Xmx4g -Xmn2g"
fi

mkdir ${logDir} >/dev/null 2>&1

check_before_start() {
    #ckeck if the process is not running
    if [ -f $serverPid ]; then
      if kill -0 `cat $serverPid` > /dev/null 2>&1; then
        echo $serverName running as process `cat $serverPid`.  Stop it first.
        exit 1
      fi
    fi
}

print_env() {
  echo "JRE_HOME:        $JAVA_HOME"
  echo "OFFLINE_ROLL_HOME:  $serverHome"
  echo "CLASSPATH:       $serverClassPath"
  echo "CONFIG_PATH      $configPath"
}

waitForProcessEnd() {
  pidKilled=$1
  processedAt=`date +%s`
  while kill -0 $pidKilled > /dev/null 2>&1;
   do
     echo -n "."
     sleep 1;
     # if process persists more than $REC_STOP_TIMEOUT (default 1200 sec) no mercy
     if [ $(( `date +%s` - $processedAt )) -gt ${REC_STOP_TIMEOUT:-1200} ]; then
       break;
     fi
   done
  # process still there : kill -9
  if kill -0 $pidKilled > /dev/null 2>&1; then
    echo -n force stopping $serverName with kill -9 $pidKilled
    $JAVA_HOME/bin/jstack -l $pidKilled > "$logDir/server.out" 2>&1
    kill -9 $pidKilled > /dev/null 2>&1
  fi
  # Add a CR after we're done w/ dots.
  echo
}

case $startStop in

(start)
    check_before_start
    print_env
    echo starting $serverName ...
    nohup $JAVA_HOME/bin/java -jar ${JAVA_OPTS} -Dloader.path=${serverClassPath} \
         -Dspring.config.location=${configPath}/ \
         -Dserver.home=${serverHome} \
         -Dlogging.config=${configPath}/logback.xml -Dfile.encoding=UTF-8 \
         ${serverClassPath}/${jarName}  >> ${logDir}/server.out 2>&1 &
    echo $! > $serverPid
  ;;

(stop)
    if [ -f $serverPid ]; then
      pidToKill=`cat $serverPid`
      # kill -0 == see if the PID exists
      if kill -0 $pidToKill > /dev/null 2>&1; then
        echo -n stopping $serverName
        echo "`date` Terminating $serverName" >> ${logDir}/server.out
        kill $pidToKill > /dev/null 2>&1
        waitForProcessEnd $pidToKill
      else
        retval=$?
        echo no $serverName to stop because kill -0 of pid $pidToKill failed with status $retval
      fi
    else
      echo no $serverName to stop because no pid file $serverPid
    fi
    rm -f $serverPid
  ;;

(*)
  echo $usage
  exit 1
  ;;
esac
