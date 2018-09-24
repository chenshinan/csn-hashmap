# Map相关实现类的源码解析

> 9.23-

## Map的架构图

![Map的架构图](https://images0.cnblogs.com/blog/497634/201309/08221402-aa63b46891d0466a87e54411cd920237.jpg)

## Map相关

* Map：不包含重复键的映射集合

* entrySet()用于返回键-值集的Set集合

* keySet()用于返回键集的Set集合

* values()用户返回值集的Collection集合

* SortedMap：是继承于Map的接口。SortedMap中的内容是排序的键值对，排序的方法是通过比较器(Comparator)。

* NavigableMap：是`继承于SortedMap`的接口。相比于SortedMap，NavigableMap有一系列的导航方法；如"获取大于/等于某对象的键值对"、“获取小于/等于某对象的键值对”等等

## HashMap

* 继承关系：HashMap -> AbstractMap 实现 Map接口

* HashMap构成

HashMap由`数组+链表`组成的，数组是HashMap的主体，链表则是主要为了解决哈希冲突而存在的，如果定位到的数组位置不含链表（当前entry的next指向null）,那么对于查找，添加等操作很快，仅需一次寻址即可；如果定位到的数组包含链表，对于添加操作，其时间复杂度为O(n)，首先遍历链表，存在即覆盖，否则新增；对于查找操作来讲，仍需遍历链表，然后通过key对象的equals方法逐一比对查找。所以，性能考虑，HashMap中的链表出现越少，性能才会越好。当链表达到一定长度时转为红黑树，复杂度降为 O(logn)。`新增对象时，size都会+1，对象会根据hash值找到在数组中对应的存储位置，当达到设置的容量阈值时就会触发扩容`

* HashMap 的实现不是同步的，这意味着它`不是线程安全的`。它的`key、value都可以为null`。此外，HashMap中的`映射不是有序`的

* 哈希表

散列表（Hash table，也叫哈希表），是根据关键码值(Key value)而直接进行访问的数据结构。也就是说，它通过把关键码值映射到表中一个位置来访问记录，以加快查找的速度。这个映射函数叫做散列函数，存放记录的数组叫做散列表。通过哈希函数（一定的规则）计算出在数组中存在的位置，哈希表的主干就是数组

* 哈希冲突

通过哈希函数得出的实际存储地址相同，即为哈希冲突，解决哈希冲突可以采用链表或红黑树

* 红黑树

JDK1.8引入红黑树大程度优化了HashMap的性能，红黑树本质上是一种二叉查找树，但它在二叉查找树的基础上额外添加了一个标记（颜色），同时具有一定的规则。这些规则使红黑树保证了一种平衡，插入、删除、查找的最坏时间复杂度都为 O(logn)
红黑输有五个特征：

        每个节点要么是红色，要么是黑色；
        根节点永远是黑色的；
        所有的叶节点都是是黑色的（注意这里说叶子节点其实是上图中的 NIL 节点）；
        每个红色节点的两个子节点一定都是黑色；
        从任一节点到其子树中每个叶子节点的路径都包含相同数量的黑色节点；

详细参考：https://blog.csdn.net/sun_tttt/article/details/65445754

* 扩容机制

当数组中的数据数量达到阈值时，触发扩容，容量扩大一倍，重新分布原数组中的数据，相对消耗性能。在常规构造器中，没有为数组table分配内存空间（有一个入参为指定Map的构造器例外），而是在执行put操作的时候才真正构建table数组。`扩容是一个特别耗性能的操作，所以当程序员在使用HashMap的时候，估算map的大小，初始化的时候给一个大致的数值，避免map进行频繁的扩容`

* hashCode

Java中的hashCode方法就是根据一定的规则将与对象相关的信息（比如对象的存储地址，对象的字段等）映射成一个数值，这个数值称作为散列值（哈希值）

> 在重写equals方法的同时，必须重写hashCode方法。不同的对象可能会生成相同的hashcode值

* hash方法：`对key的hashcode进一步进行计算以及二进制位的调整等来保证最终获取的存储位置尽量分布均匀，二进制中，高16位与低16位进行'异或'运算使存储位置均衡`

```java
static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

因为hashmap需要用length-1的数量级和hash值做一个与操作,如果长度是17,那么length-1就是16那么与下来的值要么是0要么是16,也就是说16个槽子只用了两个槽,效率是很低的,而如果采用16(2的四次方),就是15(01111)做与操作,均匀分不到0-15的槽子上 (h = key.hashCode()) ^ (h >>> 16) 这个计算目的是为了希望能够尽量均匀,最后做indexFor的时候实际上只是利用了低16位,高16位是用不到的,那么低16位的数字利用`^亦或`的方法来保证均匀分布

* 映射数据的实际存储位置：`(n - 1) & hash`

通过(n - 1) & hash来映射到数据的实际存储位置，不存在该节点则创建，`n是数组的大小，HashMap的数组长度一定是2的次幂`，因此n-1即可以形成低位的1111，与hash值进行与运算得到较为均匀的数组分布

* 负载因子：loadFactor

没有设置的话默认为0.75，与桶最大容量计算出桶的阈值，当容量达到桶阈值时将进行扩容操作。`默认负载因子为0.75, 这是在时间和空间成本上寻求一种折衷。加载因子过高虽然减少了空间开销，但同时也增加了查找某个条目的时间`

* 右移运算符

带符号右移`>>`：右移运算符>>使指定值的所有位都右移规定的次数。右边移出去的部分扔掉不要，左边空出来的部分用原来的数字填充
无符号右移`>>>`：>>>与>>唯一的不同是它无论原来的最左边是什么数，统统都用0填充

* 二进制负数

        正数 5:0000 0101（存储在计算机中）
        负数-5分为原码、反码、补码
        原码:  0000 0101
        反码:  1111 1010
        补码+1:1111 1011（存储在计算机中)

* 遍历

遍历有三种方式：map.entrySet()、map.keySet()、map.forEach()

`推荐用第一种entrySet，效率高，可以直接取到key和value，第二种还要用key去取值，第三种java8的方式`

* 其他

> HashMap是线程不安全的，不要在并发的环境中同时操作HashMap，建议使用ConcurrentHashMap

参考：
https://www.cnblogs.com/chengxiao/p/6059914.html

## Hashtable

* 继承关系：Hashtable -> Dictionary 实现 Map接口

* 实现方式与HashMap相同，由`数组+链表`构成

* Hashtable 的函数都是同步的，这意味着它是`线程安全`的。它的`key、value都不可以为null`。此外，Hashtable中的`映射不是有序`的

* Dictionary接口

Dictionary是一个抽象类，它直接继承于Object类，没有实现任何接口。Dictionary类是JDK 1.0的引入的。虽然Dictionary也支持“添加key-value键值对”、“获取value”、“获取大小”等基本操作，但它的API函数比Map少；而且`Dictionary一般是通过Enumeration(枚举类)去遍历，Map则是通过Iterator(迭代器)去遍历`

* Enumeration枚举器接口

Enumeration是java.util中的一个接口类，在Enumeration中封装了有关枚举数据集合的方法，与Iterator差不多，用来遍历集合中的元素。但是枚举Enumeration只提供了遍历Vector和Hashtable类型集合元素的功能

## TreeMap

* 继承关系：TreeMap -> AbstractMap 实现 NavigableMap接口

* TreeMap 基于`红黑树（Red-Black tree）`实现的，`不是线程安全`的。TreeMap的`键映射是有序的`

## WeekHashMap

* 继承关系：WeekHashMap -> AbstractMap 实现 Map接口

* 实现方式与HashMap相同，由`数组+链表`构成

* WeakHashMap的键是`弱键`：在 WeakHashMap 中，当某个键不再正常使用时，会被从WeakHashMap中被自动移除。更精确地说，对于一个给定的键，其映射的存在并不阻止垃圾回收器对该键的丢弃，这就使该键成为可终止的，被终止，然后被回收。某个键被终止时，它对应的键值对也就从映射中有效地移除了

* “弱键”的原理

大致上就是，通过WeakReference和ReferenceQueue实现的。 WeakHashMap的key是“弱键”，即是WeakReference类型的；ReferenceQueue是一个队列，它会保存被GC回收的“弱键”。实现步骤如下：

        1、新建WeakHashMap，将“键值对”添加到WeakHashMap中。实际上，WeakHashMap是通过数组table保存Entry(键值对)；每一个Entry实际上是一个单向链表，即Entry是键值对链表。
        2、当某“弱键”不再被其它对象引用，并被GC回收时。在GC回收该“弱键”时，这个“弱键”也同时会被添加到ReferenceQueue(queue)队列中。
        3、当下一次我们需要操作WeakHashMap时，会先同步table和queue。table中保存了全部的键值对，而queue中保存被GC回收的键值对；同步它们，就是删除table中被GC回收的键值对。

* WeekHashMap `不是线程安全`的。可以使用 Collections.synchronizedMap 方法来构造同步的 WeakHashMap

## LinkedHashMap

* 继承关系：LinkedHashMap -> HashMap -> AbstractMap 实现 Map接口

* LinkedHashMap可以认为是`HashMap+LinkedList`，即它既使用HashMap操作数据结构，又使用LinkedList维护插入元素的先后顺序，`不是线程安全的，但是有序的`

* LinkedHashMap是HashMap的子类，维护一个运行于所有条目的双向链表，LinkedHashMap保证了元素迭代的顺序。该迭代顺序可以是插入顺序或者是访问顺序

* 通过Entry<K,V> extends HashMap.Node<K,V>，增加了前节点和后节点

* 关键属性：`accessOrder`，可在构造函数时传入

false：所有的Entry按照插入的顺序排列
true：所有的Entry按照访问的顺序排列

* LinkedHashMap`存储元素`

LinkedHashMap并未重写父类HashMap的put方法，而是重写了父类HashMap的put方法调用的子方法`afterNodeAccess(Node<K,V> e)`和`afterNodeInsertion`，提供了自己特有的双向链接列表的实现

* LinkedHashMap`读取元素`

LinkedHashMap重写了父类HashMap的get方法，实际在调用父类getNode()方法取得查找的元素后，再判断当排序模式accessOrder为true时（即按访问顺序排序），`先将当前节点从链表中移除，然后再将当前节点插入到链表尾部`。由于的链表的增加、删除操作是常量级的，故并不会带来性能的损失

* `每次访问一个元素（get或put），被访问的元素都被提到最后面去了`

* 利用LinkedHashMap实现LRU算法缓存`（LRU即Least Recently Used，最近最少使用，也就是说，当缓存满了，会优先淘汰那些最近最不常访问的数据）`

get和put方法中会调用：void `afterNodeAccess`(Node<K,V> e) 这个方法，当accessOrder为true时，就是使用的访问顺序，访问次数最少到访问次数最多，此时要做特殊处理。处理机制就是访问了一次，就将自己往后移一位，这里就是先将自己删除了，然后在把自己添加，这样，近期访问的少的就在链表的开始，最近访问的元素就会在链表的末尾。如果为false。那么默认就是插入顺序，直接通过链表的特点就能依次找到插入元素，不用做特殊处理

```java
public class LRUCache extends LinkedHashMap {
    public LRUCache(int maxSize) {
        //设置accessOrder为true
        super(maxSize, 0.75F, true);
        maxElements = maxSize;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
        return size() > maxElements;
    }

    private static final long serialVersionUID = 1L;
    protected int maxElements;
}
```

## 其他知识点

* 可以通过Collections.synchronizedMap方法来构造同步的Map对象

```java
Map concurrentMyMap = Collections.synchronizedMap(myMap);
```

## 总结⭐️

* HashMap 是基于“拉链法”实现的散列表。一般用于单线程程序中。

* Hashtable 也是基于“拉链法”实现的散列表。它一般用于多线程程序中。

* WeakHashMap 也是基于“拉链法”实现的散列表，它一般也用于单线程程序中。相比HashMap，WeakHashMap中的键是“弱键”，当“弱键”被GC回收时，它对应的键值对也会被从WeakHashMap中删除；而HashMap中的键是强键。

* TreeMap 是有序的散列表，它是通过红黑树实现的。它一般用于单线程中存储有序的映射

* `HashMap`与`Hashtable`的`区别`

        1、HashMap继承于AbstractMap，而Hashtable继承于Dictionary
        2、HashMap的函数不是线程安全的，而Hashtable的函数是线程安全的
        3、HashMap的key、value都可以为null，Hashtable的key、value都不可以为null
        4、HashMap只支持Iterator(迭代器)遍历，而Hashtable支持Iterator(迭代器)和Enumeration(枚举器)两种方式遍历
        5、HashMap添加元素时，是使用自定义的哈希算法，Hashtable没有自定义哈希算法，而直接采用的key的hashCode()

* `HashMap`与`WeakHashMap`的`区别`

        1、HashMap实现了Cloneable和Serializable接口，而WeakHashMap没有
        2、HashMap的“键”是“强引用(StrongReference)”，而WeakHashMap的键是“弱引用(WeakReference)”

* `Enumeration`与`Iterator`的`区别`

        1、Enumeration 是JDK 1.0添加的接口，而Iterator 是JDK 1.2才添加的接口
        2、Iterator 比Enumeration 多了支持fail-fast机制，所以Enumeration 遍历速度较快