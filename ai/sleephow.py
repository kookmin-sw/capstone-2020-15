# Sleep predictor end client
# Copyright (c) Andreas Urbanski, 2017
from predict import predict_sleep_time_and_quality
from build import *
import datetime
import os

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

h, m = map(int, (raw_input('At what time are you going to bed? (format=HH:MM) ') or '00:00').split(':'))
w = int(raw_input('Weekday diff from today (default=0)? ') or 0)

date = datetime.datetime.today().replace(hour=h, minute=m) + datetime.timedelta(hours=(24 * w))

p = predict_sleep_time_and_quality(date)

sleep_time = minutes_to_hourmins(int(p[0]))
sleep_quality = p[1] * 100.0

wake_time = date + datetime.timedelta(hours=sleep_time[0], minutes=sleep_time[1])

print '\nYou will sleep {0} hours {1} minutes, wake up on\n\t{2}\n\t\twith sleep quality {3:.1f}%'.format(
    sleep_time[0],
    sleep_time[1],
    wake_time.strftime('%A, %d/%m at approximately %H:%M'),
    sleep_quality
)

print
