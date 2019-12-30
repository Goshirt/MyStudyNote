# 容器：
## (1)Collection
  * List:按照插入顺序保存的一组元素（线性表结构|动态数组）
     ArrayList:随机访问快，插入和移出慢
     LinkedList:随机访问慢，插入移出快,可以实现栈的所有功能   https://juejin.im/post/5aa299c1518825557b4c5806#heading-15

  * Set:不能有重复的元素,对快速查找进行了优化（通过hash（）方法以及equal（）方法去重）
      HashSet:散列表
      TreeSet:存储在红黑树数据结构中，可以在构造函数中传递一个Comparator<T>进行排序
      LinkedHashSet:散列表，并且使用链表维护元素的插入顺序
            
  * Queue:按照排队规则来确定对象的产生顺序，先进先出
     BlockingQueue的核心方法：
     
     > 放入数据：
       offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,
         则返回true,否则返回false.（本方法不阻塞当前执行方法的线程）
       offer(E o, long timeout, TimeUnit unit),可以设定等待的时间，如果在指定的时间内，还不能往队列中
         加入BlockingQueue，则返回失败。
       put(anObject):把anObject加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断
         直到BlockingQueue里面有空间再继续.
       
     > 获取数据：
         poll(time):取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,
           取不到时返回null;
         poll(long timeout, TimeUnit unit)：从BlockingQueue取出一个队首的对象，如果在指定时间内，
           队列一旦有数据可取，则立即返回队列中的数据。否则知道时间超时还没有数据可取，返回失败。
         take():取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到
           BlockingQueue有新的数据被加入; 
         drainTo():一次性从BlockingQueue获取所有可用的数据对象（还可以指定获取数据的个数）， 
           通过该方法，可以提升获取数据效率；不需要多次分批加锁或释放锁。
### BlockingQueue实现类：
  1.LinkedBlockingQueue
  > 基于链表的阻塞队列，内部也维持着一个数据缓冲队列（该队列由一个链表构成），当生产者往队列中放入一个数据时,队列会从生产者手中获取数据，并缓存在队列内部，而生产者立即返回；只有当队列缓冲区达到最大值缓存容量时（LinkedBlockingQueue可以通过构造函数指定该值），才会阻塞生产者队列，直到消费者从队列中消费掉一份数据，生产者线程会被唤醒，反之对于消费者这端的处理也基于同样的原理。LinkedBlockingQueue之所以能够高效的处理并发数据，还因为其对于生产者端和消费者端分别采用了独立的锁来控制数据同步，这也意味着在高并发的情况下生产者和消费者可以并行地操作队列中的数据。容量是没有上限的（说的不准确，在不指定时容量为Integer.MAX_VALUE，），但是也可以选择指定其最大容量，最好指定容量大小如果生产者的速度一旦大于消费者的速度，也许还没有等到队列满阻塞产生，系统内存就有可能已被消耗殆尽了

 2.ArrayBlockingQueue
  > 基于数组的阻塞队列实现，在ArrayBlockingQueue内部，维护了一个定长数组，以便缓存队列中的数据对象，这是一个常用的阻塞队列，除了一个定长数组外，ArrayBlockingQueue内部还保存着两个整形变量，分别标识着队列的头部和尾部在数组中的位置ArrayBlockingQueue在生产者放入数据和消费者获取数据，都是共用同一个锁对象，由此也意味着两者无法真正并行运行。在插入或删除元素时不会产生或销毁任何额外的对象实例。在构造时需要指定容量， 并可以选择是否需要公平性，如果公平参数被设置true，等待时间最长的线程会优先得到处理（其实就是通过将ReentrantLock设置为true来 达到这种公平性的：即等待时间最长的线程会先操作）。通常，公平性会使你在性能上付出代价，只有在的确非常需要的时候再使用它。它是基于数组的阻塞循环队 列，此队列按 FIFO（先进先出）原则对元素进行排序

  3.PriorityBlockingQueue
   > 是一个带优先级的 队列，而不是先进先出队列。元素按优先级顺序被移除，该队列也没有上限（PriorityBlockingQueue是对 PriorityQueue的再次包装，是基于堆数据结构的，而PriorityQueue是没有容量限制的，与ArrayList一样，所以在优先阻塞队列上put时是不会受阻的。虽然此队列逻辑上是无界的，但是由于资源被耗尽，所以试图执行添加操作可能会导致 OutOfMemoryError），但是如果队列为空，那么取元素的操作take就会阻塞，所以它的检索操作take是受阻的。另外，入该队列中的元素要具有比较能力。

  4.DelayQueue
  >（基于PriorityQueue来实现的）是一个存放Delayed 元素的无界阻塞队列，只有在延迟期满时才能从中提取元素。该队列的头部是延迟期满后保存时间最长的 Delayed 元素。如果延迟都还没有期满，则队列没有头部，并且poll将返回null。当一个元素的 getDelay(TimeUnit.NANOSECONDS) 方法返回一个小于或等于零的值时，则出现期满，poll就以移除这个元素了。此队列不允许使用 null 元素。常见的例子比如使用一个DelayQueue来管理一个超时未响应的连接队列
## (2) Map
    HashMap:K-V存储     https://juejin.im/post/5ac83fa35188255c5668afd0#heading-1   
## 迭代器Iterrator：
    （1）使用方法iterator()要求容器返回一个Iterator.Iterator将准备好返回序列的第一个元素
    （2）使用next()获得序列中是否还有元素
    （3）使用hasNext（）检查序列中是否还有元素
    （4）使用remove()将迭代器新返回的元素删除
### LinkedHashMap构建一个简单的LRU
    1.新建一个类继承LinkedHashMap
    2.调用父类的构造方法
    > super(maxCapacity, DEFAULT_LOAD_FACTOR, true);  
    2.重写removeEldestEntry（）方法
    >  示例用法：此重写允许映射增加到 100 个条目，然后每次添加新条目时删除最旧的条目，始终维持 100 个条目的稳定状态。 

     private static final int MAX_ENTRIES = 100;

     protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_ENTRIES;
     }


  ListIterator:一个更加强大的Iterator的子类型，只能用于各种List类的访问，并且可以双向移动
     listIterator()产生一个指向List开始处的ListIterator
     listIterator(n)产生一个指向索引为n的元素处的ListIterator

## 常见问题：
### HashSet如何保证不重复
    底层源码：HashSet的add方法（map是一个ransient类型的HashMap<E,Object>）：
        public boolean add(E e) {
          return map.put(e, PRESENT)==null;
        }
     而HashMap的put方法：
      public V put(K key, V value) { 
        if (key == null) return putForNullKey(value); 
        int hash = hash(key.hashCode()); 
        int i = indexFor(hash, table.length); 
        for (Entry<K,V> e = table[i]; e != null; e = e.next) { 
            Object k; 
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value; e.value = value; 
                e.recordAccess(this); return oldValue; 
             } 
         } 
         modCount++; addEntry(hash, key, value, i); return null; 
     }
      通过put方法的for循环可以看到是遍历整个表，然后通过hash值以及equals（）判断新添加的元素是否已经存在
      如果hash码值相同，且equles判断相等，说明元素已经存在，不存
      如果hash码值相同，且equles判断不相等，说明元素不存在，存
### TreeSet怎么保证数据的顺序
      当使用TreeSet的add方法是，add()的元素必须实现了Comparable接口，否则会报错因为保证数据的顺序是通过Comparable实现的
      例如添加persion实体
      public class Person implements Comparable<Person> {
          private String name;
          private int age;
          ...
          public int compareTo(Person o) {
              return 0;                //当compareTo方法返回0的时候集合中只有一个元素
              return 1;                //当compareTo方法返回正数的时候集合会怎么存就怎么取
              return -1;                //当compareTo方法返回负数的时候集合会倒序存储
          }
      }
      为什么返回0，只会存一个元素，返回-1会倒序存储，返回1会怎么存就怎么取呢？原因在于TreeSet底层其实是一个二叉树机构，且每插入一个新元素(第一个除外)都会调用```compareTo()```方法去和上一个插入的元素作比较，并按二叉树的结构进行排列。
        1. 如果将```compareTo()```返回值写死为0，元素值每次比较，都认为是相同的元素，这时就不再向TreeSet中插入除第一个外的新元素。所以TreeSet中就只存在插入的第一个元素。
        2. 如果将```compareTo()```返回值写死为1，元素值每次比较，都认为新插入的元素比上一个元素大，于是二叉树存储时，会存在根的右侧，读取时就是正序排列的。
        3. 如果将```compareTo()```返回值写死为-1，元素值每次比较，都认为新插入的元素比上一个元素小，于是二叉树存储时，会存在根的左侧，读取时就是倒序序排列的。


### Arrays.sort()的内部排序算法
    if(length<286){
        if(length<47){
            插入排序
         }else{
            快速排序
         }
     }else{
        if(数组具备结构){
          归并排序
        }else{
          if(length<47){
            插入排序
         }else{
            快速排序
         }
     }

## 单例序列化问题
- 序列化会破坏单例模式，解决办法是在单例中加入以下代码
  ``` 
  //不能更改方法名
  private Object readResolve() {
        //返回单例
       return INSTANCE;
 }
  ```

