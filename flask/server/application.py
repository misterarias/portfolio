from flask import Flask, render_template, request, jsonify # ,redirect, url_for ,jsonify ,flash, session


app = Flask(__name__)

class Actor(object):
    def __init__(self, name, company, description, icon):
        self.name = name
        self.company = company
        self.description = description
        self.image_name = icon
        self.role = None
        self.company_url = None
        self.social = []

    def setRole(self, role):
        self.role = role
        return self

    def setCompanyUrl(self, company_url):
        self.company_url = company_url
        return self

    def addSocial(self, social_fa_icon, social_url):
        self.social.append({
            'url': social_url,
            'icon': social_fa_icon
            })
        return self

class Module(object):
    pass

class Index(Module):
    label = 'Index'
    path = '/'

@app.route(Index().path, strict_slashes=False)
def index():

    view_data = {
            'active_module': {
                'previous_module': None,
                'next_module': Project(),
                'current_module': Index()
            }
    }

    juan = Actor(
            "Juan Arias",
            "Schibsted Products&Technology",
            "I've been working with computer systems for the past ten years, and been learning new things every day. I consider myself a very curious person, so getting expertise on Big Data systems was something I had been looking forward for a long time. Looking for the next challenge!",
            "juan-icon.jpg"
    )
    juan.setRole("Developer Advocate") \
        .setCompanyUrl("http://www.schibsted.com/") \
        .addSocial("fa-twitter", "https://twitter.com/ariasFreire") \
        .addSocial("fa-github", "https://github.com/misterarias/") \
        .addSocial("fa-linkedin", "https://www.linkedin.com/profile/view?id=96626628")

    view_data['juan'] = juan

    utad = Actor(
            "Big Data Expert",
            "U-Tad Madrid",
            "The U-tad's Certificate program in Big Data allows students to lead the design, management and exploitation of next generation infrastructures handling large volumes of data.",
            "utad-icon.png"
    )
    utad.setCompanyUrl("https://www.u-tad.com/en/studies/big-data-expert/") \
        .addSocial("fa-twitter", "https://twitter.com/U_tad") \
        .addSocial("fa-facebook", "https://es-es.facebook.com/utadcentrouniversitario") \
        .addSocial("fa-linkedin", "http://www.linkedin.com/company/u-tad")

    view_data['utad'] = utad

    return render('Index/main.html', view_data)


def render(template, args):
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

class Project(Module):
    label = 'Project'
    path = '/project'

@app.route(Project().path, strict_slashes=False)
def project():
    view_data = {
            'active_module': {
                'previous_module': Index(),
                'next_module': None,
                'current_module': Project()
            }
    }
    return render('Project/main.html', view_data)



if __name__ == '__main__':
    app.run(debug=True, threaded=True, host='0.0.0.0', port=80)
