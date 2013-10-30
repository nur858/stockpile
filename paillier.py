"""
Paillier cryptosystem implementation in python
credit: wikipedia.org
Implemented by Nur (nur858@gmail.com)
For purely educational purpose.
"""
from fractions import gcd
from random import randrange
from collections import namedtuple
PK = namedtuple('PK', 'n g')
SK = namedtuple('SK', 'lam meu')

def invmod(a, p, maxiter=1000000):
    """The multiplicitive inverse of a in the integers modulo p:
         a * b == 1 mod p
       Returns b.
       (http://code.activestate.com/recipes/576737-inverse-modulo-p/)"""
    if a == 0:
        raise ValueError('0 has no inverse mod %d' % p)
    r = a
    d = 1
    for i in xrange(min(p, maxiter)):
        d = ((p // r + 1) * d) % p
        r = (d * a) % p
        if r == 1:
            break
    else:
        raise ValueError('%d has no inverse mod %d' % (a, p))
    return d

def rabin_miller(p,k):
    n = p - 1
    s, d = 0, n
    while not d & 1:
        s,d = s + 1, d>>1
    assert 2 ** s * d ==  n and d&1
    for i in xrange(k):
        a = randrange(2, p -1)
        x = pow(a,d, p)
        if x in (1, n):
            continue
        for r in xrange(1, s):
            x = x **2 % p
            if x == 1:
                return False
            if x == n:
                break
        else:
            return False            
    return True

def is_probably_prime(p, k=5):
    if p <= 3:
        return p == 2 or p == 3
    if (p & 1) == 0: # even number 
        return False 
    lowPrimes = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43
                 ,  47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101
                 , 103, 107, 109, 113, 127, 131, 137, 139, 149, 151
                 , 157, 163, 167, 173, 179, 181, 191, 193, 197, 199
                 , 211, 223, 227, 229, 233, 239, 241, 251, 257, 263
                 , 269, 271, 277, 281, 283, 293, 307, 311, 313, 317
                 , 331, 337, 347, 349, 353, 359, 367, 373, 379, 383
                 , 389, 397, 401, 409, 419, 421, 431, 433, 439, 443
                 , 449, 457, 461, 463, 467, 479, 487, 491, 499, 503
                 , 509, 521, 523, 541, 547, 557, 563, 569, 571, 577
                 , 587, 593, 599, 601, 607, 613, 617, 619, 631, 641
                 , 643, 647, 653, 659, 661, 673, 677, 683, 691, 701
                 , 709, 719, 727, 733, 739, 743, 751, 757, 761, 769
                 , 773, 787, 797, 809, 811, 821, 823, 827, 829, 839
                 , 853, 857, 859, 863, 877, 881, 883, 887, 907, 911
                 , 919, 929, 937, 941, 947, 953, 967, 971, 977, 983
                 , 991, 997]
    for pp in lowPrimes:
        if pp == p: return True    
        elif (p % pp) == 0: return False
    return rabin_miller(p,k)

def generate_prime(length):
    p = 1
    while not is_probably_prime(p):
        p = randrange(2**(length-1),2**length)
    return p

def gen_prime_pair(length):
    while True:
        p = generate_prime(length)
        q = generate_prime(length)
        if gcd(p*q, (p - 1)* (q - 1)) == 1:
            break
    return (p,q)
def L ( u, n):
    return (u - 1)/n
def keygen(length):
    (p,q) = gen_prime_pair(length)
    n = p * q
    g = n + 1
    phi = (p -1) * (q - 1)
    lam = phi    
    n2 = n ** 2
    meu = invmod(phi, n)
    pk, sk = PK(n, g), SK(lam, meu)
    return (pk,sk)
def p_add(c1, c2, pk):
    n2 = pk.n * pk.n
    return c1 * c2 % n2

def encrypt(pk, m):
    r = randrange(pk.n)
    n2 = pk.n * pk.n
    c = (pow(pk.g, m, n2) * pow(r,pk.n, n2)) % n2 
    return c

def decrypt(pk, sk, c):
    n2 = pk.n * pk.n
    x = pow(c,sk.lam,n2) - 1
    
    m = ((x//pk.n) * sk.meu) % pk.n
    return m

def main():
    pk, sk = keygen(1024)
    m1 = 12345678987654321
    c1 = encrypt(pk,m1)
    m2 = 12345761234123409
    c2 = encrypt(pk, m2)
    m3 = m1 + m2
    c = p_add(c1, c2, pk)
    
    
    print "m1+m2 = {0}".format(m3)
    print "decrypted value: {0}" .format(decrypt(pk, sk, c))

if __name__ == "__main__":    
    main()