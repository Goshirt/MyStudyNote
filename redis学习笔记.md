
## redis数据类型
#### String 字符串类型：
字符串内部有len以及capacity属性，capacity为字符串的内存大小，len为实际长度，支持动态扩容，当字符串长度小于1M时，扩容为成倍增加，如果超过1M,扩容时每次最多增加1M,字符串最大不能超过512M。如果一个字符串设置了过期时间，如果期间调用了set方法更改字符串，那么过期时间就会失效。

- `set key value`   设置一个键值对
- `set key value ex seconds` 设置一个键值对并且设置过期时间
- `keys *` 获取当前数据库的所有的key,还可以通过模式匹配获取
- `exists key` 判断key是否存在，存在就返回1，不存在返回0
- `del key [key2,key3]` 删除一个或者多个key
- `type key` 获取key对应的value的类型值
- `incr key` 自增1，当key代表的value不是数值类型时，报错
- `decr key` 自减1
- `incrby key incrNum`   按照指定的incrNum增加
- `decrby key decrNum`   按照指定的decrNum减少
- `append key value`     在key原来的值后面追加value,如果原来的key不存在，相当于set key value
- `strlen key`  返回key对应的value的长度
- `mget key [key2 key3]` 同时获取多个key对应的value值
- `mset key value [key2,value2 key3 value3]`  同时设置多个key-value
#### hash  散列类型：
- `hset key field value`  设置一个只有字段的散列列表，如果key已经存在，就追加，如果该filed已经存在，就更新value值
- `hmset key field value [field value]`  设置一个拥有一个或者多个字段的散列列表
- `hget key field` 获取key的字段field的value值
- `hmget key field [field]` 获取key字段的一个或者多个filed的value值
- `hgetall key`  返回key的所有filed及对应的value
- `hexists key fieled` 判断是否存在字段field
- `hsetnx key field`  当field本来不存在时设值，存在的话不进行任何操作
- `hincrby key field num`  使field字段的值增加num,如果key不存在，会创建
- `hdel key field [field]` 删除一个或者多个field
- `hkeys key`  获取key的所有字段名
- `hvals key`  获取key的所有字段对应的value值 
 #### list 列表（使用的是双向链表）：
- 常用来做异步队列，当列表中的最后一个元素被弹出的时候，列表自动被删除，内存回收。
- `lpush key value [value]`  在列表key的左边增加一个或者多个元素
- `rpush key value [value]`  在列表key的右边增加一个或者多个元素
- `lpop key` 在列表key的左边弹出一个元素（删除）
- `rpop key` 在列表key的右边弹出一个元素（删除）
- `llen key` 获取列表key的元素个数，当列表不存在时返回0
- `lrange key start end`  获取列表中指定范围的的数据集，列表索引从0开始，当start或者end为负数时，表示从右边开始数相应的单位
- `lrem key count value`  删除列表key中count个出现的value值，当count>0从左边开始，当count<0，从右边开始，当count=0,删除所有
- `lindex key index`  获取列表key中索引为index的值，会遍历列表，复杂度为O(n)
- `lset key index value` 在列表key中的索引index位置设置值为value
- `ltrim key start end` 删除列表key中指定范围以外的所有数据，可以实现定长的列表
- `linsert key before|after value value2` 在列表key中的value的前面或者后面插入value2
- `rpoplpush source destination` 把source列表转移到destination列表，从source最右边弹出一个元素，从destination最左边加入一个，每次返回该元素的值
#### set 集合：
- `sadd key value [value]`  在集合key中添加一个或者多个值，如果不存在key则创建一个
- `sremk key value [value]` 在集合key中删除一个或者多个值
- `smembers key` 获取集合key的所有值
- `scard key` 获取集合key的元素个数
- `sismember key member`  判断集合key中是否存在元素member
- `sdiff setA setB [setC]`   去掉集合A中出现在集合B中的元素，多个集合时，依次去掉
- `sinter setA setB [setC]`  求两个或者多个集合的交集
- `sunion setA setB [setC]`  求两个或者多个集合的并集
- `sdiffstore destination key key2 [key3]` 求两个或者多个集合的差集并结果存在destination集合中
- `sinterstore destination key key2 [key3]` 求两个或者多个集合的交集并将结果存在destination集合中
- `sunion destination key key2 [key3]` 求两个或者多个集合的并集并将结果存在destination集合中
- `srandmember key [count]`  随机获取集合中的一个元素，当指定count个数时，随机获取count个元素
- `spop key` 从集合中弹出一个元素
 #### zset 有序集合（在集合的基础上为每一个元素加上了一个分数）：
- `zadd key score member [score member]`  在有序集合中添加一个或者多个元素以及该元素的分数，如果该key不存在则创建，如果该元素存在则更新分数
- `zscore key member`  获取指定元素的分数
- `zrange ket start stop [withscores]`  按照元素分数从小到大的顺序返回下标start-stop范围的元素，下标从0开始，包含start，stop, 加上withscores时返回该元素的分数
- `ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]`  按照元素分数从小到大的顺序返回分数在min和max之间（包含min和max）的元素分数范围不包含端点值，可以在分数前加上“(”符号,-inf 和+inf分别表示负无穷和正无穷,limit同sql的用法
- `ZINCRBY key increment member` 给指定的key中的member元素增加increment分数
- `ZCARD key` 获得集合中元素的数量
- `ZCOUNT key min max` 获得指定分数范围內的元素个数
- `ZREM key member [member …]`  删除一个或多个元素
- `ZREMRANGEBYRANK key start stop` 按照元素分数从小到大的顺序（即索引0表示最小的值）删除处在指定排名范围内的所有元素
- `ZREMRANGEBYSCORE key min max`  删除指定分数范围内的所有元素
- `ZRANK key member` 获得元素的排名，从小到大的排名
- `ZREVRANK key member`  获得元素的排名，从大到小的排名
####  HyperLogLog
HyperLogLog可以用极小的空间用于大量数据的去重统计计数，12k的空间大约可以用于2^64个数的去重统计 
- `PFADD key element [element..]`添加指定的元素element到key中
- `PFCOUNT key [key..]`返回给定key的估算不重复元素值（估算误差大概为0.81%）
- `PFMERGET` destkey sourcekey  [sourcekey..] 将多个HyperLogLog合并到destkey中
#### Geo
地理位置
- `GEOADD key lng lat member` 将给定的维度、经度、名字添加到key中
- `GEOPOS key membet [member..]` 查找指定key中member的经纬度
- `GEODIST key member1 member2 [unit]`返回指定key中两个member之间的距离(可以指定距离的单位为 m/米 km/千米 mi/英里 ft/英尺)，如果其中一个member不存在，返回空值
- `GEORADIUS key lng lat radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [ASC|DESC] [COUNT count]`以给定的经纬度为中心，返回key中所有在radius半径范围内的元素，可选元素：`withcoord`将位置信息一并返回 `withdist` 将距离信息一并返回 `withhash`将hash值一并返回
- `GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [ASC|DESC] [COUNT count]`以给定的member为中心，返回key中所有在radius半径范围内的元素
- `GEOHASH key member [member …]`返回member的hash
##### 发布与订阅
- `publish channel message` 将消息message发布到指定的channel频道中
- `SUBSCRIBE channel [channel …]`订阅给定的一个或多个频道的信息
- `PSUBSCRIBE pattern [pattern …]`订阅一个或多个符合给定模式的频道，每个模式以 * 作为匹配符，比如 it* 匹配所有以 it 开头的频道( it.news 、 it.blog 、 it.tweets 等等)， news.* 匹配所有以 news. 开头的频道( news.it 、 news.global.today 等等)，诸如此类。
- `UNSUBSCRIBE [channel [channel …]]`退订给定的频道
- `PUNSUBSCRIBE [pattern [pattern …]]`退订所有匹配给定模式的频道
- `PUBSUB CHANNELS [pattern]`列出当前活跃的频道，如果不给出 pattern 参数，那么列出订阅与发布系统中的所有活跃频道。如果给出 pattern 参数，那么只列出和给定模式 pattern 相匹配的那些活跃频道。

### 事务（不支持回滚）
1. `MULTI`  开启事务，接下来的操作属于同一个事务(执行exec前的所有命令都会存放在一个队列结构体中)
2. 需要执行的命令
3. `EXEC`  执行事务（redis是单进程单线程机制工作机制的，依次执行队列结构体中保存的命令,如果期间出错，不进行回滚，但会结束事务）
4. `WATCH ` WATCH命令可以监控一个或多个键，一旦其中有一个键被修改（或删除），之后的事务就不会执行,监控一直持续到EXEC命令（事务中的命令是在EXEC之后才执行的，所以在MULTI命令后可以修改WATCH监控的键值）

### 时效：
- `EXPIRE key seconds`  设置一个键的生存时间，单位是秒
- `TTL key`      返回一个key的失效剩余时间
- `PERSIST key`  取消一个键设置的生存时间，使用SET或GETSET命令为键赋值也会同时清除键的生存时间


### Redis淘汰机制：（从已有缓存中按照淘汰机制删除一些数据）
  首先使用Redis淘汰机制需要先设置`maxmemory`，只有当已使用内存 >= `maxmemory` 才触发淘汰机制，每一个 Redis 对象都会设置相应的 lru，即最近访问的时间，每一次访问数据的时候，会更新 redisObject.lru，Redis 数据集数据结构中保存了键值对过期时间的表，即 server.expires，在使用 SET 命令的时候，就有一个键值对超时时间的选项
- `volatile-lru`：从已设置过期时间的数据集（server.expires）中挑选最近最少使用的数据淘汰并不保证是所有数据中的最近最少使用，而是随机挑选的一些数据中的最近最少使用）
- `volatile-ttl`：从已设置过期时间的数据集（server.expires）中挑选将要过期的数据淘汰（并不保证是所有数据中的 ttl 最大的键值对，而是随机挑选的一些数据中的ttl 最大的键值对）
- `volatile-random`：从已设置过期时间的数据集（server.expires）中任意选择数据淘汰
- `allkeys-lru`：从数据集（server.dict）中挑选最近最少使用的数据淘汰
- `allkeys-random`：从数据集（server.dict）中任意选择数据淘汰
-  `noeviction`：禁止驱逐数据，当内存使用达到阈值的时候，所有引起申请内存的命令会报错

### Redis数据持久化:(Redis 中的数据从内存中 dump 到磁盘)

- RDB:可以定时备份内存中的数据集。服务器启动的时候，可以从 RDB 文件中恢复数据集当前进程执行 。有两种模式手动命令触发和自动触发，手动触发通过两个命令`save`和`bgsave`命令，自动触发可以通过配置redis.conf文件配置RDB文件的文件名以及保存路径，自动触发使用的是`bsave`模式
  1. save(阻塞执行)
  2. BGSAVE（fork一个子进程执行）:是 fork 出一个子进程，把内存中的数据集整个 dump 到硬盘上

- AOF:可以记录服务器的所有写操作。在服务器重新启动的时候，会把所有的写操作重新执行一遍，从而实现数据备份。当写操作集过大（比原有的数据集还大），Redis 会重写写操作集。redis.conf中AOF的配置：
  `appendonly yes`           //启用aof持久化方式
  `appendfsync always` //每收到写命令就立即强制写入磁盘，最慢的，但是保证完全的持久化，不推荐使用
  `appendfsync everysec` //每秒强制写入磁盘一次，性能和持久化方面做了折中，推荐
  `no-appendfsync-on-rewrite  yes`  //正在导出rdb快照的过程中,要不要停止同步aof
  `auto-aof-rewrite-percentage 100`  //aof文件大小比起上次重写时的大小,增长率100%时,重写
  `auto-aof-rewrite-min-size 64mb `  //aof文件,至少超过64M时,重写
- AOF的流程
  1. 所有的写入命令(set hset ..)会append追加到aof_buf缓冲区中
  2. AOF缓冲区向硬盘做sync同步
  3. 随着AOF文件越来越大，需定期对AOF文件rewrite重写，达到压缩
  4. 当redis服务重启，可load加载AOF文件进行恢复

- RDB与AOF优缺点对比:
   1. RDB是压缩后的二进制文件，恢复速度比AOF快,但是无法做到实时持久化，丢失的数据多，每次持久化需要创建子进程，开销大，由于是二进制文件，存在兼容性问题.
   2. AOF由于是追加写的方式把命令加入文件中，可能对导致文件过大，而且恢复的速度比RDB慢
- 当RDB与AOF同时开启时,Redis重启的加载顺序：
  1. 优先加载AOF,启动
  2. 如果AOF文件不存在，加载RDB
### 案例
1.为了减轻服务器的压力，需要限制每个用户（以IP计）一段时间的最大访问量：
    对每个用户使用一个名为“rate.limiting:用户IP”的字符串类型键，每次用户访问则使用INCR命令递增该键的键值，如果递增后的值是1（第一次访问页面），则同时还要设置该键的生存时间为1分钟。这样每次用户访问页面时都读取该键的键值，如果超过了100就表明该用户的访问频率超过了限制，需要提示用户稍后访问。该键每分钟会自动被删除，所以下一分钟用户的访问次数又会重新计算，也就达到了限制访问频率的目的。
    isKeyExists=EXISTS rate.limiting: IP
    if isKeyExists is 1
        times=INCR rate.limiting: IP
    if times＞100
        print访问频率超过了限制，请稍后再试。
        exit
    else
        MULTI
        INCR rate.limiting: IP
        EXPIRE keyName, 60
        EXEC

2. 如果要精确地保证每分钟最多访问10次，需要记录下用户每次访问的时间。
    因此对每个用户，我们使用一个列表类型的键来记录他最近10次访问博客的时间。一旦键中的元素超过10个，就判断时间最早的元素距现在的时间是否小于1分钟。如果是则表示用户最近1分钟的访问次数超过了10次；如果不是就将现在的时间加入到列表中，同时把最早的元素删除。
        listLength=LLEN rate.limiting: IP
        if listLength＜10
            LPUSH rate.limiting: IP, now()
        else
            time=LINDEX rate.limiting: IP, -1
            if now()- time＜60
                print访问频率超过了限制，请稍后再试。
            else
                LPUSH rate.limiting: IP, now()
                LTRIM rate.limiting: IP, 0, 9
3. 教务网站要对全校所有学生的各个科目的成绩汇总排名，并在首页上显示前10名的学生姓名，
       由于计算过程较耗资源，所以可以将结果使用一个Redis的字符串键缓存起来。由于学生成绩总在不断地变化，需要每隔两个小时就重新计算一次排名，这可以通过给键设置生存时间的方式实现。每次用户访问首页时程序先查询缓存键是否存在，如果存在则直接使用缓存的值；否则重新计算排名并将计算结果赋值给该键并同时设置该键的生存时间为两个小时。
                rank=GET cache:rank
                if not rank
                    rank=计算排名...
                    MUlTI
                    SET cache:rank, rank
                    EXPIRE cache:rank, 7200
                    EXEC


​                                
 ### redis主从设置：（https://www.cnblogs.com/kevingrace/p/5685332.html）

    redis主从复制分为两种：
        （1）全量同步：一般发生在Slave初始化阶段，这时Slave需要将Master上的所有数据都复制一份
        （2）增量同步：Slave初始化后开始正常工作时主服务器发生的写操作同步到从服务器的过程。主服务器每执行一个写命令就会向从服务器发送相同的写命令，从服务器接收并执行收到的写命令
        
    主从同步的策略：
    主从刚刚连接的时候，进行全量同步；全同步结束后，进行增量同步。当然，如果有需要，slave 在任何时候都可以发起全量同步。redis 策略是，无论如何，首先会尝试进行增量同步，如不成功，要求从机进行全量同步。
    
    主从复制的特点：
    1）采用异步复制；
    2）一个主redis可以含有多个从redis；
    3）每个从redis可以接收来自其他从redis服务器的连接；
    4）主从复制对于主redis服务器来说是非阻塞的，这意味着当从服务器在进行主从复制同步过程中，主redis仍然可以处理外界的访问请求；
    5）主从复制对于从redis服务器来说也是非阻塞的，这意味着，即使从redis在进行主从复制过程中也可以接受外界的查询请求，只不过这时候从redis返回的是以前老的数据，如果你不想这样，那么在启动redis时，可以在配置文件中进行设置，那么从redis在复制同步过程中来自外界的查询请求都会返回错误给客户端；（虽然说主从复制过程中对于从redis是非阻塞的，但是当从redis从主redis同步过来最新的数据后还需要将新数据加载到内存中，在加载到内存的过程中是阻塞的，在这段时间内的请求将会被阻，但是即使对于大数据集，加载到内存的时间也是比较多的）；
    6）主从复制提高了redis服务的扩展性，避免单个redis服务器的读写访问压力过大的问题，同时也可以给为数据备份及冗余提供一种解决方案；
    7）为了编码主redis服务器写磁盘压力带来的开销，可以配置让主redis不在将数据持久化到磁盘，而是通过连接让一个配置的从redis服务器及时的将相关数据持久化到磁盘，不过这样会存在一个问题，就是主redis服务器一旦重启，因为主redis服务器数据为空，这时候通过主从同步可能导致从redis服务器上的数据也被清空；
    
    使用方法：
    在Redis中使用复制功能非常容易，只需要在从数据库的配置文件中加入“slaveof主数据库IP主数据库端口”即可，主数据库无需进行任何配置
    例如
        加任何参数来启动一个Redis实例作为主数据库：
            redis-server
        然后加上slaveof参数启动另一个Redis实例作为从数据库，并让其监听6380端口：
            redis-server --port 6380 --slaveof 127.0.0.1 6379
    此时在主数据库中的任何数据变化都会自动同步到从数据库中，在默认情况下从数据库是只读的，如果直接修改从数据库的数据会出现错误。可以通过设置从数据库的配置文件中的slave-read-only为no以使从数据库可写，但是对从数据库的任何更改都不会同步给任何其他数据库，并且一旦主数据库中更新了对应的数据就会覆盖从数据库中的改动。配置多台从数据库的方法也一样，在所有的从数据库的配置文件中都加上slaveof参数指向同一个主数据库即可。
    
    应用场景：
    （1）通过复制可以实现读写分离以提高服务器的负载能力。在常见的场景中，读的频率大于写，当单机的Redis无法应付大量的读请求时（尤其是
    较耗资源的请求，比如SORT命令等）可以通过复制功能建立多个从数据库，主数据库只进行写操作，而从数据库负责读操作。
    （2）另一个相对耗时的操作是持久化，为了提高性能，可以通过复制功能建立一个（或若干个）从数据库，并在从数据库中启用持久化，同时在主
    数据库禁用持久化。当从数据库崩溃时重启后主数据库会自动将数据同步过来，所以无需担心数据丢失。而当主数据库崩溃时，需要在从数据库中使
    用SLAVEOF NO ONE命令将从数据库提升成主数据库继续服务，并在原来的主数据库启动后使用SLAVEOF命令将其设置成新的主数据库的从数据库，即
    可将数据同步回来。

### redis的哨兵机制
    (1)为了解决maste发生故障时可以将slave升级为master,需要引入守护进程（daemon）,监视master以及slave.
    (2)单个daemon时无法解决可用性，需要引入多个daemon同时监听master以及slave。
    (3)多个daemon就会出现交互以及通信的问题，这时就引入哨兵sentinel机制。
    (4)每一个sentinel都会在他们共同的master上订阅相同的chanel,因此，新加入的sentinel只需要订阅这个chanel,然后发布一个消息（包含自己的信息），这样新加入的sentinel就会和之前存在的sentinel建立长连接。
    (5)sentinel会定期向master发送心跳，判断master的存活状态，一旦master没有响应，sentinel就会把master置为“主管不可用态”。
    (6)然后sentinel就会向其他的sentinel发送确认信息，当确认的sentinel节点数>quorum（可配置），master的状态就会被置                 为“客观不可用”。
    (7)然后sentinel通过一个选举，从salve中推选一个成为新的master。
### redis分布式锁
  - 在多个并发人物同时处理一个key时，由于读取数据到内存，修改成功后再返回不是原子操作，因此会导致并发问题的产生。
    > setnx lockName true  //设置锁
        ok           //ok证明设置锁成功
        do something  //做业务操作
        del lockName  //删除锁，用完之后必须释放
 - 这样就产生一个问题，如果在进行业务操作的时候，出现异常，导致没能执行del删除命令，就会导致**死锁的产生**，锁永远得不到释放，因此需要设置过期时间
    > setnx lockName true  //设置锁
        ok           //ok证明设置锁成功
        expire lockName 5 //设置5秒过期时间
        do something  //做业务操作
        del lockName  //删除锁，用完之后必须释放
- 问题同样来了，如果在setnx与expire之间服务进程挂掉，那么久无法给锁设置过期时间，**死锁的问题**照样会产生，同样也不能引入事务，因为expire的执行依赖于setnx获取锁成功，如果setnx没有抢到锁，是不应该执行expire的，好在reids2.8之后新增了一个set指令的扩展参数，使得setnx可以与expire一起执行
  > set lockName true ex 5 //设置锁同时设置过期时间5秒
  ok //证明设置成功
- 问题又来了，**分布式锁超时的问题**，如果第一个线程在执行业务操作的时候时间过长，超过了锁的过期时间，这时由于锁过期，第二个线程就会获取到锁，执行任务，在第二个线程执行任务期间，第一个线程执行完了任务，然后执行del命令释放锁，这时来了第三个线程，获取到了锁，同样执行任务，这就导致了第二个线程在执行任务的时候，第三个线程也获取到了锁，解决办法可以为set指令设置一个随机数，在释放锁的时候先匹配随机数是否一致，如果一致再释放锁，这就在第二线程获取锁之后随机数已经发生了改变，第一个锁执行完任务之后del命令无法执行，将由第二个线程执行完任务之后del，由于判断随机数是否相等，然后执行del命令不是原子操作，就需要使用Lua脚本处理
### redis缓存雪崩
同一时间，大量的热点数据过期，导致大量请求打到DB,解决办法给过期时间加上一个随机值
### redis缓存穿透
使用一些数据库根本不存在的值进行查询（缓存不可能存在的key），导致大量的请求直接到达数据库，接口参数过滤，或者使用布隆过滤器，把所有数据库真实存在的数据加载在bitmap中，通过布隆过滤算法，把数据库不存在的数据的请求过滤掉。
### 缓存击穿
当一个热点数据失效的时候，同一时间大量的请求同时打到DB中，导致DB扛不住。解决办法：
1. 热点数据永不过期。
2. 当大量请求到来时，缓存不存在时，加锁获取DB数据，获取到之后，把数据更新到缓存中，没有获取到锁的线程sleep一定时间，从新请求。
### bitmap（同样可以解决10亿个数据中快速判断一个元素是否存在）
 - 其实就是一个只存01数据的数组，用0代表不存在，1代表存在，不同的key通过多次hash算法，判断对应的位置是否都为1（一次hash算法判断的位置只有一个），由于hash碰撞的存在，经过多次hash可以降低误判率，但不能消除。Guava框架中实现了一个布隆过滤器，新增数据的同时需要往布隆过滤器中添加元素，但是由于guava框架中的布隆过滤器是由普通的bitmap实现的，因此不能有删除方法，因为置0的位置有可能是其他元素某一次hash的置1位，导致这个元素在往后的判断中被判不存在。如果要实现删除，可以通过增加一个计数器，给每一个bit增加一个计数器，一次hash命中，给计数器+1，如果删除时计数器-1，-1后如果计数器的结果为0，证明没有其他元素hash到该为，置0，否则不能置0.


#### reids 安装
 1. 安装,进入指定的文件后获取reids压缩包
    `wget http://download.redis.io/releases/redis-5.0.4.tar.gz`
 2. 解压文件
    `tar xzvf redis-5.0.4.tar.gz`
 3. 进入刚刚解压的文件夹中 进行编译
    `make`
 4. 安装reids 到路径/home/redis
    `make PREFIX=/home/redis install` 
 5. 在redis-5.0.4文件夹中找到redis.conf,然后移动到redis的安装目录中
      `mv redis.conf /home/redis`
 6. 把redis改为后台启动服务，更改redis.conf文件中的
     `daemonize no` 改为`daemonize yes`
  7. 开启远程登录，注释掉`bind 127.0.0.1` ,将`protected-mode yes` 改为`protected-mode no` ,取消`requiredpass foobared` 的注释，并将`foobared` 改成自己的密码口令
  8. 进入redis的安装目录,并且以redis.conf配置文件的方式启动redis服务
      `bin/redis-server redis.conf`
  9. 进入redis的安装目录，启动redis 客户端

    `bin/redis-cli`
#### redis 的主从设置
  - 根据上边的安装方法，安装好两个redis，先关闭redis服务
  - 修改redis.conf配置文件
  - 作为master的redis.conf只需要把`127.0.0.1`改为`0.0.0.0`或者直接注释掉,然后找到 `logfile`并指定一个日志文件名
  - 作为slave的redis.conf同样按照master的改法，然后在任意地方添加新的一行`slaveof slaveIP` slaveIP为slave节点的ip地址，如果
  - 如果master设置了登录密码，需要在配置文件种添加一行 `masterauth {password`}
## reids慢查询日志配置
- 在reids.conf下配置：
  `slowlog-log-slower-than 10000`  记录超过10000微妙也就是10毫秒的命令，对于高并发时可以将10000改为1000
  `slowlog-max-len 128` 记录慢日志的队列大小，当超过队列大小是，把队头的移出队列
- `slowlog get` 获取慢查询的队列内容
- `slowlog len`获取慢查询队列的长度
- `slowlog reset`重置慢查询队列
- 由于慢查询的记录存在队列中，需要定期执行`slowlog get`并将结果转存其他地方
#### redis性能测试
- `bin/redis-benchmark -h ip -p port -c 100 -n 10000` 100个并发连接，1000个请求，测试命令的性能
- - `bin/redis-benchmark -h ip -p port -t set,get -n 100000 -q` 100个并发连接，1000个请求，测试sest & get命令的性能
- `bin/redis-benchmark -h ip -p port -q -d 100` 测试存取大小为100字节的数据包的性能

#### reids为什么这么快
- 内存操作 
- 单线程 
- 多路复用
- RESP协议（redis的底层协议,学习之后可以通过socket手写jedis）
#### hash的内部编码
- ziplist（压缩列表）
当哈希类型的元素个数小于hash-max-ziplist-entries配置（默认512个），同时所有值都小于hash-maxziplist-value配置（默认为64字节），Redis会使用ziplist做为哈希的内部实现。Ziplist可以使用更加紧凑的结构来实现多个元素的连续存储，所以在节省内存方面更加优秀。
- hashtable（哈希表）
当哈希类型无法满足ziplist要求时，redis会采用hashtable做为哈希的内部实现，因为此时ziplist的读写效率会下降
#### 利用redis的RESP协议快速导入mysql的表数据
- `mysql -uuserName -ppassword dbName --skip-column-names --raw < test.sql | ./redis-cli -h url -p port -a password2 --pipe` 
  1. `userName` 连接数据库的用户名
  2. `password` 连接数据库的密码
  3. `dbName` 连接的数据名
  4. `url` reids的ip地址
  5. `port` redis的连接端口
  6. `password2` reids的连接密码
  7. `test.sql` 拼接的sql,可以访问teset.sql的命令
  8. `./redis-cli` 可以启动redis client的命令

- test.sql内容：
>  ` SELECT CONCAT( 
"*20\r\n", 
'$',LENGTH(redis_cmd),'\r\n',redis_cmd,'\r\n', 
'$',LENGTH(redis_key),'\r\n',redis_key,'\r\n', 
'$',LENGTH(filed_blogId),'\r\n',filed_blogId,'\r\n', 
'$',LENGTH(blogId),'\r\n',blogId,'\r\n', 
'$',LENGTH(filed_tile),'\r\n',filed_tile,'\r\n', 
'$',LENGTH(title),'\r\n',title,'\r\n', 
'$',LENGTH(filed_summary),'\r\n',filed_summary,'\r\n', 
'$',LENGTH(summary),'\r\n',summary,'\r\n', 
'$',LENGTH(filed_releaseDate),'\r\n',filed_releaseDate,'\r\n', 
'$',LENGTH(releaseDate),'\r\n',releaseDate,'\r\n', 
'$',LENGTH(filed_clickNum),'\r\n',filed_clickNum,'\r\n', 
'$',LENGTH(clickNum),'\r\n',clickNum,'\r\n', 
'$',LENGTH(filed_replyNum),'\r\n',filed_replyNum,'\r\n', 
'$',LENGTH(replyNum),'\r\n',replyNum,'\r\n', 
'$',LENGTH(filed_content),'\r\n',filed_content,'\r\n', 
'$',LENGTH(content),'\r\n',content,'\r\n', 
'$',LENGTH(filed_typeId),'\r\n',filed_typeId,'\r\n', 
'$',LENGTH(typeId),'\r\n',typeId,'\r\n', 
'$',LENGTH(filed_keyWord),'\r\n',filed_keyWord,'\r\n' 
'$',LENGTH(keyWord),'\r\n',keyWord,'\r') 
FROM( 
SELECT 
'HMSET' as redis_cmd, 
't_blog' as redis_key, 
'blogId' as filed_blogId, 
blogId as blogId, 
'title' as filed_tile, 
title as title, 
'summary' as filed_summary, 
summary as summary, 
'releaseDate' as filed_releaseDate, 
releaseDate as releaseDate, 
'clickNum' as filed_clickNum, 
clickNum as clickNum, 
'replyNum' as filed_replyNum, 
replyNum as replyNum, 
'content' as filed_content, 
content as content, 
'typeId' as filed_typeId, 
typeId as typeId, 
'keyWord' as filed_keyWord, 
keyWord as keyWord 
FROM t_blog)AS ttt;`
## jedis pipelined实现批量操作
使用pipelined进行批量操作可以减少因为网络环境引起的性能开销
> Pipelined pipelined = jedis.pipelined();
 for(){
    pipelined.del(key);//不提交
}
pipeliend.sync();//提交
