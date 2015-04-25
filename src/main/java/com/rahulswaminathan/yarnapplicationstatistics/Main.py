'''
Created on Apr 24, 2015

@author: bfox1793
'''

import MySQLdb
from time import sleep
import numpy as np
import matplotlib.pyplot as plt
import datetime


SERVER_LOCATION = "localhost";
PORT = 8125;
PASSWORD = 'SOGkPxhY'
DATABASE = "test"
TABLE = "metrics"
TAG1 = 'RAM'
TAG2 = 'buckets'

def main():
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd='SOGkPxhY', db=DATABASE)
    cursor = conn.cursor()
    #cursor.execute('select * from ' + TABLE);
#     cursor.execute('select value from ' + TABLE + ' where tag=' + "'" + TAG + "'");
#     columnNames = []
#     rows = cursor.fetchall()
#     for columnName in rows:
#         print(columnName[0])
#         columnNames.append(columnName[0])
#         
#     updateValue(cursor, TAG, 10)
#     cursor.execute('select value from ' + TABLE + ' where tag=' + "'" + TAG + "'");
#     rows = cursor.fetchall()
#     for value in rows:
#         print(value)
    
#     figOne = plt.figure()
#     graph1 = figOne.add_subplot(211)
#     graph2 = figOne.add_subplot(212,sharex=graph1)
#     graph1.plot(range(0,10))
#     graph2.plot(range(0,20))
#     count=10
#     while(1):
#         sleep(1000)
#         count=count+1
#         print(count)

    x = []
    values = []
    plt.ion()
    plt.show()
    count=0
    plt.axis([0, 10, 0, 100])
    for i in range(10):
        updateValue(cursor, TAG1, count)
        newValue = getValueFromTable(cursor, TAG1)
        values.append(newValue)
        
        x.append(i)
                
        plt.scatter(i, newValue)
        
        plt.draw()
        count=count+10
        sleep(1)
        
    plt.ioff()
    plt.show()

def getValueFromTable(cursor, tag):
    cursor.execute('select value from ' + TABLE + ' where tag=' + "'" + tag + "'");
    rows = cursor.fetchall()
    return rows[0]

def updateValue(cursor, tag, newValue):
    #print("UPDATE " + TABLE + " SET value="
    #                + str(newValue) + " WHERE tag='" + tag + "'")
    cursor.execute("UPDATE " + TABLE + " SET value="
                    + str(newValue) + " WHERE tag='" + tag + "'");
    
    
main()