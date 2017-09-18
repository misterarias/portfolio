class Module(object):

    def __init__(self):
        pass

    def navigation(self):
        pass

class Index(Module):
    label = 'Index'
    path = '/'

    def navigation(self):
        return {
                'previous_module': None,
                'next_module': project_module,
                'current_module': self
                }

class Project(Module):
    label = 'Project'
    path = '/project'

    def navigation(self):
        return {
                'previous_module': index_module,
                'next_module': None,
                'current_module': self
            }

index_module = Index()
project_module = Project()
def get_modules():
    return [index_module, project_module]
