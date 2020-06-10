# coding: utf-8
import time
import json
import os
import ssl
import urllib.request

#case1 : 현재시간, 출발시간의 차이가 10분이상이면 무조건 0을 return
#case2 : 현재시간, 출발시간의 차이가 10분이하라면
#case3 : 이동시간+준비시간 > 현재걸리는시간+준비시간 -> 원래시간으로 일어나도된다
#case4 : 이동시간+준비시간 < 현재걸리는시간+준비시간 -> 예정보다 일찍 일어나야한다.

#입력받아야하는것 : 구글맵api json / 원래잡은이동시간 / 준비시간 / 기상시간
#준비시간 / 기상시간은 딱히 필요 없을듯?
#구해야하는것 : 출발시간(UTC 1970 년 1월 1일 0시 0분 0초 부터 경과한 초를 정수로 반환한 값) / 걸리는시간
def extra_time(json_obj, _start, _ready=3600):
    #경로
    path            = json_obj["routes"][0]["legs"][0]
    #걸리는 시간
    duration_sec    = path["duration"]["value"]

    now = time.time()
    #출발시간
    departure_time = path["departure_time"]["value"]
    print("현재시간(UTC) : ", now)
    print("출발시간(UTC) : ", departure_time)
    print("이동하는데 걸리는 시간 : ", duration_sec)
    print("준비시간 : ", _ready)
    print("일정 시작 시간 : ", _start)
    #case 1
    if now - departure_time > 600:
        return 0
    #case2
    else:
        #case3
        if now + _ready + duration_sec >= _start:
            return 1
        else:
            return 0

    return 0
