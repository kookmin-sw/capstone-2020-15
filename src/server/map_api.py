# coding: utf-8
import time
import json
import os
import ssl
import urllib.request
#def call_map():
def call_map(_origin, _destination):
    client = None
    with open("./key.json","r") as clientJson :
        client = json.load(clientJson)

    #출발지 일단은 고정(도농역)
    origin          = 37.608797, 127.161123
    
    #도착지
    destination     = _destination
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

    print("JSON객체")
    print(responseJson)
    print("============")

    #구글맵 api 결과 json return
    return responseJson
