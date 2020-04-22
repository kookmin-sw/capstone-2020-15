import pymysql

conn = pymysql.connect(host='localhost', user='test', password='0000', db='test', charset='utf8')

curs = conn.coursor()

sql = '''
CREATE TABLE test (
           id INT UNSIGNED NOT NULL AUTO_INCREMENT,
           name VARCHAR(20) NOT NULL,
           model_num VARCHAR(10) NOT NULL,
           model_type VARCHAR(10) NOT NULL,
           PRIMARY KEY(id)
    )
'''

curs.execute(sql)
conn.commit()

