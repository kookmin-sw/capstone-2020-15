from flask import Flask, render_template, request, jsonify
import map_api
app = Flask(__name__)

arr = []

@app.route('/test', methods = ['GET'])
def req_test():
    tmp = map_api.call_map()
    #json data recive
    # user_json = request.get_json()
    #print(tmp)
    return jsonify(tmp)

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
