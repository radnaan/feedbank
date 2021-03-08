from flask import Flask, jsonify, request

from Classifier import classify_text
from Simplifier import identify_requests

app = Flask(__name__)

#At the url just returns the message as a string
@app.route("/")
def index():
    return "Analysis is currently running. To use go to /analyse and provide text=<text here>"

# This function requires a parameter from the URL.
@app.route("/analyse", methods=["GET"])
def analyse_feedback():
    result = {}

    #Classifies the provided sentence
    #Will fail if the param is not provided
    text = request.args.get('text')
    mood = classify_text(text)
    requests = identify_requests(text)
    
    result = jsonify(
        {
            "mood": mood,
            "requests": ','.join(requests)
            }
        )
    
    return result

# Checks to see if the name of the package is the run as the main package.
if __name__ == "__main__":
    # Runs the Flask application only if the main.py file is being run.
    app.run()