#!/bin/sh

SCRIPT_ABSPATH=$(readlink -f $0)

BASE_HOME=$(dirname $SCRIPT_ABSPATH)

app=$1

maven_setting=${BASE_HOME}/.maven/aliyun.xml

cmd="mvn -s ${maven_setting} -Dmaven.test.skip=true -U"

if [ "${app}" == "all" ]; then
  cmd="${cmd} clean package"
  exit 0
elif [ -z "${app}" ]; then
  echo "应用名称不能为空"
  exit 1
elif [ ! -d "${BASE_HOME}/${app}" ]; then
  echo "应用 ${app} 不存在"
  exit 1
else
  cmd="${cmd} -am -pl com.caiwillie.tools:${app} clean package"
fi

echo -e "正在打包: ${app}"

${cmd}
