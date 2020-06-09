from flask import Flask, render_template, request, jsonify
import map_api, extractor, reverse_alarm
app = Flask(__name__)

@app.route('/in', methods=['POST'])
def test():
    return jsonify({'msg' : '0'})

@app.route('/test', methods=['POST', 'GET'])
def reverse_alarm():
    #str = request.form['id']
    obj = request.get_json()
    print(obj)
    #arr = _str.split(':')
    #_h = arr[0]
    #_min = arr[1]
    #print(_h)
    #print(_min)
    #tmp_json = reverse_alarm.calculateWaketimes(_h, _min)
    return jsonify({'msg' : '0'})

@app.route('/req', methods = ['POST'])
def in_test():
    obj = request.get_json()
    print(obj)
    #string _origin, string _destination, string _move_sec, string _preparations, string _wake_up_time
    #출발지
    _origin = obj["origin"]
    _destination = obj["destination"]
    _ready = obj["ready"]

    # _preparations = request.form['_preparations']
    # _wake_up_time = request.form['_wake_up_time']

    #tmp_json = map_api.call_map(_origin, _destination, _mode)
    #extra = extractor.extra_time(tmp_json, _move_sec, _preparations, _wake_up_time)
    extra = 0
    if extra == 0:
        return jsonify({'msg' : '0'})
    else:
        return jsonify({'msg' : '1'})

    return jsonify({'msg' : '2'}) 


if __name__ == '__main__':
    app.run(host="0.0.0.0")
