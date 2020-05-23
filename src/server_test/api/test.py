from flask import Flask, render_template, request, jsonify
app = Flask(__name__)

@app.route('/test', methods = ['POST'])
# def test():
#    return render_template('sample.html')
def req_test():
    #json data recive
    user_json = request.get_json()
    print(user_json)
    return jsonify(user_json)


if __name__ == '__main__':
    app.run(host="0.0.0.0")
