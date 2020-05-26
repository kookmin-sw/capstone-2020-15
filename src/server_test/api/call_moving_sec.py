# coding: utf-8
import time
import json
import os
import ssl
import urllib.request

def call_req():
    client = None
    with open("./key.json","r") as clientJson :
        client = json.load(clientJson)

    #출발지 일단은 고정
    origin          = "37.5728359,126.9746922"
    #도착지
    destination     = "37.5129907,127.1005382"
    #모드 => driving, walking, bicycling, transit
    #모드 => 드라이빙, 도보, 자전거, 대중교통
    mode            = "transit"
    #출발시간
    departure_time  = "now"
    #키값
    key             = client["key"]

    url = "https://maps.googleapis.com/maps/api/directions/json?origin="+ origin \
            + "&destination=" + destination \
            + "&mode=" + mode \
            + "&departure_time=" + departure_time\
            + "&language=ko" \
            + "&key=" + key

    request         = urllib.request.Request(url)
    context         = ssl._create_unverified_context()
    response        = urllib.request.urlopen(request, context=context)
    responseText    = response.read().decode('utf-8')
    responseJson    = json.loads(responseText)

    return responseJson

def call_res(json_msg):
    path            = json_msg["routes"][0]["legs"][0]
    duration_sec    = path["duration"]["value"]
    json_obj = {'duration_sec' : duration_sec}
    # start_geo       = path["start_location"]
    # end_geo         = path["end_location"]
    # stepList = path["steps"]
    # print(stepList[0])
    print(duration_sec) # 전체 걸리는 시간을 초로 나타낸 것
    # print(start_geo)	# 출발지 위도,경도
    # print(end_geo)	# 도착지 위도,경도

    return json_obj