/*  App version manual
	APP_VERSION: The version of the app. The version is separated to three numbers. The first two are app versions that will increase when a new version
	             of the app is pushed to the store. The last part is the content version. It will increase when there is correction of content or message to broadcast.
	STATUS: The indicator for each combination of app version and sub version.
	TITLE: The title of the pop up message on start of the app.
	MESSAGE: The message of the pop up message on start of the app.
	UPDATES: Survival guide content update queries. Multiple queries are separated by semi colon.
	
	Status:
	OK: Client is up to date.
	AU: App Update required. This will happen when APP_VERSION get increased, namely, we push a new version to the store.
	    AU record will have a title and message that will show up on client side
	BM: Broadcast Message. A message that is sent to all clients.
	    BM record will have a title and message that will show up on client side
	CU: Content Update. Updates on contents of survival guide.
	IBM: Apple Broadcast Message. A message that is sent to all Apple clients.(not implemented)
	ABM: Android Broadcast Message. A message that is sent to all Android clients.(not implemented)
	ICU: Apple Content Update. This should not be used unless necessary.(not implemented)
	ACU: Android Content Update. This should not be used unless necessary.(not implemented)
*/

INSERT INTO WC_APP_VERSIONS (APP_VERSION, STATUS, TITLE, MESSAGE, UPDATES)
VALUES ('1.00.001', 'OK', '', '', '');

INSERT INTO WC_APP_VERSIONS (APP_VERSION, STATUS, TITLE, MESSAGE, UPDATES)
VALUES ('1.00.002', 'OK', '', '', '');

UPDATE WC_APP_VERSIONS SET STATUS = 'CU',
UPDATES = 'update articles set content = ''<p style="font-size:20px;">&emsp;&emsp;欢迎WPI 2020届的同学！<br>&emsp;&emsp;她包容的环境，当然最重要的是这里可爱的人。不仅是老师同学，校园里几乎每个Staff都会对你亲切的微笑。毕业典礼之后，54岁清洁工拿到 WPI Mechanical Engineering Degree的新闻在社交网络中被广泛留传。虽然这件事情发生的几率并不大，但它体现出了WPI一种奋发向上的精神。我希望你们，也能在接下来的一年里有所收获。<br>&emsp;&emsp;这本SG是许多学长学姐用心写出来的。这么多年大家在那边的一些心得和经验，希望能多多少少帮到你们一点。特别感谢一直在操心这本 SG 我们CSA的 Webmaster 王耀奉同学，花费了好多个日日夜夜整理材料，辛苦啦。</p><p style="font-size:20px;" align="right">陆安琪<br>2016年6月1日</p><p>这行字的字体大小为16</p><p style="font-size:20px;">这行字的字体大小为20</p><p style="font-size:24px;">这行字的字体大小为24</p><p style="font-size:28px;">这行字的字体大小为28</p><p style="font-size:36px;">这行字的字体大小为36</p>'' where id = 2;'
WHERE APP_VERSION = '1.00.001';

-- App version update

INSERT INTO WC_APP_VERSIONS (APP_VERSION, STATUS, TITLE, MESSAGE, UPDATES)
VALUES ('1.01.001', 'OK', '', '', '');

UPDATE WC_APP_VERSIONS SET STATUS = 'AU', TITLE = 'App Update', MESSAGE = 'A new version is available on test flight. Please download the new version by clicking on the test flight link from email. This current version will not be supported.'
WHERE APP_VERSION in ('1.00.001', '1.00.002');

INSERT INTO WC_APP_VERSIONS (APP_VERSION, STATUS, TITLE, MESSAGE, UPDATES)
VALUES ('1.02.001', 'OK', '', '', '');

UPDATE WC_APP_VERSIONS SET STATUS = 'AU', TITLE = 'App Update', MESSAGE = 'A new version is available on test flight. Please download the new version by clicking on the test flight link from email. This current version will not be supported.'
WHERE APP_VERSION in ('1.01.001');

INSERT INTO WC_APP_VERSIONS (APP_VERSION, STATUS, TITLE, MESSAGE, UPDATES)
VALUES ('1.03.001', 'OK', '', '', '');