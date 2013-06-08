/*--------------------------------------------
 * Written Candy Jimenez
 * -------------------------------------------
 * INSTUCTIONS FOR COMPILING
 * 
 * Open the command line prompt.
 * Find this java file in the directory
 * type "javac DFSA.java" to compile
 * type "java DFSA" to run
 * ---------------------------------------------
*/
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DFSA {
    File inFile = new File("input.txt");
    File inFile2 = new File("input2.txt");
    Scanner inputFile;
    Scanner inputFile2;
    FileWriter outFile;
    BufferedWriter writer;
    int [] switchs;
    char [] symbols;
    int [] next;
    int sw_index;
    int sy_index;
    int n_index;

    public DFSA() throws IOException {
        try {
            this.inputFile = new Scanner(inFile);
            this.inputFile2 = new Scanner(inFile2);
            outFile = new FileWriter("output.txt");
            writer = new BufferedWriter(outFile);
            
        }
        catch(FileNotFoundException e) { 
            System.out.println("FILE NOT FOUND!");
            System.exit(0);
        }
        switchs = new int [54];
        symbols = new char [1024];
        next = new int [1024];
        sw_index = 0;
        sy_index = 0;
        n_index = 0;
    }
//runs the program
    public static void main(String [] args) throws IOException{
        DFSA d = new DFSA();
        d.read();
        d.readNext();
        d.print();
    }
//reads inputFile one
    public void read(){
//initializes switch array to -1
        for (int i = 0; i < switchs.length; i++) {
            switchs [i] = -1;
            
        }
//initializes next array to -1
        for (int i = 0; i < next.length; i++) {
            next[i] = -1;
        }
        int lineIndex;
        String [] tempLine;
//traverses through inputFile one
        while(inputFile.hasNext()){
            lineIndex = 0;
//converts each line into an array of strings
            tempLine = inputFile.nextLine().split(" ");
            for (int k = 0; k < tempLine.length; k++) {
//maps the switch array according to the first character of each word
                char firstCharOfWord = tempLine [lineIndex].charAt(0);
                int index = getSwitchIndex(firstCharOfWord);
//the first character was not found
                if(index == -1)
                    continue;
//charter found and hasnt been mapped in switch array
                if(switchs[index] == -1){
                    switchs[index] = sy_index;
                    for (int i = 1; i < tempLine[lineIndex].length(); i++) {
                        symbols[sy_index] = tempLine[lineIndex].charAt(i);
                        sy_index++;
                    }
                    symbols[sy_index] = '*';
                    sy_index++;
                }
//character found but already mapped goes yo next array
                else{
                    boolean b = false;
                    index = switchs[index];
                    while(!b){
                        if(next[index] == -1){
                            next[index] = sy_index;
//inserts word into symbol array
                            for (int i = 1; i < tempLine[lineIndex].length(); i++) {
                                symbols[sy_index] = tempLine[lineIndex].charAt(i);
                                sy_index++;
                            }
                            symbols[sy_index] = '*';
                            sy_index++;
                            b = true;
                        }
                        else{
                            index = next[index];
                            
                        }
                    }
                }
                lineIndex++;
            }
        }
    }
//read inputFile two
    public void readNext() throws IOException{
        int lineIndex;
        boolean found = false;
//traverses inoutFile two
        while(inputFile2.hasNext()){
            lineIndex = 0;
//divides each line according to split
            String split = "[   .,\'\"<>\t\n*0123456789/;:+=!?#&%](){}";
            String [] tempLine = this.realSplit(inputFile2.nextLine(), split);
//word at lineIndex is empty
            if(tempLine == null || tempLine.length == 0 || 
                                tempLine[lineIndex].equals("")) {
            }
//maps each transition in inputFile two
            else{
                for (int k = 0; k < tempLine.length; k++) {
                    char firstCharOfWord = tempLine [lineIndex].charAt(0);
                    int index = getSwitchIndex(firstCharOfWord);
                    if(index == -1) {
                        continue;
                    }
                    else if(switchs[index] == -1){
                        switchs[index] = sy_index;
                        for (int i = 1; i < tempLine[lineIndex].length(); i++) {
                            symbols[sy_index] = tempLine[lineIndex].charAt(i);
                            sy_index++;
                        }
                        symbols[sy_index] = '?';
                        sy_index++;
                        writer.write(tempLine[lineIndex] + "? ");
                    }
                    else{
                        index = switchs[index];
//searching through the symbols array for the word
                        while(!found) {
                            if(this.check(tempLine[lineIndex],index)) {
                                found = true;
                                if(symbols[index + tempLine[lineIndex].length() - 1] == '*') {
                                    writer.write(tempLine[lineIndex] + "* ");
                                }
                                else if(symbols[index + tempLine[lineIndex].length() - 1] == '@') {
                                    writer.write(tempLine[lineIndex] + "@ ");
                                }
                                else if(symbols[index + tempLine[lineIndex].length() - 1] == '?') {
                                    symbols[index + tempLine[lineIndex].length() - 1] = '@';
                                    writer.write(tempLine[lineIndex] + "@ ");
                                }
                            }
                            else if(next[index] == -1) {
                                found = true;
                                next[index] = sy_index;

                                for (int i = 1; i < tempLine[lineIndex].length(); i++) {
                                    symbols[sy_index] = tempLine[lineIndex].charAt(i);
                                    sy_index++;
                                }
                                symbols[sy_index] = '?';
                                sy_index++;
                                writer.write(tempLine[lineIndex] + "? ");
                            }
//moves onto the next iteration through next array
                            else {
                                    index = next[index];
                            }
                        }
                    }
                    lineIndex++;
                    found = false;
                }
                writer.newLine();
            }
        }
        writer.close();
        
    }
//gets the index of the character in the switch array
    private int getSwitchIndex(char c){
  String switch_char = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_$";
        for (int i = 0; i < switch_char.length(); i++) {
            if(switch_char.charAt(i) == c){
                return i;
            }
        }
        return -1;
    }
//checks if the word has been mapped to a given index
    private boolean check(String s, int index){
        for (int i = 1; i < s.length(); i++) {
            if(s.charAt(i) != symbols[index])
                return false;
            index++;
        }
        return true;
    }
//prints 
    public void print(){
    String switch_char = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_$";
        System.out.println("Switch: ");
        for (int i = 0; i < 19; i++) {
            System.out.printf("%5s", switch_char.charAt(i));
        }
        System.out.println();
        for (int i = 0; i < 19; i++) {
            System.out.printf("%5s", switchs[i]);
        }
        System.out.println();
        for (int i = 19; i < 38; i++) {
            System.out.printf("%5s", switch_char.charAt(i));
        }
        System.out.println();
        for (int i = 19; i < 38; i++) {
            System.out.printf("%5s", switchs[i]);
        }
        System.out.println();
        for (int i = 38; i < 54; i++) {
            System.out.printf("%5s", switch_char.charAt(i));
        }
        System.out.println();
        for (int i = 38; i < 54; i++) {
            System.out.printf("%5s", switchs[i]);
        }
        
        System.out.println();
        System.out.println("Symbol: ");
        
        boolean syNum = true;
        boolean syChar = false;
        boolean nextNum = false;
        boolean exit = false;
        for(int i = 1; i <= symbols.length && !exit; i++) {
            if((symbols[i-1] + " ").equals(" ")){
                exit = true;
                break;
            }
            else if(syNum)
                System.out.printf("%5s", i-1);
            else if(syChar)
                System.out.printf("%5s", symbols[i-1]);
            else if(nextNum)
                System.out.printf("%5s", next[i-1]);
            if(i%19 == 0) {
                System.out.println();
                if(syNum) {
                    syNum = false;
                    syChar = true;
                    i -= 19;
                }
                else if(syChar) {
                    syChar = false;
                    nextNum = true;
                    i -= 19;
                    System.out.println("Next: ");
                }
                else if(nextNum) {
                    nextNum = false;
                    syNum = true;
                    System.out.println("Symbols: ");
                }
            }
        }
    }
//a split method that can handle multiple instances of the same dilimeter   
    private String [] realSplit(String line, String delim){
        String [] splitter = new String [line.length()];
        int arrayCount = 0;
        int index1 = 0, index2 = 0;
        for (int i = 0; i < line.length(); i++) {
            if(this.elementOf(delim, line.charAt(i)) && index1 == index2) {
                index1++;
                index2++;
            }
            else if(!this.elementOf(delim, line.charAt(i))) {
                index2++;
            }
            else if(this.elementOf(delim, line.charAt(i)) && index1 != index2) {
                splitter[arrayCount++] = line.substring(index1, index2);
                index2++;
                index1 = index2;
            }
        }
        String [] realSplit = new String [arrayCount];
        for (int i = 0; i < arrayCount; i++) {
            realSplit[i] = splitter[i];
            
        }
        return realSplit;
    }
//checks if the string line contains char c     
    private boolean elementOf(String line, char c) {
        for (int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == c)
                return true;
        }
        return false;
    }
    
    
            
}
    
