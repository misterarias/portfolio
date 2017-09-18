class Module(object):
    title = None
    subtitle = None

    def navigation(self):
        pass

    def get_title(self):
        return self.title

    def get_subtitle(self):
        return self.subtitle

    def next_module(self):
        return None

    def previous_module(self):
        return None

class Index(Module):
    title = "Topic modeling historical data"
    subtitle = "Distributed discovery of topics over GDELT data using Spark's MLLib and Elastic Search"
    label = 'Home'
    path = '/'

    def next_module(self):
        return project_module

    def previous_module(self):
        return None

class Project(Module):
    title = "Project overview"
    subtitle = "Eagle-eye over the main components of a Big Data problem"
    label = 'Overview'
    path = '/project'

    def next_module(self):
        return None

    def previous_module(self):
        return index_module


index_module = Index()
project_module = Project()
def get_modules():
    return [index_module, project_module]
