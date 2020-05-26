from flask import Flask, render_template, request, jsonify
import call_moving_sec
app = Flask(__name__)

@app.route('/test', methods = ['POST'])
# def test():
#    return render_template('sample.html')
def req_test():
    tmp = call_moving_sec.call_req()
    json_obj = call_moving_sec.call_res(tmp)
    #json data recive
    # user_json = request.get_json()
    print(json_obj)
    return json_obj

if __name__ == '__main__':
    app.run(host="0.0.0.0")
