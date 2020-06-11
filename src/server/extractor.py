# coding: utf-8
import time
import json
import os
import ssl
import urllib.request
from datetime import datetime

#case1 : 현재시간, 출발시간의 차이가 10분이상이면 무조건 0을 return
#case2 : 현재시간, 출발시간의 차이가 10분이하라면
#case3 : if : 현재시간 + 준비시간 + 걸리는시간 > 일정 시작 시간 -> 예정보다 일찍 일어나야한다.
#case4 : else : -> 원래시간으로 일어나도된다.

def extra_time(json_obj, _start):
    _ready = 3600
    #경로
    path            = json_obj["routes"][0]["legs"][0]
    #걸리는 시간
    duration_sec    = path["duration"]["value"]

    time_format = '%Y-%m-%dT%H:%M'
    now = datetime.strptime(_start, time_format)
    time_tuple = now.timetuple()
    print(time_tuple)
    utc_now = time.mktime(time_tuple)
    print(utc_now)

    now = time.time()
    print(utc_now-now-3600*9)
    print(type(now))
    print(type(duration_sec))

    #출발시간
    departure_time = path["departure_time"]["value"]
    print("현재시간(UTC) : ", now)
    print("출발시간(UTC) : ", departure_time)
    print("이동하는데 걸리는 시간 : ", duration_sec)
    print("준비시간 : ", _ready)
    print("일정 시작 시간 : ", _start)
    print(now-departure_time)
    print(now + _ready + duration_sec)
    print(utc_now)
    #case 1
    if now - departure_time > 600:
        return 0
    #case2
    else:
        #case3
        if now + _ready + duration_sec >= utc_now:
            return 1
        else:
            return 0

    return 0
