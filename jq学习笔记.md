## jq的安装
1.`wget http://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm`
2.`rpm -ivh epel-release-latest-7.noarch.rpm`
3.`yum repolist`
4.`yum install jq`
## jq的使用
- 数字相等使用的是`==`,字符串相等使用的是`=`
- `jq '.' 172856.json`
  格式化输出172856.json文件
- `jq '.resultData | length' 172856.json`
  统计resultData的大小，如果resultData为数组，则是数组的长度，如果resultData为Map,则是Map的大小
- `jq '.resultData[] | (.taskId|tostring)+":"+(.isFinished|tostring)' 172856.json` 
  读取172856.json文件的属性resultData(resultData的值必须为数组)，并且把数组中的每一个元素中的taskId的值以及isFinished的值使用`:`分隔符拼接输出,resultData[]可以看作是遍历数组的每一项
- `jq '.resultData[] | select(.taskId == 668740187732516 and .isFinished == 0) |(.taskId|tostring)+":"+(.isFinished|tostring)' 111222.json`
  找出数组中符合select条件的元素并按照格式输出