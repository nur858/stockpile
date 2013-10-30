"""
Simple crypto voting based on paillier crypto system
based on http://security.hsr.ch/msevote/seminar-papers/HS09_Homomorphic_Tallying_with_Paillier.pdf
Implemented by Nur (nur858@gmail.com)
For purely educational purpose.
"""
import paillier
def cast_vote(choice,pk, tally):
    c = paillier.encrypt(pk, choice)
    if tally ==0:
        return c
    return paillier.p_add(tally, c, pk)
def count_vote(tally, pk, sk, base):
    tally = paillier.decrypt(pk, sk, tally)
    count = []
    while tally:
        count.append(tally%base)
        tally = tally/base
    return count
        
def find_base(voters):
    return 10 **len(str(voters))    

def main():
    numvoters = 5 #total number of voters.
    b = find_base(numvoters)
    sA = b**0 # symbol for candidate A
    sB = b**1 # symbol for candidate B
    sC = b**2 # symbol for candidate C
    pk, sk = paillier.keygen(128) 
    votecount = cast_vote(sA,pk, 0)
    votecount = cast_vote(sB,pk, votecount)
    votecount = cast_vote(sA,pk, votecount)
    votecount = cast_vote(sA,pk, votecount)
    votecount = cast_vote(sC,pk, votecount)
     
     
    result = count_vote(votecount, pk, sk, b)
    
    print "Candidate A got: {0} votes" .format(result[0]);
    print "Candidate B got: {0} votes" .format(result[1]);
    print "Candidate C got: {0} votes" .format(result[2]);

if __name__ == "__main__":
    main()



