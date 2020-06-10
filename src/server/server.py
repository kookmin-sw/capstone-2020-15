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

@app.route('/req', methods = ['POST'])
def in_test():
    obj = request.get_json()
    print(obj)
    #출발지
    _clat = request.form["clat"]
    _clng = request.form["clng"]
    _origin = _clat + ',' + _clng

    #도착지
    _dlat = request.form["dlat"]
    _dlng = request.form["dlng"]
    _destination = _dlat + ',' + _dlng

    #일정 시작 시간
    _start = request.form["start"]
    print("clat : ", _clat)
    print("clng : ", _clng)
    print("출발지 : ", _origin)
    print()

    print("dlat : ", _dlat)
    print("dlng : ", _dlng)
    print("도착지 : ", _destination)
    print()

    print("일정 시작시간 : ", _start)

    tmp_json = map_api.call_map(_origin, _destination)
    extra = extractor.extra_time(tmp_json, _start)
    extra = 0
    if extra == 0:
        return jsonify({'msg' : '0'})
    else:
        return jsonify({'msg' : '1'})

    return jsonify({'msg' : '0'})


if __name__ == '__main__':
    app.run(host="0.0.0.0")
