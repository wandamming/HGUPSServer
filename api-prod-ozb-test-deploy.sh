#!/usr/bin/env bash

failExist(){
    success_msg=$1
    fail_msg=$2
    if [ -z "${success_msg}" ]
    then
    success_msg="deploy success!"
    fi
    if [ -z "${fail_msg}" ]
    then
    fail_msg="deploy fail!"
    fi

    if [ $? == 0 ]
    then
        echo ${success_msg}
    else
        echo ${fail_msg}
        exit -1
    fi
}

admin_home=$(cd "$(dirname "$0")/"; pwd)

cd ./api-server
mvn clean install
failExist

remote_home=/opt/crafttime/hgups
remote_path=${remote_home}/api-server

#push jar
scp ${admin_home}/api-server/target/api-server-1.0-SNAPSHOT.jar root@www.onezerobeat.com:${remote_path}/lib

#push sql
scp ${admin_home}/sql/HGUPS.sql root@www.onezerobeat.com:${remote_home}
