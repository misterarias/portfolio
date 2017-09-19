import os

from flask import Flask, render_template, request, jsonify # ,redirect, url_for ,jsonify ,flash, session
import actors
from modules import *

app = Flask(__name__)
view_data = {}

# Environment variables
debug_mode = str(os.environ.get('DEBUG_MODE', '0')) == '1'
http_port = int(os.environ.get('HTTP_PORT', '8080'))

def render(template, args={}):
    args['available_modules'] = get_modules()
    try:
        return render_template(template, **args)
    except Exception as e:
        response_body = {
            'status': 'error',
            'message': 'page not found in this server',
            }
        if debug_mode:
            response_body['exception'] = "{}".format(e)

        response = jsonify(response_body)
        response.status_code = 404
        return response


@app.route(index_module.path, strict_slashes=False)
def index():

    view_data['active_module'] = index_module
    view_data['juan'] = actors.juan
    view_data['utad'] = actors.utad

    return render('Index/main.html', view_data)


@app.route(project_module.path, strict_slashes=False)
def project():

    view_data['active_module'] = project_module

    return render('Project/main.html', view_data)

@app.route(data_module.path, strict_slashes=False)
def data():
    view_data['active_module'] = data_module
    return render("Data/main.html", view_data)


@app.route(processor_module.path, strict_slashes=False)
def processor():
    view_data['active_module'] = processor_module
    return render("Processor/main.html", view_data)


@app.route(conclussions_module.path, strict_slashes=False)
def conclussions():
    view_data['active_module'] = conclussions_module
    return render("Conclusions/main.html", view_data)

if __name__ == '__main__':
    app.run(
            debug=debug_mode,
            threaded=True,
            host='0.0.0.0',
            port=http_port
            )
