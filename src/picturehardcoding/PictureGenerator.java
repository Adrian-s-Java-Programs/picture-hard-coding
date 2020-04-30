package picturehardcoding;

import java.io.*;
import java.lang.reflect.*;

/*
 * This class reconstructs a file that is hard-coded in another java class.
 * The hard-coded file is kept in separate pieces, stored as arrays explicitly initialized with values and defined inside individual methods.
 * The file is kept distributed, because Java methods have size constraints, and a too large file would exceed the size limit of one single method.
 * Each piece of file is returned by the method that stores that piece.
 */

public class PictureGenerator {

  public static void main(String[] args) {

    HardCodedFile hcf = new HardCodedFile();                    /* this class contains the file we want to reconstruct */
    File fOut = new File("GeneratedFile"+hcf.fileExtension);	/* output file */
    String fileNameWithPath = "";                               /* output file with path */

    Method m;           /* used to retrieve a method with a certain name */
    int methodNumber;	/* used as iterator */

    System.out.println("Current working path is "+System.getProperty("user.dir"));

    if(fOut.exists()){
      System.out.println("A file named \""+fOut.getName()+"\" already exists under current path. No output file was generated.");
    }
    else{
      System.out.println("Generating output file. Please wait...");

      try{
        OutputStream oStream = new FileOutputStream(fOut);

        /* using Java Reflection, we invoke methods with variable names, which return pieces of the file that needs to be reconstructed */
        for (methodNumber = 0; methodNumber < hcf.numberOfMethods; methodNumber++){

          /*
           * each method's name is composed of the string "method", followed by an incremental number 
           * we get the method by its name and then we invoke it;
           * the method will return a piece of the file that we want to reconstruct
           */
          m = hcf.getClass().getDeclaredMethod("method" + methodNumber);
          short[] filePart = (short[])m.invoke(hcf);

          /* writing current file part */
          for (int i = 0 ; i<filePart.length ; i++)
            oStream.write(filePart[i]);

        }

        oStream.close();
        fileNameWithPath = fOut.getCanonicalPath();
      }

      catch(ReflectiveOperationException e){
        e.printStackTrace();
        return;
      }

      catch(IOException e){
        System.out.println("A problem has occured: "+e.getMessage());
        return;
      }

      System.out.println("Finished generating file "+fileNameWithPath);

    }

  }

}
