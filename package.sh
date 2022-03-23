#!/bin/sh

SCRIPT_ABSPATH=$(readlink -f $0)

BASE_HOME=$(dirname $SCRIPT_ABSPATH)

app=$1

if [ -z "${app}" ]; then
  echo "应用名称不能为空"
  exit 1
elif [ ! -d "${BASE_HOME}/${app}" ]; then
  echo "应用 ${app} 不存在"
  exit 1
fi

echo -e "正在打包: ${app}"

cmd="mvn"

cmd="${cmd} -Dmaven.test.skip=true -U -am -pl com.caiwillie.tools:${app} clean package"

${cmd}
