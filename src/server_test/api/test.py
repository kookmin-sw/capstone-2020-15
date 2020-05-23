from flask import Flask, render_template, request, jsonify
app = Flask(__name__)

<<<<<<< HEAD
@app.route('/test', methods = ['POST'])
# def test():
#    return render_template('sample.html')
def req_test():
    #json data recive
    user_json = request.get_json()
    print(user_json)
    return jsonify(user_json)

=======
@app.route('/test', methods=['GET'])
#def test():
#  return render_template('sample.html')
def json_test():
    print("json_test_in")
    user_jon = request.get_json()
    print(user_jon)
    return "<h1>hello</h1>"
    #return jsonify(user_json)
    
>>>>>>> b1937ff292fdd3d7a065a2b1fd513cb5701afa9d

if __name__ == '__main__':
    app.run(host="0.0.0.0")
