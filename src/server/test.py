from flask import Flask, render_template, request, jsonify
import map_api, extractor, reverse_alarm

tmp_json = map_api.call_map('37.5728359,126.9746922', '37.5129907,127.1005382')
extra = extractor.extra_time(tmp_json, '2020-06-11T22:00')
