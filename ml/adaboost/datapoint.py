
class DataPoint(object):
    def __init__(self, index,fv, label):
        self.id             = index
        self.fv = fv
        self.label          = label
    def __str__(self):
        return '{} labeled {}'.format(self.fv, self.label)

    def __repr__(self):
        return '{}({}, {})'.format(self.__class__.__name__, self.fv,
                                   self.label)

          
    