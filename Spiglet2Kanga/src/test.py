import os
import re
import filecmp

std_out_path = "./test/std_out/"
out_path = "./test/out/"
for filename in os.listdir(std_out_path):
    if filename != '.DS_Store':
        try:
            if filecmp.cmp(std_out_path+filename, out_path+filename):
                print("Correct: file[{}]".format(filename))
            else:
                print("Error: file[{}]".format(filename))
        except:
            print("File Open Error: file[{}]".format(filename))


    

