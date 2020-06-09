# coding: utf-8
import time
import json
import os
import ssl
import urllib.request
#def call_map():
def call_map(_origin, _destination, _ready):
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
    
    #구글맵 api 결과 json return
    return responseJson