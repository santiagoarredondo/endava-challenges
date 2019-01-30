// $Id: Huffman.java 388 2013-05-03 18:38:48Z ylari $


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Prefix codes and Huffman tree. Tree depends on source data.
 * References: http://rosettacode.org/wiki/Huffman_coding
 * @author <a href="mailto:ulari.ainjarv@itcollege.ee?subject=I231: Huffman.java ...">Ülari Ainjärv</a>
 * @version $Revision: 388 $
 */
public class Huffman {

    /** We will assume that all our bytes will have code less than 256, for simplicity. */
    private int[] frequencies;

    /** Encoding table. */
    private Map<Byte, Leaf> table;

    /** Encoded bits. */
    private int length;

    /**
     * Constructor to build the Huffman code for a given bytearray.
     * @param original source data.
     */
    Huffman(byte[] original) {
        frequencies = new int[256];
        table = new HashMap<Byte, Leaf>();
        length = 0;
        init(original);
    }

    public Huffman() {
		// TODO Auto-generated constructor stub
	}

	/**
     * Length of encoded data in bits.
     * @return number of bits.
     */
    public int bitLength() {
        return length;
    }

    /**
     * Encoding the byte array using this prefixcode.
     * @param origData original data.
     * @return encoded data.
     */
    public byte[] encode(byte[] origData) {
        StringBuffer tmp = new StringBuffer();
        for (byte b : origData)
            tmp.append(table.get(b).code);
        length = tmp.length();
        List<Byte> bytes = new ArrayList<Byte>();
        while (tmp.length() > 0) {
            while (tmp.length() < 8)
                tmp.append('0');
            String str = tmp.substring(0, 8);
            bytes.add((byte) Integer.parseInt(str, 2));
            tmp.delete(0, 8);
        }
        byte[] ret = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            ret[i] = bytes.get(i);
        return ret;
    }

    /**
     * Decoding the byte array using this prefixcode.
     * @param encodedData encoded data.
     * @return decoded data (hopefully identical to original).
     */
    public byte[] decode(byte[] encodedData) {
        StringBuffer tmp = new StringBuffer();
        for (int i = 0; i < encodedData.length; i++)
            tmp.append(String.format("%8s", Integer.toBinaryString(encodedData[i] & 0xFF)).replace(' ', '0'));
        String str = tmp.substring(0, length);
        List<Byte> bytes = new ArrayList<Byte>();
        String code = "";
        while (str.length() > 0) {
            code += str.substring(0, 1);
            str = str.substring(1);
            Iterator<Leaf> list = table.values().iterator();
            while (list.hasNext()) {
                Leaf leaf = list.next();
                if (leaf.code.equals(code)) {
                    bytes.add(leaf.value);
                    code = "";
                    break;
                }
            }
        }
        byte[] ret = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            ret[i] = bytes.get(i);
        return ret;
    }


    /*

    public static void main(String[] params) {
        String tekst = "ABCDEFAAABBC";
        byte[] orig = tekst.getBytes();
        Huffman huf = new Huffman(orig);
        byte[] kood = huf.encode(orig);
        byte[] orig2 = huf.decode(kood);
        System.out.println(Arrays.equals(orig, orig2)); // must be equal: orig, orig2
        System.out.println("Length of encoded data in bits: " + huf.bitLength());
    }
    */

    private void init(byte[] data) {
        for (byte b : data)
            frequencies[b]++;
        PriorityQueue<Tree> trees = new PriorityQueue<Tree>();
        for (int i = 0; i < frequencies.length; i++)
            if (frequencies[i] > 0)
                trees.offer(new Leaf(frequencies[i], (byte) i));
        assert trees.size() > 0;
        while (trees.size() > 1)
            trees.offer(new Node(trees.poll(), trees.poll()));
        Tree tree = trees.poll();
        code(tree, new StringBuffer());
    }

    private void code(Tree tree, StringBuffer prefix) {
        assert tree != null;
        if (tree instanceof Leaf) {
            Leaf leaf = (Leaf) tree;
            leaf.code = (prefix.length() > 0) ? prefix.toString() : "0";
            table.put(leaf.value, leaf);
        } else if (tree instanceof Node) {
            Node node = (Node) tree;
            prefix.append('0');
            code(node.left, prefix);
            prefix.deleteCharAt(prefix.length() - 1);
            prefix.append('1');
            code(node.right, prefix);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /** Represents Huffman tree. */
    abstract class Tree implements Comparable<Tree> {

        /** The frequency of this tree. */
        public final int frequency;

        public Tree(int frequency) {
            this.frequency = frequency;
        }

        /**
         * This is an overriding method.
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Tree o) {
            return frequency - o.frequency;
        }

    }

    /** Represents Huffman leaf. */
    class Leaf extends Tree {

        /** The byte this leaf represents. */
        public final byte value;

        public String code;

        public Leaf(int frequency, byte value) {
            super(frequency);
            this.value = value;
        }

    }

    /** Represents Huffman node. */
    class Node extends Tree {

        public final Tree left, right; // subtrees

        public Node(Tree left, Tree right) {
            super(left.frequency + right.frequency);
            this.left = left;
            this.right = right;
        }

    }

    private static final String INPUT_FILE = System.getProperty("user.dir")+"\\"+"dijk.txt";
    private static final String ENCODED_FILE = System.getProperty("user.dir")+"\\"+"encodedDijk.txt";
    private static final String DECODED_FILE = System.getProperty("user.dir")+"\\"+"decodedDijk.txt";
    private static final String OUTPUT_FILE = System.getProperty("user.dir")+"\\"+"outputDijk.txt";
    
    private static final String INPUT_FILE2 = System.getProperty("user.dir")+"\\"+"shortest.txt";
    private static final String ENCODED_FILE2 = System.getProperty("user.dir")+"\\"+"encodedShort.txt";
    private static final String DECODED_FILE2 = System.getProperty("user.dir")+"\\"+"decodedShort.txt";
    private static final String OUTPUT_FILE2 = System.getProperty("user.dir")+"\\"+"outputShort.txt";

    private static String readFile(String filePath){
        BufferedReader objReader = null;
        String text = "";
        try {
            String strCurrentLine;
            objReader = new BufferedReader(new FileReader(filePath));
            while ((strCurrentLine = objReader.readLine()) != null) {
                text+=strCurrentLine+"\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return text;
    }

    /*
    private static void writeFile(String filePath, String fileContent){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(fileContent+"");
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    */

    public static  void writeFile(String filePath, ArrayList<String> fileContent){
        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            for(String str: fileContent) {            
	            byte[] strToBytes = str.getBytes();
	            outputStream.write(strToBytes);	
	            //outputStream.
            }
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getNormalNotation(byte[] array){
        String text="";
        char currentChar;
        for (int i=0; i<array.length; i++){
            currentChar = (char)array[i];
            text+=currentChar;
        }
        //System.out.println(text);
        return text;
    }

    public static String getBinaryNotation(byte[] array){
        String text="";
        char currentChar;
        for (int i=0; i<array.length; i++){
            currentChar = (char)array[i];
            text+=Integer.toBinaryString(currentChar)+"";
        }
        //System.out.println(text);
        return text;
    }

    private static int[] toIntegerArray(byte[] array){
        int[] rta = new int[array.length];
        for (int i=0; i<array.length; i++){
            rta[i]=(int) array[i];
        }
        return rta;
    }

    public void loadData(){
        String fileText = readFile(DECODED_FILE);
        byte[] orig = fileText.getBytes();
        Huffman huf = new Huffman(orig);
        byte[] orig2 = huf.decode(readFile(ENCODED_FILE).getBytes());
        getNormalNotation(orig2);
    }

    public void saveData(){
        //writeFile(INPUT_FILE,readFile(INPUT_FILE));
        //writeFile(DECODED_FILE,readFile(DECODED_FILE));
    }

    public static void main(String[] args) {
    	ArrayList<String> dijks  = new ArrayList<String>();
    	ArrayList<String> dijksDec  = new ArrayList<String>();
    	ArrayList<String> shortest  = new ArrayList<String>();
    	ArrayList<String> shortestDec  = new ArrayList<String>();
    	
    	
        String fileText = readFile(INPUT_FILE);
        byte[] orig = fileText.getBytes();
        Huffman huf = new Huffman(orig);
        byte[] kood = huf.encode(orig);
        System.out.println("---- ENCODED ----");
        System.out.println("writing...");
        dijks.add(getNormalNotation(kood));
        writeFile(ENCODED_FILE, dijks);
        System.out.println("reading...");
        String encoded = readFile(ENCODED_FILE);
        System.out.println("end");
        byte[] orig2 = huf.decode(kood);
        System.out.println("---- DECODED ----");
        System.out.println("writing...");
        dijksDec.add(getNormalNotation(orig2));
        writeFile(DECODED_FILE, dijksDec);
        System.out.println("reading...");
        String decoded = readFile(DECODED_FILE);
        System.out.println("Correctly codified: "+Arrays.equals(orig, orig2)); // must be equal: orig, orig2
        System.out.println("Length of encoded file: " + encoded.length());
        System.out.println("Lenght of decoded file: " + decoded.length());
        
        
        String fileTextShort = readFile(INPUT_FILE2);
        byte[] origShort = fileText.getBytes();
        Huffman hufShort = new Huffman(origShort);
        byte[] koodShort = huf.encode(origShort);
        System.out.println("---- ENCODED ----");
        System.out.println("writing...");
        shortest.add(getNormalNotation(koodShort));
        writeFile(ENCODED_FILE2, shortest);
        System.out.println("reading...");
        String encodedShort = readFile(ENCODED_FILE2);
        System.out.println("end");
        byte[] orig2Short = huf.decode(koodShort);
        System.out.println("---- DECODED ----");
        System.out.println("writing...");
        shortestDec.add(getNormalNotation(orig2Short));
        writeFile(DECODED_FILE2, shortestDec);
        System.out.println("reading...");
        String decodedShort = readFile(DECODED_FILE2);
        System.out.println("Correctly codified: "+Arrays.equals(origShort, orig2Short)); // must be equal: orig, orig2
        System.out.println("Length of encoded file: " + encodedShort.length());
        System.out.println("Lenght of decoded file: " + decodedShort.length());
    }
}