#!/bin/sh
PRG="$0"
PRGDIR=`dirname "$PRG"`
BASE_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`
JAVA_HOME="/usr/lib/jvm/jre-1.8.0-openjdk"
#server内存配置
#export JAVA_OPTS="$JAVA_OPTS -Dorg.apache.catalina.security.SecurityListener.UMASK=`umask` -server -Xms2G -Xmx2G -Xmn512M -XX:PermSize=128M -XX:MaxPermSize=256M -Xss256k -XX:SurvivorRatio=6"

#gc
export JAVA_OPTS="$JAVA_OPTS -XX:MaxTenuringThreshold=4 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=2 -XX:+ExplicitGCInvokesConcurrent  -XX:+CMSScavengeBeforeRemark"

#gc日志
export JAVA_OPTS="$JAVA_OPTS -Xloggc:$BASE_HOME/logs/gc.log.`date +%Y-%m-%d_%H` -XX:+UseGCLogFileRotation -XX:GCLogFileSize=100M -XX:NumberOfGCLogFiles=20 -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -XX:+PrintTenuringDistribution -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:PrintFLSStatistics=1"

#内存溢出
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$BASE_HOME/logs/heapdump.hprof"