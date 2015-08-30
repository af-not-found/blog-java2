
TRUNCATE TABLE comments cascade;
TRUNCATE TABLE entries cascade;
TRUNCATE TABLE users  cascade;


ALTER SEQUENCE entries_id_seq  RESTART with 4;
ALTER SEQUENCE comments_id_seq RESTART with 9;
ALTER SEQUENCE users_id_seq    RESTART with 2;



INSERT INTO entries VALUES (1, 'blog1234', 'tag1, tag2, tag3', '#てすと123
abc

#code
````
if(1==1){
    alert(1)
}
````

#point
1. aaega
1. 433
1. 4343
 1. 224
 1. あああ
 1. bbb', '<h1>てすと123</h1>
<p>abc</p>
<h1>code</h1>
<pre><code>if(1==1){
    alert(1)
}
</code></pre><h1>point</h1>
<ol>
<li>aaega</li>
<li>433</li>
<li>4343<ol>
<li>224</li>
<li>あああ</li>
</ol>
</li>
</ol>
', now(), 1);
INSERT INTO entries VALUES (2, 'ブログ12345', 'tag1, tag3, タグa', '#テスト
aaa|bbb|ccc
---|---|---
1|2|3
ああああ|いいいいい|うううううう
#|*|abbbccc', '<h1>テスト</h1>
<table>
<thead>
<tr>
<th>aaa</th>
<th>bbb</th>
<th>ccc</th>
</tr>
</thead>
<tbody>
<tr>
<td>1</td>
<td>2</td>
<td>3</td>
</tr>
<tr>
<td>ああああ</td>
<td>いいいいい</td>
<td>うううううう</td>
</tr>
<tr>
<td>#</td>
<td>*</td>
<td>abbbccc</td>
</tr>
</tbody>
</table>
', now(), 1);
INSERT INTO entries VALUES (3, 'Markdownのテスト1234', 'tag1, tag3, タグa, タグb', '#Markdownのテスト1234
 てすとてすとてすとて **すとてすとてす** とてすとてすとてすと。
てすと```てすとてすとてすとてすと```てすとてすとてす _とてすとてすとてす_ とてすとてすと。
aaaalongaaaalogaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaa
<br/><br/>aa3a<span style="color:red" class="md_element">e3a4</span>3aa

#テーブル1224
1|2 |3
---|---|---
a|b|c
xxxxxxx|yyyyyyyy|zzzzzz*long* **longlong**


# 複数行の整形済テキスト
````
class HelloWorld {
  public static void main() {
    int i = 1;
    System.out.println("Hello, World! : " + i);
  }
}
````

## PHPでhello world
````
<?php
  echo "Hello, World!\n";
?>
````


## 順序
1. a1
1. 順序付きリストのアイテム
 1. 子供1
 1. 子供1
1. 順序付きリストの別のアイテム
  

### リスト
* aa
 * [抗酸化物質](//ja.wikipedia.org/wiki/%E6%8A%97%E9%85%B8%E5%8C%96%E7%89%A9%E8%B3%AA)
 * cc
* e


#### リンク
* [ローカルホスト2](http://localhost)
* [ローカルホスト1](http://localhost)
* ![alt](/blogdemo/static/img/email1.png;v=21)
* ![alt](/blogtest/static/img/email1.png;v=21)', '<h1>Markdownのテスト1234</h1>
<p> てすとてすとてすとて <strong>すとてすとてす</strong> とてすとてすとてすと。
てすと<code>てすとてすとてすとてすと</code>てすとてすとてす <em>とてすとてすとてす</em> とてすとてすと。
aaaalongaaaalogaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaa
<br/><br/>aa3a<span style="color:red" class="md_element">e3a4</span>3aa</p>
<h1>テーブル1224</h1>
<table>
<thead>
<tr>
<th>1</th>
<th>2</th>
<th>3</th>
</tr>
</thead>
<tbody>
<tr>
<td>a</td>
<td>b</td>
<td>c</td>
</tr>
<tr>
<td>xxxxxxx</td>
<td>yyyyyyyy</td>
<td>zzzzzz<em>long</em> <strong>longlong</strong></td>
</tr>
</tbody>
</table>
<h1>複数行の整形済テキスト</h1>
<pre><code>class HelloWorld {
  public static void main() {
    int i = 1;
    System.out.println(&quot;Hello, World! : &quot; + i);
  }
}
</code></pre><h2>PHPでhello world</h2>
<pre><code>&lt;?php
  echo &quot;Hello, World!\n&quot;;
?&gt;
</code></pre><h2>順序</h2>
<ol>
<li>a1</li>
<li>順序付きリストのアイテム<ol>
<li>子供1</li>
<li>子供1</li>
</ol>
</li>
<li>順序付きリストの別のアイテム</li>
</ol>
<h3>リスト</h3>
<ul>
<li>aa<ul>
<li><a href="//ja.wikipedia.org/wiki/%E6%8A%97%E9%85%B8%E5%8C%96%E7%89%A9%E8%B3%AA">抗酸化物質</a></li>
<li>cc</li>
</ul>
</li>
<li>e</li>
</ul>
<h4>リンク</h4>
<ul>
<li><a href="http://localhost">ローカルホスト2</a></li>
<li><a href="http://localhost">ローカルホスト1</a></li>
<li><img src="/blogdemo/static/img/email1.png;v=21" alt="alt"></li>
<li><img src="/blogtest/static/img/email1.png;v=21" alt="alt"></li>
</ul>
', now(), 1);







INSERT INTO comments VALUES (1, 1, NULL, 'test2', 'aaaaaaa"aaaaaa', now(), '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 0);
INSERT INTO comments VALUES (2, 1, NULL, 'test2', 'aaaaaaaabbbbbbbbbbbbb
 ああああああ<script>location.href=''/b'';</script>ああああaaa"aa
<b>aaa</b>', now(), '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 1);
INSERT INTO comments VALUES (3, 1, NULL, 'test3', 'aaaaaaabbbbbb
 あああ
<b>aaa</b> vv', '2014-02-09 17:48:49.161979+09', '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 2);
INSERT INTO comments VALUES (4, 1, NULL, 'aaaa', 'bbbbbbbb', now(), '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 0);
INSERT INTO comments VALUES (5, 2, NULL, 'ああああああああああああ', 'aaaaaaaabbbbbbbbbbbbb
 ああああああああああaaaaa
<b>aaa</b>jfdytr



i7tfjyg


fj75rfjytft', now(), '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 0);
INSERT INTO comments VALUES (6, 3, NULL, 'aaa', 'bbbbb', now(), '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 0);
INSERT INTO comments VALUES (7, 3, NULL, 'vvvvv', 'cccccccc', now(), '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 0);
INSERT INTO comments VALUES (8, 3, NULL, 'dddddd', 'vvvvvvvvvvvvvvvvvvv', now(), '127.0.0.1, Mozilla/5.0 (Unknown; Linux i686) AppleWebKit/534.34 (KHTML, like Gecko) PhantomJS/1.9.2 Safari/534.34', 0);




INSERT INTO users VALUES (1, 'admin', '$2a$08$aF4JfKSih28HZuwNjaOGAuk4GBLVXfI1vkSMYRrugbp9/WFxbEwV2', 'ROLE_ADMIN');







insert into entries
	(title, tags, content, content_html, postdate, state)
select
	'title' || s.a, 
	x.tags || ', tag' || (s.a % 16),
	x.content, 
	x.content_html, 
	date '2010-2-1' + (s.a / 10),
	1
from
	generate_series(1,10000) as s(a),
	(select title, tags, content, content_html from entries where id = 3) x
;


insert into entries
	(title, tags, content, content_html, postdate, state)
select
	x.title || '789', 
	x.tags,
	x.content, 
	x.content_html, 
	now(),
	1
from
	(select title, tags, content, content_html from entries where id = 3) x
;




insert into comments
	(entryid, name, content, state)
select
	x.entryid, 
	x.name || s.a,
	x.content || s.a, 
	s.a % 3
from
	generate_series(1,1000) as s(a),
	(select entryid, name, content, state from comments where id = 2) x
;

