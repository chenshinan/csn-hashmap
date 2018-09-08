## HashMap源码分析

* 哈希表

散列表（Hash table，也叫哈希表），是根据关键码值(Key value)而直接进行访问的数据结构。也就是说，它通过把关键码值映射到表中一个位置来访问记录，以加快查找的速度。这个映射函数叫做散列函数，存放记录的数组叫做散列表。通过哈希函数（一定的规则）计算出在数组中存在的位置，哈希表的主干就是数组

* 哈希冲突

通过哈希函数得出的实际存储地址相同，即为哈希冲突，解决哈希冲突可以采用链表或二叉树

* HashMap

HashMap由数组+链表组成的，数组是HashMap的主体，链表则是主要为了解决哈希冲突而存在的，如果定位到的数组位置不含链表（当前entry的next指向null）,那么对于查找，添加等操作很快，仅需一次寻址即可；如果定位到的数组包含链表，对于添加操作，其时间复杂度为O(n)，首先遍历链表，存在即覆盖，否则新增；对于查找操作来讲，仍需遍历链表，然后通过key对象的equals方法逐一比对查找。所以，性能考虑，HashMap中的链表出现越少，性能才会越好。当链表达到一定长度时转为红黑树，复杂度降为 O(logn)

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

没有设置的话默认为0.75，与桶最大容量计算出桶的阈值，当容量达到桶阈值时将进行扩容操作

* 右移运算符

带符号右移`>>`：右移运算符>>使指定值的所有位都右移规定的次数。右边移出去的部分扔掉不要，左边空出来的部分用原来的数字填充
无符号右移`>>>`：>>>与>>唯一的不同是它无论原来的最左边是什么数，统统都用0填充

* 二进制负数

        正数 5:0000 0101（存储在计算机中）
        负数-5分为原码、反码、补码
        原码:  0000 0101
        反码:  1111 1010
        补码+1:1111 1011（存储在计算机中

* 其他

> HashMap是线程不安全的，不要在并发的环境中同时操作HashMap，建议使用ConcurrentHashMap