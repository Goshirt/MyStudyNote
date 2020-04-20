## jvm的参数类型
1. 标准参数（各个版本稳定）。eg：`-help`,`-server`,`-client`。
2. 非标准参数（X参数，使用频率低）。eg：`-Xint`(解释执行),`-Xcomp`（第一次使用就编译成本地代码）。
3. 非标准参数（XX参数，使用频率高，用于jvm调优）。
   - Boolean类型：格式 `-XX:[+-]{name}`,表示根据`+``-`启动或者禁用指定的name属性。eg: `-XX:+UseConcMarkSweepGc`(启动UseConcMarkSweepGc收集器)
   
   - 非Boolean类型：格式`-XX:{name}={value}`，表示设置name属性的值为value 
   
## 查看jvm的参数
- `java -XX:+PrintFlagsFinal -version` 可以输出当前jvm`-XX`的所有参数，`=`表示的是默认的参数，`:=`表示的修改过的

- `jps`输出java的进程

- `jinfo -flags {进程id}`输出当前运行的java进程id的jvm参数。

- `jinfo -flag  {name} {进程id}`输出当前运行的java进程id的指定name的jvm参数。

## jstat查看jvm统计信息

- `jstat -class pid`指定pid的类加载情况
- `jstat `