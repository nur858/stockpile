class Trainset(object):
    def __init__(self):
        self.data    = []
        self.count   = 0
        self.spam    = 0
        self.nonspam = 0
         
    def add(self,dp):
        self.count += 1        
        self.data.append(dp)        
        if dp.label == 0: self.nonspam += 1 
        if dp.label == 1: self.spam    += 1        