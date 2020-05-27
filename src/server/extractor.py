# coding: utf-8
import time
import json
import os
import ssl
import urllib.request
import map_api

#현재시간, 출발시간의 차이가 5분이상이면 무조건 0을 return
#현재시간, 출발시간의 차이가 5분이하라면
#이동시간+준비시간 > 현재걸리는시간+준비시간 -> 원래시간으로 일어나도된다
#이동시간+준비시간 < 현재걸리는시간+준비시간 -> 예정보다 일찍 일어나야한다.

#입력받아야하는것 : 구글맵api json / 원래잡은이동시간 / 준비시간 / 기상시간
#구해야하는것 : 출발시간(UTC 1970 년 1월 1일 0시 0분 0초 부터 경과한 초를 정수로 반환한 값) / 걸리는시간
def call_res(json_obj):
    path            = json_obj["routes"][0]["legs"][0]
    duration_sec    = path["duration"]["value"]

    start_geo       = path["start_location"]
    departure_time = path["departure_time"]["value"]
    # end_geo         = path["end_location"]
    # stepList = path["steps"]
    # print(stepList[0])
    print(duration_sec) # 전체 걸리는 시간을 초로 나타낸 것
    # print(start_geo)	# 출발지 위도,경도
    # print(end_geo)	# 도착지 위도,경도

    return json_obj