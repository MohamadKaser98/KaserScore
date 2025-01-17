package de.di.similarity_measures;
import lombok.AllArgsConstructor;


import java.util.*;
@AllArgsConstructor
public class KaserScore implements SimilarityMeasure{

    // Calculates the KaserScore similarity of the two input strings.
    @Override
    public double calculate(String string1, String string2) {
        if (string1 == null || string2 == null) {
            throw new IllegalArgumentException("Input strings must not be null");
        }
        //Step 1: Construct Cartesian Product Matrices
        List<List<String>> matrix1 = constructCartesianProductMatrix(string1);
        List<List<String>> matrix2 = constructCartesianProductMatrix(string2);

//        System.out.println("Matrix for " + string1 + ":");
//        printMatrix(matrix1);
//        System.out.println("Matrix for " + string2 + ":");
//        printMatrix(matrix2);

        // Step 2: Update Matrices by Removing Different Elements
        List<List<String>> updatedMatrix1 = updateMatrix(string1, string2);
        List<List<String>> updatedMatrix2 = updateMatrix(string2, string1);

//        System.out.println("Updated Matrix for " + string1 + ":");
//        printMatrix(updatedMatrix1);
//        System.out.println("Updated Matrix for " + string2 + ":");
//        printMatrix(updatedMatrix2);


        // Step 3: Calculate Positional Distances between the letters and save it in the Matrix 3 and Matrix 4
        List<List<Double>> distanceMatrix3 = calculatePositionalDistances(updatedMatrix1, matrix2);
        List<List<Double>> distanceMatrix4 = calculatePositionalDistances(updatedMatrix2, matrix1);

//        System.out.println("Positional Distance Matrix:");
//        printDistanceMatrix(distanceMatrix3);
//        System.out.println("Positional Distance Matrix:");
//        printDistanceMatrix(distanceMatrix4);

        // This is the updated string that we need its size for the final equation
        StringBuilder s3 = updateString(string1, string2);
        StringBuilder s4 = updateString(string2, string1);
//        System.out.println(s4);

        // Step 4: Sum of the first row distance values that we saved in Matrix 3 and 4
        double sumOfDistancesValues = calculateTotalDistance(distanceMatrix3, s3.toString(), s4.toString()) + calculateTotalDistance(distanceMatrix4, s3.toString(), s4.toString());
        // The average of the sums of the first row values in M3 and M4. -could be changed later-
        double distAverage = sumOfDistancesValues / 2;
//        System.out.println(distAverage);


        // Step 5: Final Similarity Equation Calculation
        double sim = similarityEquation(distAverage, s3.toString(), s4.toString());
        if (Double.isNaN(sim)){
            sim = 0.0;
        }
        System.out.println("Similarity score of String: '" +string1+"' and String: '"+ string2 + "' is: "+ sim + "\n");


        return sim;
    }


    // Calculates the KaserScore similarity of the two input string lists.
    @Override
    public double calculate(String[] strings1, String[] strings2) {
        // To Do: Implement method to handle list input, possibly converting to single strings
        return 0.0; // Placeholder
    }

    // Constructs the Cartesian product matrix for a given string
    private List<List<String>> constructCartesianProductMatrix(String input) {
        int length = input.length(); // Ssize
        List<Character> uniqueChars = new ArrayList<>(); // To store unique characters in order

        for (char c : input.toCharArray()) {
            uniqueChars.add(c);
        }

        List<List<String>> matrix = new ArrayList<>();

        for (int i = 1; i <= length; i++) {
            List<String> row = new ArrayList<>();
            for (char c : uniqueChars) {
                row.add("(" + i + ", " + c + ")");
            }
            matrix.add(row);
        }

        return matrix;
    }



    // Updates a matrix by retaining only columns found in the reference matrix
    private List<List<String>> updateMatrix(String source, String reference) {
        List<List<String>> updatedMatrix = new ArrayList<>();
        StringBuilder result = new StringBuilder();

        result = updateString(source,reference);
        updatedMatrix = constructCartesianProductMatrix(result.toString());

        return updatedMatrix;

    }

    private StringBuilder updateString (String source, String reference){
        StringBuilder result = new StringBuilder();
        // Iterate over each character in the source string
        for (char c : source.toCharArray()) {
            // Add the character to the result if it exists in the reference string
            if (reference.indexOf(c) != -1) {
                result.append(c);
            }
        }

        return result;
    }



    // Calculates row-by-row positional distances
    private List<List<Double>> calculatePositionalDistances(List<List<String>> originalMatrix, List<List<String>> updatedMatrix) {
        List<List<Double>> distanceMatrix = new ArrayList<>();
        for (int i = 0; i < originalMatrix.size(); i++) {
            List<String> originalRow = originalMatrix.get(i);
            List<Double> distanceRow = new ArrayList<>();
            int colNr= 0;
            for (String originalElement : originalRow) {
                List<List<Integer>> listOfMatchedElement = findAndCalculateDistance(originalElement, updatedMatrix);
                double SumOfMatchedElementInRow = 0.0;
                for (List<Integer> ele: listOfMatchedElement) {
                    double originalScaleFactor = (double) (i + 1) / (colNr + 1);
                    double updatedScaleFactor = (double) ele.get(0) / ele.get(1);
                    SumOfMatchedElementInRow += (originalScaleFactor - updatedScaleFactor);
                }
                double distance = Math.sqrt(Math.pow(SumOfMatchedElementInRow, 2));
                distanceRow.add(distance);
                colNr += 1;
            }
            distanceMatrix.add(distanceRow);
        }

        return distanceMatrix;
    }

    // Finds the matching element in the updated matrix and calculates the positional distance
    private List<List<Integer>> findAndCalculateDistance(String originalElement, List<List<String>> updatedMatrix) {
        List<List<Integer>> listOfMatchedElements = new ArrayList<>();

        for (int i = 0; i < updatedMatrix.size(); i++) {
            List<String> updatedRow = updatedMatrix.get(i);
            for (int j = 0; j < updatedRow.size(); j++) {
                if (originalElement.equals(updatedRow.get(j))) {
                    listOfMatchedElements.add(Arrays.asList(i + 1, j + 1)); // Add row and column as a pair
                }
            }
        }
        //System.out.println(listOfMatchedElements);
        return listOfMatchedElements;

    }



    // Calculates the total distance from a distance matrix (Matrix 3 and 4)
    private double calculateTotalDistance(List<List<Double>> distanceMatrix, String s3, String s4) {
        double totalDistance = 0.0;

        //if(Math.min(s3.length(), s4.length()) == 1){

        if (distanceMatrix == null || distanceMatrix.isEmpty()) {
            //System.out.println("Distance matrix is empty or null.");
            return 0.0; // Handle empty or null matrix
        }
        List<Double> firstRow = distanceMatrix.get(0);
        for (double value : firstRow) {
            totalDistance += value;
        }
        if(s3.length() == 1 && s4.length() == 1){
            return totalDistance;
        }else {
            totalDistance = totalDistance * 2;
        }


//        }else {
//            for (List<Double> row : distanceMatrix) {
//                for (double value : row) {
//                    totalDistance += value;
//                }
//            }
//        }

        return totalDistance;
    }





    private double similarityEquation( double distAverage, String s3, String s4 ){
        double sim;
        int max = Math.max(s3.length(), s4.length());
//        System.out.println(max);
//        if (distAverage == 0){
//            return 0.0;
//        }
        if(distAverage > max){
            distAverage = distAverage / max;
        }
        if(s3.length() == s4.length()){
            sim = 1 - (distAverage / (Math.pow(Math.min(s3.length(), s4.length()), 2)));
        }else {
            sim = 1 - (distAverage / max);
//            System.out.println(sim);
        }
        //sim = Math.round(sim * 100.0) / 100.0;
        return  sim;

    }







    // Utility method to print the matrix
    private void printMatrix(List<List<String>> matrix) {
        for (List<String> row : matrix) {
            System.out.println(row);
        }
    }

    // Utility method to print the distance matrix
    private void printDistanceMatrix(List<List<Double>> matrix) {
        for (List<Double> row : matrix) {
            System.out.println(row);
        }
    }

}

