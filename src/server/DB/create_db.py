import pymysql

db = pymysql.connect(host='127.0.0.1', user='tester', password='0000', db='sweetleep', charset='utf8')

curs = db.cursor()

sql = '''
CREATE TABLE sleep (
           user_id VARCHAR(20) NOT NULL,
           start DATETIME NOT NULL,
           end DATETIME NOT NULL,
           qualiry INT NOT NULL,
           PRIMARY KEY(user_id)
    )
'''

curs.execute(sql)
db.commit()

