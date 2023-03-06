import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

/**
 * Tag Cloud Generator
 *
 * @author William Branch
 */
public final class TagCloudGeneratorStandardJavaLibrary {

    /**
     * Default constructor--private to prevent instantiation.
     */
    private TagCloudGeneratorStandardJavaLibrary() {
    }

    public static class IntegerGT
            implements Comparator<Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {
            int comp = 0;
            if (o1.getKey().equals(o2.getKey())) {
                comp = o2.getValue().compareTo(o1.getValue());
            } else if (o1.getValue().equals(o2.getValue())) {
                comp = o1.getKey().compareTo(o2.getKey());
            } else {
                comp = o2.getValue().compareTo(o1.getValue());
            }

            return comp;
        }
    }

    public static class StringLT implements Comparator<Entry<String, Integer>> {
        @Override
        public int compare(Entry<String, Integer> o1,
                Entry<String, Integer> o2) {
            int comp = 0;
            if (o1.getValue().equals(o2.getValue())) {
                comp = o1.getKey().compareTo(o2.getKey());
            } else if (o1.getKey().equals(o2.getKey())) {
                comp = o2.getValue().compareTo(o1.getValue());
            } else {
                comp = o1.getKey().compareTo(o2.getKey());
            }

            return comp;
        }
    }

    /**
     * Separators between words.
     */
    public static final String SEPARATORS = " ,-.!?[]';:/()";

    /**
     * The maximum font size.
     */
    public static final int FONT_MAX = 48;

    /**
     * The minimum font size.
     */
    public static final int FONT_MIN = 11;

    private static String nextWordOrSeparator(String line, int pos) {
        String s = "";
        int endPos = pos;
        boolean flag = false;
        boolean isSep = false;
        char c = line.charAt(endPos);
        
      //Check if the string you are creating will be a word or separator string.
        if (SEPARATORS.indexOf(c) > -1) {
            isSep = true;
        }
        /*
         * Run loop until it reaches a separator or the end of the line, determining if 
         * each index is the same "type" (word/separator) as the last.
         */
        while (!flag && endPos < line.length()) {
            if (isSep) {
                if (SEPARATORS.indexOf(line.charAt(endPos)) == -1) {
                    flag = true;
                }
                endPos++;
            } else {
                if (SEPARATORS.indexOf(line.charAt(endPos)) > -1) {
                    flag = true;
                }
                endPos++;
            }
        }
        if (flag) {
        	//Create substring until word or separator string ends.
            s = line.substring(pos, endPos - 1);
        } else {
        	//Create substring from starting position to end of line
            s = line.substring(pos);
        }
        return s;
    }

    public static void countWords(Map<String, Integer> allWords,
            BufferedReader in) {

        int pos = 0;

        try {
            String line = in.readLine();
            while (line != null) {
            	/*
                 * Using the nextWordOrSeparator method, add each one of the words to the entry set.
                 * If the entry set already contains the word, increment it's value.
                 */
                while (pos < line.length()) {
                    String next = nextWordOrSeparator(line, pos);
                    if (SEPARATORS.indexOf(next.charAt(0)) == -1) {
                        next = next.toLowerCase();
                        if (allWords.containsKey(next)) {
                            Set<Entry<String, Integer>> s = allWords.entrySet();
                            Iterator<Entry<String, Integer>> t = s.iterator();
                            while (t.hasNext()) {
                                Entry<String, Integer> entry = t.next();
                                if (entry.getKey().equals(next)) {
                                    entry.setValue(entry.getValue() + 1);
                                }
                            }

                        } else {
                            allWords.put(next, 1);
                        }
                    }
                    pos += next.length();
                }
                line = in.readLine();
                pos = 0;
            }
        } catch (IOException e) {
            System.err.println("Could not read lines from input file");
        }


    }

    public static void printHtmlHeader(String inFile, PrintWriter out,
            Integer num) {

    	/*
    	 * Format header for HTML page
    	 */
        out.println("<html>");
        out.println("<head>");
        out.println("<title>");
        out.println("Top " + num + "words in " + inFile);
        out.println("</title>");
        out.println("<link href=\"http://web.cse.ohio-state.edu/software/2231/"
                + "web-sw2/assignments/projects/tag-cloud-generator/data/"
                + "tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");

        out.println("<body><h2>Top " + num + " words in " + inFile + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");
    }

    public static void printHtmlBody(PrintWriter out,
            PriorityQueue<Entry<String, Integer>> sortedWords, int min,
            int max) {

    	/*
    	 * Add all words to the page while formating the body of HTML page
    	 */
        while (sortedWords.size() > 0) {
            Entry<String, Integer> entry = sortedWords.remove();
            int size = ((FONT_MAX - FONT_MIN) * (entry.getValue() - min))
                    / (max - min) + FONT_MIN;
            out.println("<span style=\"cursor:default\" class=\"f" + size
                    + "\" title=\"count: " + entry.getValue() + "\">"
                    + entry.getKey());
            out.println("</span>");
        }
    }

    public static void printHtmlFooter(PrintWriter out) {

    	//Output footer for HTML page
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        //Take input file name
        System.out.println("Enter input file: ");
        String inFile = in.nextLine();

        //Take output file name
        System.out.println("Enter output file: ");
        String outFile = in.nextLine();

        int num = 0;
        do {
        	//Take number of words and report possible error of non-integer input
            System.out.println("Enter the number of words in the tag cloud: ");
            String s = in.nextLine();
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
            	//Catch errors
                System.err.println("Invalid integer");
            }
            num = Integer.parseInt(s);
        } while (num < 0);

        BufferedReader inputFile = null;
        try {
            inputFile = new BufferedReader(new FileReader(inFile));
        } catch (IOException e) {
        	//Catch errors
            System.err.print("The input file could not be openend");
        }

        if (inputFile != null) {

            PrintWriter outputFile = null;
            try {
                outputFile = new PrintWriter(
                        new BufferedWriter(new FileWriter(outFile)));
                
                //Catch output error 
            } catch (IOException e) {
                System.err.println("The output file could not be opened");
            }

            if (outputFile != null) {

            	/*
                 * Create a map and using the countWords method, add each word from the file to the map.
                 * This will also keep track of the number of time each word appears as the values in the map.
                 */
                Map<String, Integer> allWords = new HashMap<String, Integer>();
                countWords(allWords, inputFile);

                Comparator<Entry<String, Integer>> intComp = new IntegerGT();
                Comparator<Entry<String, Integer>> stringComp = new StringLT();

                PriorityQueue<Entry<String, Integer>> intPQ = new PriorityQueue<>(
                        allWords.size(), intComp);
                PriorityQueue<Entry<String, Integer>> wordPQ = new PriorityQueue<>(
                        allWords.size(), stringComp);

                Set<Entry<String, Integer>> s = allWords.entrySet();

                /*
                 * Iterate through entry set to order the words by value into Priority Queue.
                 * This will be used to get the max value
                 */
                Iterator<Entry<String, Integer>> it = s.iterator();
                for (int i = 0; i < s.size(); i++) {
                    Entry<String, Integer> entry = it.next();
                    intPQ.add(entry);
                }

                //Remove first value and since it has been previously ordered it is the max.
                Entry<String, Integer> entry = intPQ.remove();
                int max = entry.getValue();

                wordPQ.add(entry);

                for (int i = 0; i < num - 1; i++) {
                    entry = intPQ.remove();
                    wordPQ.add(entry);
                }

                //Last value will be the min, these are used to determine font size ratio
                int min = entry.getValue();

                /*
                 * Print HTML page
                 */
                printHtmlHeader(inFile, outputFile, num);
                printHtmlBody(outputFile, wordPQ, min, max);
                printHtmlFooter(outputFile);

                outputFile.close();

            }

            //Close input file and catch error
            try {
                inputFile.close();
            } catch (IOException e) {
                System.err.println("Could not correctly close input file");
            }
        }
        in.close();
    }

}
