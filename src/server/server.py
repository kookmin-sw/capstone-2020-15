from flask import Flask, render_template, request, jsonify
import map_api, extractor
app = Flask(__name__)

arr = []

@app.route('/test', methods = ['GET'])
def req_test():
    #tmp = map_api.call_map()
    #json data recive
    # user_json = request.get_json()
    #print(tmp)
    return jsonify(tmp)

@app.route('/req', methods = ['POST'])
def in_test():
    #string _origin, string _destination, string _move_sec, string _preparations, string _wake_up_time
    #출발지
    _origin = request.form['_origin']
    _destination = request.form['_destination']
    _mode = request.form['_mode']
    _move_sec = request.form['_move_sec']
    # _preparations = request.form['_preparations']
    # _wake_up_time = request.form['_wake_up_time']

    tmp_json = map_api.call_map(_origin, _destination, _mode)
    extra = extractor.extra_time(tmp_json, _move_sec, _preparations, _wake_up_time)
    if extra == 0:
        return jsonify({'msg' : '0'})
    else:
        return jsonify({'msg' : '1'})

    return jsonify({'msg' : '2'}) 


if __name__ == '__main__':
    app.run(host="0.0.0.0")
