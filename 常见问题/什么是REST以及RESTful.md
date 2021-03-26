概括：
    看Url就知道要什么
    看http method就知道干什么
    看http status code就知道结果如何
**URL定位资源，用HTTP动词（GET,POST,DELETE,DETC）描述操作。**



**--- 简洁版 ---**

0. REST不是"rest"这个单词，而是几个单词缩写。但即使那几个单词说出来，也无法理解在说什么 -_-!! （不是要贬低人，是我自己也理解困难）；

1. REST描述的是在网络中client和server的一种交互形式；REST本身不实用，实用的是如何设计 RESTful API（REST风格的网络接口）；

2. Server提供的RESTful API中，URL中只使用名词来指定资源，原则上不使用动词。“资源”是REST架构或者说整个网络处理的核心。比如：
   [http://api.qc.com/v1/newsfeed](https://link.zhihu.com/?target=http%3A//api.qc.com/v1/newsfeed): 获取某人的新鲜; 
   [http://api.qc.com/v1/friends](https://link.zhihu.com/?target=http%3A//api.qc.com/v1/friends): 获取某人的好友列表;
   [http://api.qc.com/v1/profile](https://link.zhihu.com/?target=http%3A//api.qc.com/v1/profile): 获取某人的详细信息;

3. 用HTTP协议里的动词来实现资源的添加，修改，删除等操作。即通过HTTP动词来实现资源的状态扭转：
GET    用来获取资源，
POST  用来新建资源（也可以用于更新资源），
PUT    用来更新资源，
DELETE  用来删除资源。比如：
DELETE [http://api.qc.com/v1/](https://link.zhihu.com/?target=http%3A//api.qc.com/v1/friends)friends: 删除某人的好友 （在http parameter指定好友id）
POST [http://api.qc.com/v1/](https://link.zhihu.com/?target=http%3A//api.qc.com/v1/friends)friends: 添加好友
UPDATE [http://api.qc.com/v1/profile](https://link.zhihu.com/?target=http%3A//api.qc.com/v1/profile): 更新个人资料

禁止使用： GET [http://api.qc.com/v1/deleteFriend...](https://link.zhihu.com/?target=http%3A//api.qc.com/v1/deleteFriend)

作者：覃超
链接：https://www.zhihu.com/question/28557115/answer/48094438
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。



4. Server和Client之间传递某资源的一个表现形式，比如用JSON，XML传输文本，或者用JPG，WebP传输图片等。当然还可以压缩HTTP传输时的数据（on-wire data compression）。

5. 用 HTTP Status Code传递Server的状态信息。比如最常用的 200 表示成功，500 表示Server内部错误等。



主要信息就这么点。最后是要解放思想，Web端不再用之前典型的PHP或JSP架构，而是改为前段渲染和附带处理简单的商务逻辑（比如AngularJS或者BackBone的一些样例）。Web端和Server只使用上述定义的API来传递数据和改变数据状态。格式一般是JSON。iOS和Android同理可得。由此可见，Web，iOS，Android和第三方开发者变为平等的角色通过一套API来共同消费Server提供的服务。



**--- 详细版 ---**

**先说REST名称**
REST -- REpresentational State Transfer
首先，之所以晦涩是因为前面主语被去掉了，全称是 Resource Representational State Transfer：通俗来讲就是：资源在网络中以某种表现形式进行状态转移。分解开来：
Resource：资源，即数据（前面说过网络的核心）。比如 newsfeed，friends等；
Representational：某种表现形式，比如用JSON，XML，JPEG等；
State Transfer：状态变化。通过HTTP动词实现。