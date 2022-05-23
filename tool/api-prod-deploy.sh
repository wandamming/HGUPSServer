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

admin_home=$(cd "$(dirname "$0")/.."; pwd)
echo admin_home:${admin_home}

cd ${admin_home}/common
mvn clean install
failExist
cd ../admin-api
mvn clean install
failExist

remote_path=/opt/hgups/express/api-server
scp ${admin_home}/admin-api/target/api-api-1.0-SNAPSHOT.jar root@xxxxx:${remote_path}/lib

#ssh root@huangbin "sh ${remote_path}/bin/stop.sh"
#sleep 3
#ssh root@huangbin "sh ${remote_path}/bin/start.sh"

