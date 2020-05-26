from flask import Flask, render_template, request, jsonify
import call_moving_sec
app = Flask(__name__)

arr = []

@app.route('/test', methods = ['GET'])
def req_test():
    tmp = call_moving_sec.call_req()
    json_obj = call_moving_sec.call_res(tmp)
    #json data recive
    # user_json = request.get_json()
    print(json_obj)
    return json_obj

@app.route('/in', methods = ['POST'])
def in_test():
    #id = request.form['id']
    #name = request.form['name']
    #form = request.form['form']

    test = request
    arr.append(test)
    #arr.append(id)
    #arr.append(name)
    #arr.append(form)
    print(arr)
    return jsonify({'message' : 'sucess'}) 


if __name__ == '__main__':
    app.run(host="0.0.0.0")
