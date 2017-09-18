class Module(object):
    title = None
    subtitle = None

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
        return processor_module

    def previous_module(self):
        return index_module

class Processor(Module):
    label = "Processing"
    title = "The data processing layer"
    subtitle = "Train LDA over 50000 documents using Spark and MLLib"
    path = '/processor'

    def next_module(self):
        return data_module

    def previous_module(self):
        return project_module

class Data(Module):
    path = '/data'
    label = "Visualization"
    title = "The data visualization layer"
    subtitle = "Representation of the topics inferred from the data"
    def next_module(self):
        return conclussions_module

    def previous_module(self):
        return processor_module

class Conclusions(Module):
    path = '/conclussions'
    label = "Conclussions"
    title = "Project conclusions"
    subtitle = "Summary of lessons learned and future steps that would be taken"
    def next_module(self):
        return None

    def previous_module(self):
        return data_module

index_module = Index()
project_module = Project()
processor_module = Processor()
data_module = Data()
conclussions_module = Conclusions()

def get_modules():
    return [index_module, project_module, processor_module, data_module, conclussions_module]
