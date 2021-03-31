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

- `jstat -gc {pid} 1000 `  每一秒输出gc收集数据

  > - S0C：第一个幸存区的大小
  > - S1C：第二个幸存区的大小
  > - S0U：第一个幸存区的使用大小
  > - S1U：第二个幸存区的使用大小
  > - EC：伊甸园区的大小
  > - EU：伊甸园区的使用大小
  > - OC：老年代大小
  > - OU：老年代使用大小
  > - MC：方法区大小
  > - MU：方法区使用大小
  > - CCSC:压缩类空间大小
  > - CCSU:压缩类空间使用大小
  > - YGC：年轻代垃圾回收次数
  > - YGCT：年轻代垃圾回收消耗时间
  > - FGC：老年代垃圾回收次数
  > - FGCT：老年代垃圾回收消耗时间
  > - GCT：垃圾回收消耗总时间

- 

  