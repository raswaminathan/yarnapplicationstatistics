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

    values = getCapacityUpToTimeFromTable(TAG, int(round(time.time() * 1000)))

    timeStamps = [];
    valuesToGraph = []
    for i in range (0, len(values)):
        valuesToGraph.append(values[i][0])
        timeStamps.append(values[i][1])

    plt.cla()               
    #plt.xlim([startTime, curTime+250])
    #plt.ylim([0,Y_MAX])
    #plt.title(TAG + ' Value over past ' + str(NUMBER_OF_VISIBLE_DATAPOINTS) + ' datapoints at time intervals of ' + str(TIME_INTERVAL) + ' second(s)')
    plt.xlabel("Time Intervals")
    plt.ylabel(TAG + " Value")
    plt.scatter(timeStamps,valuesToGraph)
    plt.draw()        
    plt.ioff()
    plt.show()
    print("Graphing program shutting down")
    
def getValueFromTable(tag):
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd=PASSWORD, db=DATABASE)
    cursor = conn.cursor()
    cursor.execute('select value from ' + TABLE + ' where tag=' + "'" + tag + "'");
    rows = cursor.fetchall()
    return rows[0]

def getCapacityBetweenTimesFromTable(tag, time1, time2):
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd=PASSWORD, db=DATABASE)
    cursor = conn.cursor()
    cursor.execute("SELECT " + tag + " FROM " + TABLE + " WHERE TimeStamp BETWEEN " + str(time1) + " AND " + str(time2) + ";")
    rows = cursor.fetchall()
    return rows[0]

def getCapacityUpToTimeFromTable(tag, time):
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd=PASSWORD, db=DATABASE)
    cursor = conn.cursor()
    time2 = time
    time1 = 0#time-18000000
    query = "SELECT " + tag + ",timeStamp"+ " FROM " + TABLE + " WHERE TimeStamp BETWEEN " + str(time1) + " AND " + str(time2) + ";"
    cursor.execute(query)
    rows = cursor.fetchall()
    return rows
#def updateValue(cursor, tag, newValue):
    #print("UPDATE " + TABLE + " SET value="
    #                + str(newValue) + " WHERE tag='" + tag + "'")
    #cursor.execute("UPDATE " + TABLE + " SET value="
    #                + str(newValue) + " WHERE tag='" + tag + "'");
    
   
main()

