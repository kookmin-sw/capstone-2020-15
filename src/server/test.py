from flask import Flask, render_template, request, jsonify
import map_api, extractor, reverse_alarm, time

tmp_json = map_api.call_map('37.5728359,126.9746922', '37.5129907,127.1005382')
extra = extractor.extra_time(tmp_json, '2020-06-11T22:00')

print()

now = time.time()
local_tuple = time.localtime(now)
time_format = '%Y-%m-%dT%H:%M'
time_str = time.strftime(time_format, local_tuple)
print(time_str)
