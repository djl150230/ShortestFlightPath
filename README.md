Project 6
David Lanius
CS 3345.003

This project takes 3 command line arguments. It is important to note the order of these arguments:
input query output

Example input/output:

command line args: input.txt query.txt output.txt

Contents of input.txt:
Dallas|Austin|98|47
Austin|Houston|95|39
Dallas|Houston|101|51
Austin|Chicago|144|192
Chicago|Austin|155|200
Austin|Dallas|100|50
Houston|Dallas|100|50

Contents of query.txt:
Dallas|Houston|T
Chicago|Dallas|C
NewYork JFK|Dallas|C

Contents of resulting output.txt:
1|Dallas|Houston|101|51
2|Chicago|Austin|Dallas|255|250
NO FLIGHT AVAILABLE FOR THE REQUEST
