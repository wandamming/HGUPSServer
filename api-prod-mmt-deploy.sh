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

remote_home=/opt/shipstous/shipstous
remote_path=${remote_home}/api-server
remote_host=www.mai2mai.com

#push jar
scp ${admin_home}/api-server/target/api-server-1.0-SNAPSHOT.jar root@${remote_host}:${remote_path}/lib

#push sql
#scp ${admin_home}/sql/shipstous.sql root@${remote_host}:${remote_home}
