import csv
import time

def redCsvFile(filename):
    with open(filename) as csvfile:
#        reader = csv.reader(csvfile)
#        for i, row in enumerate(reader):
#            pass
#        print(i)
        for line in csvfile:
            pass

starttime = time.clock()
redCsvFile("c://temp//log.txt")
timeTaken = time.clock() - starttime

print("Time taken = ")
print(timeTaken)


starttime = time.clock()
redCsvFile("c://temp//log.txt")
timeTaken = time.clock() - starttime

print("Time taken = ")
print(timeTaken)
