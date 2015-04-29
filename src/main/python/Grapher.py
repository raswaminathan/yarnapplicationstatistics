'''
Created on Apr 24, 2015

@author: bfox1793
'''

import MySQLdb
from time import sleep
import matplotlib.pyplot as plt
import datetime

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
NUMBER_OF_VISIBLE_DATAPOINTS = cp.getint(graph, 'numberOfVisibleDatapoints')
TIME_INTERVAL = cp.getfloat(graph, 'timeInterval')

def main():

    plt.ion()
    plt.show()
    
    index=1
    x = []
    values = []
    
    while(True):
        try:
            plt.ion()
            newValue = getValueFromTable(TAG)
            if len(values) >= NUMBER_OF_VISIBLE_DATAPOINTS:
                values.pop(0)
            else:
                x.append(index)
                index=index+1
                
            values.append(newValue)
                
            plt.cla()               
            plt.xlim([0, NUMBER_OF_VISIBLE_DATAPOINTS+1])
            plt.ylim([0,10000])
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

#def updateValue(cursor, tag, newValue):
    #print("UPDATE " + TABLE + " SET value="
    #                + str(newValue) + " WHERE tag='" + tag + "'")
    #cursor.execute("UPDATE " + TABLE + " SET value="
    #                + str(newValue) + " WHERE tag='" + tag + "'");
    
   
main()

