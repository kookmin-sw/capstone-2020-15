# Builds sleep predictor dataset from Sleep Cycle data
# Copyright (c) Andreas Urbanski, 2017
from datetime import datetime, timedelta
import numpy as np
import calendar
import pickle
import math

"""Get datetime object from time entry in sleepdata"""
date = lambda s: datetime.strptime(s, '%Y-%m-%d %H:%M:%S')

"""Apply aliasing to time values"""
alias = lambda n, X=15: X * math.floor(n / float(X))

"""Normalize value from 0-max to 0-1"""
normalize = lambda n, max: n / float(max)

"""De-normalize value from 0-1 to 0-max"""
denormalize = lambda n, max: n * float(max)

"""Normalize datetime object to minutes"""
datetime_to_minutes = lambda d: d.hour * 60. + d.minute

"""De-normalize the above to [hours, minutes]"""
minutes_to_hourmins = lambda n: map(int, str(timedelta(minutes=n))[:-3].split(':'))

"""Get weekday from datetime object"""
weekdayn_from_date = lambda d: d.weekday()

"""Get weekday string from weekday number"""
weekday_from_n = lambda n: calendar.day_name[n]

"""
Input format (sleepdata): Start; End; Sleep Quality; Time in bed; Notes; HR; Activity
Output format: list of [normalized start time, weekday number, time slept, sleep quality float]
"""
def build(from_file):
    output = []
    with open(from_file, 'r') as filehandle:
        items = [x.split(';') for x in filehandle.readlines()[1:]]

    for item in items:
        start_date = date(item[0])
        output.append([
            # Normalized start time
            normalize(alias(datetime_to_minutes(start_date), 15), 1440),
            # Weekday number
            normalize(weekdayn_from_date(start_date), 6.0),
            # Sleep time in minutes
            normalize(reduce(lambda h, m: int(h)*60 + int(m), item[3].split(':')), 1440),
            # Sleep quality as float
            float(item[2][:-1]) / 100.0,
        ])

    return np.array(output)

if __name__ == '__main__':
    input_filename = raw_input('path to sleepcycle dataset (./sleepdata.csv): ') or 'sleepdata.csv'
    with open(raw_input('output path (./sleepdata.pkl): ') or 'sleepdata.pkl', 'w') as output_file:
        data = build(input_filename)
        pickle.dump(data, output_file)

        print 'sample data', data[0]
