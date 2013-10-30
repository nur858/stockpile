"""
Simple RSA implementation in python
Written by nur hasan (nur858@gmail.com)
Partially credited: http://code.activestate.com/
For purely educational purpose.
"""
from fractions import gcd
from random import randrange
from collections import namedtuple
from math import log
from binascii import hexlify, unhexlify
import time
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
             

def multinv(modulus, value):
    x, lastx = 0, 1
    a, b = modulus, value
    while b:
        a, q, b = b, a // b, a % b
        x, lastx = lastx - q * x, x
    result = (1 - lastx * modulus) // value
    if result < 0:
        result += modulus
    assert 0 <= result < modulus and value * result % modulus == 1
    return result

KeyPair = namedtuple('KeyPair', 'public private')
Key = namedtuple('Key', 'exponent modulus')

def generate_prime(length):
    p = 1
    while not is_probably_prime(p):
        p = randrange(2**(length-1),2**length)
    print("Generated Prime: %s" % p)
    return p
def KeyGen(length):
    print("Prime Generation Started")
    p = generate_prime(length)
    q = generate_prime(length)
    N = p * q
    totient = (p - 1) * (q - 1)
    print("p*q = %s"% N)
    print("totient = %s"% totient)
    while True:
        private = randrange(2**(length-1), 2**length)
        if gcd(private, totient) == 1:
            break
    public = multinv(totient, private)
    print("public key = %s"% public)
    print("private key = %s"% private)
    assert public * private % totient == gcd(public, totient) == gcd(private, totient) == 1
    assert pow(pow(1234567, public, N), private, N) == 1234567
    return KeyPair(Key(public, N), Key(private, N))

def Encode(plaintextfile, pubkey):
    print("Encoding started with input file: %s" % plaintextfile)
    chunksize = int(log(pubkey.modulus, 256))
    outchunk = chunksize + 1
    outfmt = '%%0%dx' % (outchunk * 2,)
    f = open(plaintextfile, 'r')
    msg = f.read()
    f.close()
    bmsg = msg.encode()
    result = []
    for start in range(0, len(bmsg), chunksize):
        chunk = bmsg[start:start+chunksize]
        chunk += b'\x00' * (chunksize - len(chunk))
        plain = int(hexlify(chunk), 16)
        coded = pow(plain, *pubkey)
        bcoded = unhexlify((outfmt % coded).encode())
        result.append(bcoded)
    content = b''.join(result)
    outfile = plaintextfile + '_enc'
    f = open(outfile, 'w')
    f.write(content)
    f.close()
    print("Encoded text written to file : %s" % outfile)
    return outfile

def Decode(encryptedfile, privkey):
    print("Decoding Started with input file:%s" % encryptedfile)
    chunksize = int(log(pubkey.modulus, 256))
    outchunk = chunksize + 1
    outfmt = '%%0%dx' % (chunksize * 2,)
    f = open (encryptedfile, 'r')
    bcipher = f.read()
    f.close()
    result = []
    for start in range(0, len(bcipher), outchunk):
        bcoded = bcipher[start: start + outchunk]
        coded = int(hexlify(bcoded), 16)
        plain = pow(coded, *privkey)
        chunk = unhexlify((outfmt % plain).encode())
        result.append(chunk)
    content = b''.join(result).rstrip(b'\x00').decode()   
    outfile = encryptedfile + '_dec'
    f = open(outfile, 'w')
    f.write(content)
    f.close() 
    print("Decoded text written to file: %s" % outfile)
    return outfile


if __name__ == '__main__':
    starttime = time.time()
    pubkey, privkey = KeyGen(1024)
    endtime = time.time()
    print "KeyGen time:%s" % (endtime - starttime)
    f = open('pubkey', 'w')
    f.write("%s#%s" % (pubkey.exponent, pubkey.modulus))
    f.close()
    f = open("privkey", "w")
    f.write("%s#%s" % (privkey.exponent,privkey.modulus))
    f.close()
    
    f = open('pubkey', 'r')
    e, m = f.read().split("#")
    f.close()
    starttime = time.time()
    encfile = Encode('plaintext', Key(int(e),int(m)))
    endtime = time.time()
    print "Encrypt time:%s"% (endtime - starttime)
    f = open('privkey', 'r')
    e, m = f.read().split("#")
    f.close() 
    starttime = time.time()   
    p = Decode(encfile, Key(int(e), int(m)))
    endtime = time.time()
    print "Decrypt time:%s"% (endtime - starttime)    
    print "Completed...."