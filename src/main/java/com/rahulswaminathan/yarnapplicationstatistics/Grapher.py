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
TAG = 'RAM'
NUMBER_OF_VISIBLE_DATAPOINTS = 20
Y_MAX = 1000
TIME_INTERVAL = 1

def main():
    conn = MySQLdb.connect(host=SERVER_LOCATION, user="root", passwd='SOGkPxhY', db=DATABASE)
    cursor = conn.cursor()

    
    plt.ion()
    plt.show()
    
    count=0
    index=1
    x = []
    values = []
    
    while(True):
        try:
            plt.ion()
            #updateValue(cursor, TAG, count)
            newValue = getValueFromTable(cursor, TAG)
                
            if len(values) >= NUMBER_OF_VISIBLE_DATAPOINTS:
                values.pop(0)
            else:
                x.append(index)
                index=index+1
                
            values.append(newValue)
                
            plt.cla()               
            plt.axis([0, NUMBER_OF_VISIBLE_DATAPOINTS+1, 0, Y_MAX])     
            plt.title(TAG + ' Value over past ' + str(NUMBER_OF_VISIBLE_DATAPOINTS) + ' at time intervals of ' + str(TIME_INTERVAL) + ' second(s)')
            plt.xlabel("Time Intervals")
            plt.ylabel(TAG + " Value")
            plt.scatter(x,values)
            plt.draw()
                
            count=count+10
            sleep(1)
        except KeyboardInterrupt:
            print("User has broken out of the loop")
            break
        
    plt.ioff()
    plt.show()
    print("Program shutting down")
    
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
