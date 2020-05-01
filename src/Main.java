import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Stack;

public class Main {

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static int occurances(String str, String o) {
        return str.length() - str.replace("(", "").length();
    }

    // Creates a look up table for the characters in the compressed file
    public static void readTree(String tree, String path, HashMap<String, Character> data) {

        tree = tree.trim();

        if (isNumeric(tree)) {
            data.put(path, (char) Integer.parseInt(tree));
            return;
        }

        if (occurances(tree, "(") == 1 && occurances(tree, ")") == 1) {
            int space = tree.indexOf(" ");
            data.put(path + "0", (char) Integer.parseInt(tree.substring(1, space)));
            data.put(path + "1", (char) Integer.parseInt(tree.substring(space+1, tree.length()-1)));
            return;
        }

        char[] treeArray = tree.toCharArray();
        int first = 0;
        int open = 0;
        int close = 0;
        int index = 1;
        while (open != close || open == 0) {
            if (first == 0 && treeArray[index] == '(') first = index;
            if (treeArray[index] == '(') open++;
            if (treeArray[index] == ')') close++;
            index++;
        }

        if (first == 1) {
            readTree(tree.substring(first, index), path + "0", data);
            readTree(tree.substring(index, tree.length() - 1), path + "1", data);
        } else {
            readTree(tree.substring(1, first), path +  "0", data);
            readTree(tree.substring(first, index), path + "1", data);
        }


    }

    public static void main(String[] args) {
        String path = "src/compressed.mzip";
        File file = new File(path);

        try {
            FileInputStream fin = new FileInputStream(file);

            int singleCharInt;
            char singleChar;

            String name = "";
            while((char)(singleCharInt = fin.read()) != '\n') {
                singleChar = (char) singleCharInt;
                name += singleChar;
            }

            String tree = "";
            while((char)(singleCharInt = fin.read()) != '\n') {
                singleChar = (char) singleCharInt;
                tree += singleChar;
            }

            HashMap<String, Character> data = new HashMap<>();

            readTree(tree, "", data);

            System.out.println(data);

            String foo = "";
            while((char)(singleCharInt = fin.read()) != '\n') {
                singleChar = (char) singleCharInt;
                foo += singleChar;
            }
            int bits = Integer.parseInt(foo.trim());

            String endSigniture = "";
            for (int i = 0; i < bits; i++) {
                endSigniture += '0';
            }

            // This method of decompressing files is more optimized for smaller files, even better than the other method.
            // However, if the file gets larger, it won't perform as well

            /*byte[] bytes = fin.readAllBytes();
            String compressed = "";
            for (byte baz : bytes) {
                String str = String.format("%8s", Integer.toBinaryString(baz & 0xFF)).replace(' ', '0');
                compressed += str;
            }

            // TODO: Change it so it reads two bytes, gets the data, and then saves it
            // Don't read the entire file and save it, read and write on go

            int index = 0;
            String decompressed = "";
            System.out.println(bits);
            while (compressed.length() > bits) {
                String str = compressed.substring(0, index+1);
                if (data.containsKey(str)) {
                    decompressed += data.get(str);
                    compressed = compressed.substring(index+1);
                    index = 0;
                }
                index++;
            }

            FileOutputStream fout = new FileOutputStream(name.trim());
            fout.write(decompressed.getBytes());
            fout.close();*/

            int firstBit = fin.read();
            String compressed = String.format("%8s", Integer.toBinaryString(firstBit & 0xFF)).replace(' ', '0');

            FileOutputStream fout = new FileOutputStream(name.trim());

            while ((singleCharInt = fin.read()) != -1) {

                compressed += String.format("%8s", Integer.toBinaryString(singleCharInt & 0xFF)).replace(' ', '0');

                int index = 0;
                while (index < compressed.length() && !(fin.available() == 0 && compressed.equals(endSigniture))) {
                    String key = compressed.substring(0, index+1);
                    if (data.containsKey(key)) {
                        compressed = compressed.substring(index+1);
                        fout.write(Character.toString(data.get(key)).getBytes());
                        index = 0;
                    }
                    index++;
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}