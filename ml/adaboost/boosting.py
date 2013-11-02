import spambase
from decisionstumps import StumpCollection

def main():
    spambase.load('spambase.data')
    sc = StumpCollection(spambase.trainset)

if __name__ == '__main__':
    main()