
import os
import re
import filecmp

with open('run.sh', 'w') as f:
    f.write('javac Main.java\n')
    testpath = "./test/spg/"
    pigpath = "./test/kg/"
    outpath = "./test/out/"
    for filename in os.listdir(testpath):
        if filename[-3:] == '.spg':
            f.write('java Main {}\n'.format(testpath+filename))
            f.write('java -jar pgi.jar < {}.spg > {}.out\n'.format(pigpath+filename[:-4], outpath+filename[:-4]))
    f.write('python test.py\n')
            
