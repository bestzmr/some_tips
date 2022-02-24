# 索引基础篇

简介:mysql中不同的存储引擎的索引的实现方式是不同的.

## 索引的底层 - 见高级篇

* MyISAM

  * 有单独的索引文件的,索引过多 - 索引文件变大(占空间的)

  * 叶子节点中保存的是索引+物理行地址的

    索引的本质:键值对(索引列值,物理行地址)

  * 先判断查询是否走了索引,先查询索引文件,找到物理行地址

    再由地址直接定位到数据表.

  * 索引是单独的文件

* InnoDB

  * 索***引文件不是一个单独的文件,它和数据文件是合二为一的.***
  * 索引和数据->数据文件中 -> 聚簇索引

## 索引算法

* B+Tree(索引数据结构)
  * 聚簇索引 - mysql会自动选择主键列作为聚簇索引列
    * 非叶节点 - 聚簇索引列的值
    * 叶节点 - 聚簇索引列值以及真实的数据.
  * 非聚簇索引
    * 非叶节点 - 非聚簇索引列的值
    * 叶节点 - 键值对(非聚簇索引列的值,主键值)

## 优缺点

1. 好处:加快了查询速度(select )
2. 坏处:降低了增,删,改的速度(update/delete/insert),增大了表的文件大小(索引文件甚至可能比数据文件还大)

## 索引类型

1. 普通索引(index)：仅仅是加快了查询速度
2. **唯一索引(unique)：**行上的值不能重复
3. **主键索引(primary key)：**不能重复
4. **全文索引(fulltext):仅可用于 MyISAM 表**，针对较大的数据，生成全文索引很耗时好空间。
5. 组合索引[覆盖索引]：为了更多的提高mysql效率可建立组合索引，遵循”最左前缀“原则。

## 索引语法

## 创建索引总览

```sql
CREATE TABLE table_name(
    [col_name data type]
	[unique|fulltext][index|key] [index_name](col_name[length]) [asc|desc]
)
```

1. unique|fulltext为可选参数，分别表示唯一索引、全文索引
2. index和key为同义词，两者作用相同，用来指定创建索引
3. col_name为需要创建索引的字段列，该列必须从数据表中该定义的多个列中选择
4. index_name指定索引的名称，为可选参数，如果不指定，默认col_name为索引值
5. length为可选参数，表示索引的长度，只有字符串类型的字段才能指定索引长度
6. asc或desc指定升序或降序的索引值存储

## 索引使用方式

1. 查看某张表上的所有索引

   show index from tableName [\G,如果是在cmd窗口，可以换行];

2. 建立索引

   alter table 表名 add index/unique/fulltext 索引名 (列名) ; ---索引名可不写,不写默认使用列名

   **CREATE INDEX  索引名 ON 表名(列值)**

   删除索引 - alter table 表名 drop index 索引名;

   ~~~mysql
   drop table index_test;
   create table index_test(
   	id int(7) primary key,
     a int(7),
     b int(7),
     c varchar(20),
     d varchar(20)
   );
   insert into index_test values(1,100,10,'aaa','A');
   insert into index_test values(2,300,30,'aba','BB');
   insert into index_test values(3,200,20,'caa','CC');
   insert into index_test values(4,100,10,'daa','DD');
   insert into index_test values(5,500,50,'aad','FF');
   
   -- 给a列单独创建一个索引 - 普通索引 - 非聚簇索引
   create index index_test_a_index on index_test(a);
   
   -- 删除索引
   alter table index_test drop index index_test_a_index;
   
   -- 创建一个组合索引 - 非聚簇索引
   create index index_test_abc on index_test(a,b,c);
   
   -- 给定一个索引的长度key_len,列的数据类型应该是字符串类型
   create index index_test_a_index on index_test(d(1));
   
   比如:select * from xxx where name like = '李%';
   
   name,李,李妈,李三儿,李四二二,李妈太跳了....
   长度越长,索引就会更优化.索引长度也不能太大,所占空间变大 - 找到平衡值.
   索引长度是有一个计算公式.
   
   img_url
   http://www.aistar.tech/x.png
   http://www.aistar.tech/y.png
   http://www.aistar.tech/z.png
   
   http://www.aistar.tech/ - 前缀都是一样的 - 索引更优 - 索引长度变长 - 所占空间变大.
   倒过来进行存储.  x.png/http://www.aistar.tech
   ~~~

   ***alter table 表名 add primary key(列名) --不要加索引名，因为主键只有一个***

3. 删除非主键索引

   alter table 表名 drop index 索引名；

   ~~~mysql
   mysql>
   ~~~

4. 删除主键索引：

   ***alter table 表名 drop primary key;***

## 查看查询是否使用到了索引

```sql
mysql>explain select * from index_test where id=4;//走索引的
```

## 组合索引

(5)复合索引

~~~mysql
create index 索引名 on 表(列1,列2,列n);
~~~

## 索引失效情况

索引type从优到差：System-->**const-->eq_ref-->ref-->ref_or_null-->index_merge-->unique_subquery-->index_subquery-->**range-->index-->all(全表扫描的意思)

~~~mysql
drop table index_test;
create table index_test(
	id int(7) primary key,
  a int(7),
  b int(7),
  c varchar(20),
  d varchar(20)
);
insert into index_test values(1,100,10,'aaa','A');
insert into index_test values(2,300,30,'aba','BB');
insert into index_test values(3,200,20,'caa','CC');
insert into index_test values(4,100,10,'daa','DD');
insert into index_test values(5,500,50,'aad','FF');

-- 复合索引
create index index_test_abc on index_test(a,b,c);
~~~

### 遵循最左原则

简介:***针对的是复合索引*** - 最左边的列一定要和创建复合索引的第一个列保持一致.

* 复合索引(a,b,c) - 必须要连续.

~~~msyql
-- a,b,c都是走了索引的.
mysql>explain select * from index_test where a=100 and b=10 and c='aaa';

-- a列走了索引,但是c列没有走索引,原因是跳过了复合索引的b列
mysql>explain select * from index_test where a=100 and c='aaa';

-- a列和b列都是走了索引的,并且它们是连续的.
mysql>explain select * from index_test where a=100 and b=10;

-- 最左原则
-- 索引完全失效,不符合最左匹配原则.where最左边的检索列不是复合索引的第一个列a

-- 原因同上
mysql>explain select * from index_test where b=10;//没有走索引

mysql>explain select * from index_test where a=100;//走了索引

mysql>explain select * from index_test where c='aaa';//没有走索引
~~~

### 范围之后索引列也会失效

~~~mysql
-- a列和b列是走了索引的,但是c列没有走索引.因为c列是范围之后的判断
mysql>explain select * from index_test where a=100 and b>10 and c='aaa';
~~~

### 模糊查询

~~~mysql
like '%'出现在末尾,仍然a,b,c都是走索引
mysql>explain select * from index_test where a=100 and b=10 and c like 'a%';

-- 只有a,b是走了索引的,c是没有走索引的
mysql>explain select * from index_test where a=100 and b=10 and c like '%a';

-- 只有a,b是走了索引的,c是没有走索引的
mysql>explain select * from index_test where a=100 and b=10 and c like '%a%';
~~~

### 索引列使用函数

~~~mysql
索引列套在函数中使用,将会导致索引失效
mysql>explain select * from index_test where abs(id)=1;
+----+-------------+------------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table      | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+------------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | index_test | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    5 |   100.00 | Using where |
+----+-------------+------------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

mysql>explain select * from xxx where name = lower('AAA');
~~~

### 索引列参加了计算

~~~mysql
mysql>explain select * from index_test where id+5>7;

应用场景:
mysql>explain select * from index_test where months>5*12;
~~~

### 索引列参加运算符

~~~sql

-- is null(走索引)和is not null(不走索引)
mysql>explain select * from s_emp where commission_pct is not null;
-- is not null是不支持索引的
mysql>

-- in(走了索引)  not in(不走索引)
~~~



### 利用索引列查询出来的数据超过整张表的30%.



## 建立索引的策略

1. 主键列和唯一性列						√
2. 不经常发生改变的[在update列数据的数据的时候,也会更新索引文件]				√
3. 满足以上2个条件,经常作为查询条件的列	√
4. 重复值太多的列						×
5. null值太多的列						×




