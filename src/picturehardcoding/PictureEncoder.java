package picturehardcoding;

import java.io.*;
import java.lang.invoke.MethodHandles;

public class PictureEncoder {

  /*
   *  Takes an input file and generates a Java file containing a class definition.
   *  The generated class contains a number of methods, and each method defines and returns an array that represents a part of the input file.
   */

  public static void main(String[] args) {

    File fIn;
    InputStream iStream;
    String inputFile="";        /* input file name, given by the user */
    String shortFileName;       /* input file name, without other path sequences */
    int lastDotIndex;           /* index of last dot in the file name; used for extracting file extension */
    String fileExtension = "";  /* input file's extension */
    int fileData;               /* used for reading bytes from the input file */

    File fOut;
    OutputStream oStream;
    String outputFile = "HardCodedFile.java";
    String outputFileWithPath="";
    String s="";                /* used for writing data to the output file */
    String lineSeparator;       /* used for writing new line characters to the output file */

    boolean createNewMethod;			  /* used for deciding when to start generating another method's code */
    int numberOfGeneratedMethods=0;       /* counts the generated methods */
    int arraySize=5000;                   /* size of each array that stores a part of the input file */
    int numberOfWrittenElements=0;        /* counts the array elements put in the generated code that represents array's creation with initialization */
    boolean firstElementInArray = false;  /* used to determine when to write a comma before adding a new element to the array's initialization sequence */

    Package currentPackage;     /* used for storing the class package */

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    System.out.println("Current working path is "+System.getProperty("user.dir"));
    System.out.print("Please enter the input file name: ");

    try{
      inputFile = br.readLine();
    }
    catch(IOException e){
      System.out.println("A problem has occured: "+e.getMessage());
      return;
    }

    fIn = new File(inputFile);

    fOut = new File(outputFile);

    createNewMethod = true;

    try{

      if(fIn.getCanonicalPath().equalsIgnoreCase(fOut.getCanonicalPath())){
        throw new IllegalArgumentException("Same file.");
      }

      iStream = new FileInputStream(fIn);

      if (iStream.available()==0){
        System.out.println("Input file is empty.");
      }

      else{

        /* it means there is data that can be read from the input file */

        if(fOut.exists()){
          System.out.println("A file named \""+fOut.getName()+"\" already exists under current path. No output file was generated.");
        }
        else{

          System.out.println("Generating output Java file. Please wait...");

          oStream = new FileOutputStream(fOut);

          lineSeparator = System.getProperty("line.separator");

          /* get package name for the current class and use the same package name for the generated class */
          currentPackage = MethodHandles.lookup().lookupClass().getPackage();
          if(currentPackage != null) {
            s = "package "+currentPackage.getName()+";";
            oStream.write(s.getBytes());
            oStream.write(lineSeparator.getBytes());
            oStream.write(lineSeparator.getBytes());
          }

          s = "class HardCodedFile{";
          oStream.write(s.getBytes());
          oStream.write(lineSeparator.getBytes());
          oStream.write(lineSeparator.getBytes());

          while ((fileData=iStream.read()) >= 0){

            /*
             * In the output file, we generate definitions of arrays containing pieces of data read from the input file;
             * Each array is defined in a separate method, because Java only allows a limited method body size, 
             * and a too large array would cause a compilation error in the code that this program generates.
             * Each array has maximum "arraySize" elements; when we reach "arraySize", we generate code for another method, 
             * containing another array declaration with initialization.
             */

            if(createNewMethod == true) {

              /* beginning method definition: */
              s = "  short[] method"+numberOfGeneratedMethods+"(){";
              oStream.write(s.getBytes());
              oStream.write(lineSeparator.getBytes());

              /* beginning array definition: */
              oStream.write("    short[] filePart = {".getBytes());

              /* mark that we already started creating a method */
              createNewMethod = false;

              firstElementInArray = true;

            }

            if (firstElementInArray) {
              oStream.write(Integer.toString(fileData).getBytes());
              firstElementInArray = false;
            }
            else{
              oStream.write((", "+fileData).getBytes());
            }

            numberOfWrittenElements++;

            if (numberOfWrittenElements==arraySize){

              /*
               *  it means that the array we are creating has reached maximum established size
               *  and for the next data being read from the file we need to create another method containing another array
               */

              /* closing definition of current array */
              oStream.write("};".getBytes());
              oStream.write(lineSeparator.getBytes());

              oStream.write("    return filePart;".getBytes());
              oStream.write(lineSeparator.getBytes());

              /* closing body of current method */
              oStream.write("  }".getBytes());
              oStream.write(lineSeparator.getBytes());
              oStream.write(lineSeparator.getBytes());

              numberOfGeneratedMethods++;

      	      /* mark that we should start creating another method that will contain another piece of data read from the file */
              createNewMethod = true;

              /* we reset the counter of elements written in an array */
              numberOfWrittenElements = 0;

            }

          }

          if(createNewMethod == false) {

            /*
             * It means that the total number of data in the input file is not multiple of "arraySize" number, 
             * and the last generated array and method must be closed explicitly, here.
             * Had the last array had exactly "arraySize" elements, it would have been closed in the "while" loop, 
             * and the method that contained it would have also been closed there.
             */

            /* closing definition of last generated array */
            oStream.write("};".getBytes());
            oStream.write(lineSeparator.getBytes());

            oStream.write("    return filePart;".getBytes());
            oStream.write(lineSeparator.getBytes());

            /* closing body of last generated method */
            oStream.write("  }".getBytes());
            oStream.write(lineSeparator.getBytes());
            oStream.write(lineSeparator.getBytes());
            numberOfGeneratedMethods++;

          }

          oStream.write(("  int numberOfMethods = "+numberOfGeneratedMethods+";").getBytes());
          oStream.write(lineSeparator.getBytes());
          oStream.write(lineSeparator.getBytes());

          /* 
           *  Getting input file's short name; this will be helpful when extracting file's extension, based on the last dot in the file's name.
           *  Since folders can also contain dots in their names, we need to avoid incorrect extension calculation, 
           *  for example when a file without extension is given as input, but having a relative path that included dots.
           *  For this reason, we use "getName()" method, which returns the file's short name, without any other path details.
           */

          /* get input file's short name, based on the name that the file has on disk */
          shortFileName = new File(fIn.getCanonicalPath()).getName();

          lastDotIndex = shortFileName.lastIndexOf(".");
          if (lastDotIndex >= 0)
            fileExtension = shortFileName.substring(lastDotIndex);
          /* otherwise, it means that the input file has no extension */

          oStream.write(("  String fileExtension = \""+fileExtension+"\";").getBytes());
          oStream.write(lineSeparator.getBytes());
          oStream.write(lineSeparator.getBytes());

          /* closing definition of generated class */
          oStream.write("}".getBytes());

          oStream.close();

          outputFileWithPath = fOut.getCanonicalPath();
          System.out.println("Finished generating file "+ outputFileWithPath);

        }

      }

      iStream.close();

    }

    catch (FileNotFoundException e){
      if (e.getMessage().contains(inputFile+" (The system cannot find the file specified)")){
      System.out.println("A file with the name you entered could not be found.\n"
                       + "Please make sure the path is correct and that you include the file extension.");
      }
      else{
        System.out.println("A problem has occured: "+e.getMessage());
      }
      return;
    }

    catch (IllegalArgumentException e){
      if (e.getMessage().contains("Same file")){
        System.out.println("The name you entered for the input file is reserved for the output file (case insensitive). "
                         + "Please use an input file with a different name or path.");
      }
      else{
        System.out.println("A problem has occured: "+e.getMessage());
      }
      return;
    }

    catch (IOException e){
      System.out.println("A problem has occured: "+e.getMessage());
      return;
    }

  }

}
