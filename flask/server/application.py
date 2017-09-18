from flask import Flask, render_template, request, jsonify # ,redirect, url_for ,jsonify ,flash, session
import actors
from modules import get_modules, index_module, project_module

app = Flask(__name__)
view_data = {}


def render(template, args):
    args['available_modules'] = get_modules()
    try:
        return render_template(template, **args)
    except Exception as e:
        response = jsonify({
            'status': 'error',
            'message': 'page not found in this server',
            'exception': "{}".format(e)
            }
        )
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


if __name__ == '__main__':
    app.run(debug=True, threaded=True, host='0.0.0.0', port=80)
