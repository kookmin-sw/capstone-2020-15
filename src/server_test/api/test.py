from flask import Flask, render_template, request, jsonify
app = Flask(__name__)

@app.route('/test', methods=['GET'])
#def test():
#  return render_template('sample.html')
def json_test():
    print("json_test_in")
    user_jon = request.get_json()
    print(user_jon)
    return "<h1>hello</h1>"
    #return jsonify(user_json)
    

if __name__ == '__main__':
    app.run(host="0.0.0.0")
