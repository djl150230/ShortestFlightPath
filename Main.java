/*
 David Lanius
 Class: CS 3345
 Section: 003
 Semester: Spring 2018
 Project #6: This program implements Dijkstra's algorithm for flight paths. It takes
 three command-line arguments, the name of the input file, the name of the query file,
 and the name of the output file, all three in the forms of a String.
 */

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.lang.*;
import java.util.Stack;


public class Main {
    public static class City implements Comparable<City> {
        private String name;

        public int distance;
        public int otherDistance;

        public City last;

        City() {
            name = "";

            neighbors = new ArrayList<City>();

            cost = new ArrayList<Integer>();

            time = new ArrayList<Integer>();

            distance = Integer.MAX_VALUE;

            otherDistance = Integer.MAX_VALUE;

            last = null;
        }

        City(String s) {
            name = s;

            neighbors = new ArrayList<City>();

            cost = new ArrayList<Integer>();

            time = new ArrayList<Integer>();

            distance = Integer.MAX_VALUE;

            otherDistance = Integer.MAX_VALUE;

            last = null;

        }

        //holds city names pointed to by this city
        public ArrayList<City> neighbors;

        //holds corresponding cost to get to each neighbor
        public ArrayList<Integer> cost;

        //holds corresponding time to get to each neighbor
        public ArrayList<Integer> time;

        public int compareTo(City c) {
            return Integer.compare(this.distance, c.distance);
        }

    } //end class City

    public static void main(String[] args) throws IOException {
        //scanner for reading files
        Scanner scanner = new Scanner(System.in);

        //city arraylist to hold each city from data file
        ArrayList<City> cities = new ArrayList<City>();

        String inputFileName = args[0];

        String instructionFileName = args[1];

        String outputFileName = args[2];

        //new input file
        File myInputFile = new File(inputFileName);
        Scanner readFile = new Scanner(myInputFile);

        //reading file line by line
        while (readFile.hasNext()) {
            //read line
            String line = readFile.nextLine();

            //split line with delimiter
            String[] contents = line.split("\\|");

            //assign each of the 4 parts to their respective variables
            String city1 = contents[0];
            String city2 = contents[1];
            int cost = Integer.parseInt(contents[2]);
            int time = Integer.parseInt(contents[3]);

            //assume cities do not already exist
            boolean add1 = false;
            boolean add2 = false;

            //test if cities exist
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(city1))
                    add1 = true;
                if (cities.get(i).name.equals(city2))
                    add2 = true;
            }

            //add cities if they do not exist yet
            if (!add1)
                cities.add(new City(city1));
            if (!add2)
                cities.add(new City(city2));

            //get indexes of 2 working cities
            int index1 = 0;
            int index2 = 0;

            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(city1))
                    index1 = i;
                if (cities.get(i).name.equals(city2))
                    index2 = i;
            }

            //assign values to neighbors, cost, and time of working city
            cities.get(index1).neighbors.add(cities.get(index2));
            cities.get(index1).cost.add(cost);
            cities.get(index1).time.add(time);

        }
        //close file
        readFile.close();


        //open output file
        File myOutputFile = new File(outputFileName);
        BufferedWriter outFile = new BufferedWriter(new FileWriter(myOutputFile));

        //open query input file
        File myInputFile2 = new File(instructionFileName);
        Scanner readFile2 = new Scanner(myInputFile2);


        while (readFile2.hasNext()) {
            //reset city data for next call to dijkstras()
            for (City c : cities) {
                c.distance = Integer.MAX_VALUE;
                c.otherDistance = 0;
                c.last = null;
            }

            //split up like we did the data input file
            String line2 = readFile2.nextLine();
            String[] contents = line2.split("\\|");
            String source = contents[0];
            String destination = contents[1];
            char choice = contents[2].charAt(0);


            //test if source and destination exist in the cities arraylist
            //if they do not, we will output to file and move to next line

            int sourceIndex = 0;
            boolean sourceExists = false;
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(source)) {
                    sourceIndex = i;
                    sourceExists = true;
                }
            }


            int destinationIndex = 0;
            boolean destinationExists = false;
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).name.equals(destination)) {
                    destinationIndex = i;
                    destinationExists = true;
                }
            }

            //if cities do not exist
            if (!sourceExists)
                outFile.write("NO FLIGHT AVAILABLE FOR THE REQUEST\n");
            else if (!destinationExists)
                outFile.write("NO FLIGHT AVAILABLE FOR THE REQUEST\n");

            else {
                //calling dijkstras() with values from query file
                dijkstras(cities.get(sourceIndex), cities, choice);

                //set current city to final destination
                City current = cities.get(destinationIndex);

                //start with distance to current city
                int dist = current.distance;

                //if infinity, no path ws found. output to file and move to next line
                if (dist == Integer.MAX_VALUE)
                    outFile.write("NO FLIGHT AVAILABLE FOR THE REQUEST");

                else {
                    //stack cities because we traverse backwards and want to print them forwards
                    Stack<City> finalPath = new Stack<>();
                    int i = 0; //counter
                    //while we are not at source city
                    while (current.distance > 0) {
                        finalPath.push(current);
                        current = current.last;
                        i++;
                    }

                    //push source city
                    finalPath.push(current);

                    //print flight legs
                    outFile.write(i + "|");

                    while (!finalPath.isEmpty()) {
                        outFile.write(finalPath.pop().name + "|");
                    }
                    //if time was chosen, we print the two distances in a flipped order because of how we used them
                    //in dijkstras()
                    if (choice == 'T')
                        outFile.write(cities.get(destinationIndex).otherDistance + "|" + cities.get(destinationIndex).distance);
                    else
                        outFile.write(cities.get(destinationIndex).distance + "|" + cities.get(destinationIndex).otherDistance);
                    outFile.write("\n");
                }
            }

        }
        //close files
        outFile.close();
        readFile.close();

    }

    private static void dijkstras(City source, ArrayList<City> cities, char choice) {
        PriorityQueue<City> paths = new PriorityQueue<>();
        ArrayList<City> citiesAdded = new ArrayList<>();

        //set source distance to zero
        source.distance = 0;
        //add source to priority queue and citiesAdded
        paths.add(source);
        citiesAdded.add(source);
        while (citiesAdded.size() != cities.size()) {
            while (!paths.isEmpty()) {
                //dequeue min element
                City u = paths.poll();

                if (u != null) {
                    //for all neighbors
                    for (int i = 0; i < u.neighbors.size(); i++) {
                        //if user chose time efficiency
                        if (choice == 'T') {
                            //update distance if less than current distance
                            if (u.distance + u.time.get(i) < u.neighbors.get(i).distance) {
                                u.neighbors.get(i).distance = u.distance + u.time.get(i);
                                //edit the cost even though it does not matter
                                u.neighbors.get(i).otherDistance = u.otherDistance + u.cost.get(i);

                                u.neighbors.get(i).last = u;
                                paths.add(u.neighbors.get(i));
                                citiesAdded.add(u.neighbors.get(i));
                            }
                        } else //if user chose cost efficiency
                        {
                            //update distance if less than current distance
                            if (u.distance + u.cost.get(i) < u.neighbors.get(i).distance) {
                                u.neighbors.get(i).distance = u.distance + u.cost.get(i);

                                //edit the time even though it does not matter
                                u.neighbors.get(i).otherDistance = u.otherDistance + u.time.get(i);

                                u.neighbors.get(i).last = u;
                                paths.add(u.neighbors.get(i));
                                citiesAdded.add(u.neighbors.get(i));
                            }
                        }
                    }
                }
            }
        }
    }
}