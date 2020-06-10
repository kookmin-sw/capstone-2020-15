from flask import Flask, render_template, request, jsonify
import map_api, extractor, reverse_alarm
app = Flask(__name__)

@app.route('/reverse', methods=['POST'])
def reverse():
    #str = request.form['id']
    obj = request.get_json()
    print(obj)
    #arr = _str.split(':')
    #_h = arr[0]
    #_min = arr[1]
    #print(_h)
    #print(_min)
    #ex) pm 9:30 -> string으로 넘겨주자
    #tmp_json = reverse_alarm.calculateWaketimes(_h, _min)
    return jsonify({'msg' : '0'})

@app.route('/', methods = ['POST'])
def in_test():
    obj = request.get_json()
    print(obj)
    #출발지
    #_origin = request.form["origin"]
    _lat = request.form["lat"]
    _lng = request.form["lng"]
    _destination = request.form["destination"]
    #_start = request.form["strat"]
    #_ready = request.form["ready"]
    print("위도 : ", _lat)
    print("경도 : ", _lng)
    print("도착지 : ", _destination)

    #tmp_json = map_api.call_map(_origin, _destination)
    #extra = extractor.extra_time(tmp_json, _start, _ready)
    extra = 0
    if extra == 0:
        return jsonify({'msg' : '0'})
    else:
        return jsonify({'msg' : '1'})

    return jsonify({'msg' : '2'})


if __name__ == '__main__':
    app.run(host="0.0.0.0")
