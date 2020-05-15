#!/usr/bin/env python


import math

CYCLE_DURATION = 1.5 # hours per sleep cycle
MIN_CYCLES = 4 # minimum number of sleep cycles
MAX_CYCLES = 8 # maximum number of sleep cycles
TIME_TO_BED = 15 # hours between bedtime and asleep, minute



def calculateBedtimes(hour, min):
    waketime = hour * 60 + min
    times = list()
    for sleep_cycle_num in range(MIN_CYCLES, MAX_CYCLES):
        sleeptime = CYCLE_DURATION * sleep_cycle_num * 60
        times.append(prettifyTime(waketime - sleeptime ,1 if (waketime - sleeptime) >= 0 else 2))
    return times

def calculateWaketimes(hour, min):
    times = list()
    waketime = hour * 60 + min
    for sleep_cycle_num in range(MIN_CYCLES, MAX_CYCLES, 1):
        sleeptime = CYCLE_DURATION * sleep_cycle_num * 60 + TIME_TO_BED
        times.append(prettifyTime(waketime-sleeptime , 1 if (waketime - sleeptime) >= 0 else 2))
    return times

def prettifyTime(time, x):
    if time < 0:
        hour = (720 + time) / 60
    else:
        hour = time / 60
    
    min = time % 60
    return "{:02d}:{:02d}{}".format(int(hour), int(min), 'am' if x == 1 else 'pm')

#if __name__ == '__main__':
#    menu = 1
#    while True:
#        
#        if menu == 0:
#            hour, min = map(int, input("When would you like to wake up? \n").split(':'))
#            results = calculateWaketimes(hour, min)
#            print (results)
#        elif menu == 1:
#            hour, min = map(int, input("When would you like to wake up? \n").split(':'))
#            results = calculateWaketimes(hour, min)
#            print (results)
#        elif menu == 2:
#           print ("Quitting.")
#           break #quit
#        else:
            print ("Invalid selection. Please select one of the menu items by entering its number.")
            continue #skip to next iteration
