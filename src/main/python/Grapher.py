'''
Created on Apr 24, 2015

@author: bfox1793
'''

import MySQLdb
from time import sleep
import matplotlib.pyplot as plt
import datetime
import time

import ConfigParser
cp = ConfigParser.ConfigParser()
cp.read('graphProperties.ini')

sql = 'MySQL Login'
database = 'Database'
graph = 'Graph'

SERVER_LOCATION = cp.get(sql, 'serverLocation')
print(SERVER_LOCATION)
PORT = cp.getint(sql, 'port')
PASSWORD = cp.get(sql, 'password')
DATABASE = cp.get(database, 'database')
TABLE = cp.get(database, 'table')
TAG = cp.get(database, 'tag')
oldTagValue = TAG
NUMBER_OF_VISIBLE_DATAPOINTS = cp.getint(graph, 'numberOfVisibleDatapoints')
TIME_INTERVAL = cp.getfloat(graph, 'timeInterval')
Y_MAX = cp.getint(graph,'yMax')

def main():

    plt.ion()
    plt.show()
    
    index=1
    x = []
    values = []
    startTime = int(round(time.time()))
    print startTime
    while(True):
        try:
            oldTagValue = cp.get(database, 'tag')
            cp.read('graphProperties.ini')
            TABLE = cp.get(database, 'table')
            TAG = cp.get(database, 'tag')

            if oldTagValue != TAG:
                x = []
                values = []
                index = 1

            NUMBER_OF_VISIBLE_DATAPOINTS = cp.getint(graph, 'numberOfVisibleDatapoints')
            TIME_INTERVAL = cp.getfloat(graph, 'timeInterval')
            Y_MAX = cp.getint(graph,'yMax')
            plt.ion()
            newValue = getValueFromTable(TAG)
            if len(values) >= NUMBER_OF_VISIBLE_DATAPOINTS:
                values.pop(0)
                x.pop(0)
            else:
                index=index+1
              
            curTime = int(round(time.time()))  
            x.append(curTime)
            values.append(newValue)
                
            plt.cla()               
            plt.xlim([startTime, curTime+250])
            plt.ylim([0,Y_MAX])
            plt.title(TAG + ' Value over past ' + str(NUMBER_OF_VISIBLE_DATAPOINTS) + ' datapoints at time intervals of ' + str(TIME_INTERVAL) + ' second(s)')
            plt.xlabel("Time Intervals")
            plt.ylabel(TAG + " Value")
            plt.scatter(x,values)
            plt.draw()
            sleep(TIME_INTERVAL)
        except KeyboardInterrupt:
            print("User has stopped the grapher")
            break
        
    plt.ioff()
    plt.show()
    print("Graphing program shutting down")
    
def getValueFromTable(tag):
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd=PASSWORD, db=DATABASE)
    cursor = conn.cursor()
    cursor.execute('select value from ' + TABLE + ' where tag=' + "'" + tag + "'");
    rows = cursor.fetchall()
    return rows[0]

def getCapacityBetweenTimesFromTable(time1, time2):
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd=PASSWORD, db=DATABASE)
    cursor = conn.cursor()
    cursor.execute("SELECT capacity FROM " + TABLE + " WHERE TimeStamp BETWEEN %s AND %s;", (time1), (time2))
    rows = cursor.fetchall()
    return rows[0]

def getCapacityUpToTimeFromTable(time):
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd=PASSWORD, db=DATABASE)
    cursor = conn.cursor()
    time2 = time
    time1 = time-18000000
    cursor.execute("SELECT capacity FROM " + TABLE + " WHERE TimeStamp BETWEEN %s AND %s;", (time1), (time2))
    rows = cursor.fetchall()
    return rows[0]
#def updateValue(cursor, tag, newValue):
    #print("UPDATE " + TABLE + " SET value="
    #                + str(newValue) + " WHERE tag='" + tag + "'")
    #cursor.execute("UPDATE " + TABLE + " SET value="
    #                + str(newValue) + " WHERE tag='" + tag + "'");
    
   
main()

