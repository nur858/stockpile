import itertools as it

class FeatureSet(object):
    
    def __init__(self,feature,fs):
        self.feature = feature
        self.fs = fs
        self.stumps = self.create_stumps(fs)
    def create_stumps(self,fs):
        fs.sort(key=lambda s:s[1])
        t = self.__class__.thresholds(list(set([f[1] for f in fs])))
        
        
    
    @staticmethod
    def thresholds(fs):
        tv = [(a + b) / 2.0 for a, b in it.imap(None, fs, fs[1:])]
        return [tv[0]-1] + tv + [tv[-1]+1]
    
class StumpCollection(object):    
    def __init__(self,dataset):
        self.feature_set = self.create_featureset(dataset)

    def optimal(self):
        pass
    def random(self):
        pass
    
    def create_featureset(self,trainset):
        fct = len(trainset.data[0].fv)
        fs = []
        for i in range(fct):
            fs.append(FeatureSet(i,[(j,dp.fv[i]) for j,dp in enumerate(trainset.data)]))
        return fs
            
            
                    
        
    
    
    