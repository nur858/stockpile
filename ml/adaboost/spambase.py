from testset import Testset
from trainset import Trainset
from datapoint import DataPoint

dataformat = []
#             48 continuous real [0,100] attributes of type word_freq_WORD
dataformat += 48 * [(float, lambda x: 0 <= x <= 100)]
#              6 continuous real [0,100] attributes of type char_freq_CHAR
dataformat +=  6 * [(float, lambda x: 0 <= x <= 100)]
#              1 continuous real [1,...] attribute of type capital_run_length_average
dataformat +=  1 * [(float, lambda x: 0 <= x)]
#              1 continuous integer [1,...] attribute of type capital_run_length_longest
dataformat +=  1 * [(int, lambda x: 0 <= x)]
#              1 continuous integer [1,...] attribute of type capital_run_length_total
dataformat +=  1 * [(int, lambda x: 0 <= x)]
#              1 nominal {0,1} class attribute of type spam
dataformat +=  1 * [(int, lambda x: x == 1 or x == 0)]

data  = []
count = 0  
trainset = Trainset()
testset  = Testset()

def validate(s, fs):
    t,v = fs
    d = t(s)
    
    if v(d):
        return d
    else:
        raise ValueError(s + ' is not valid feature value')
    
def load(datafile):
    ct = 0
    with open(datafile,'r') as fp:
        for line in fp:
            ct += 1
            item = [validate(f.strip(),fmt) for f,fmt in zip(line.split(","), dataformat) ]
            data.append(item)
            dp = DataPoint(ct,item[0:-1], item[-1])
            if ct % 10 == 1:
                testset.add(dp)
            else:
                trainset.add(dp)
    count = ct
    print "data points read: {}".format(count)
    print "data points in trainset: {}".format(trainset.count)
    print "data points in testset: {}".format(testset.count)
                
