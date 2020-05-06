from flask import Flask, render_template, request, jsonify
app = Flask(__name__)

@app.route('/test')
#def test():
#  return render_template('sample.html')
def json_test():
    user_jon = request.get_json()
    print(user_jon)
    return jsonify(user_json)
    

if __name__ == '__main__':
    app.run(host="0.0.0.0")
