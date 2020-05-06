<h3>Introduction</h3>

This program is a solution I found when I needed to generate a picture directly from a Java program.
It can be useful when you don’t want a program that comes with separate picture files, or that takes them from the internet.

Practically, I found a way of hard-coding a picture inside a Java file. It is stored inside an explicitly initialized and fragmented data structure.  

I will explain later why I wanted to do such an unconventional thing.

<h3>How to use the application</h3>

Please follow the following steps:

1) Compile “PictureEncoder.java” and then run it. It will ask you for an input file: that is the picture you want to hard-code. The result of the execution will be a Java file called “HardCodedFile.java”. This file stores the picture and will be needed later, when you want to re-create the picture.

2) Compile “HardCodedFile.java”.

3) Compile “PictureGenerator.java” and then run it. Based on “HardCodedFile” class, it will re-generate the file that is hard-coded in “HardCodedFile.java” file.

(more explicit examples on how to run the code are described later)

<h3>Why I wanted to hard-code a picture inside a Java file</h3>

Well, while I was trying to obtain a Java Programmer job, I thought that instead of sending my resume to companies in conventional ways, it might be cool if I sent them (via e-mail) a Java program that would generate my resume, to show a bit of my Java skills.

What I had on my mind was a Java program that would generate an html output file (actually a basic text file, with html tags for text formatting) that would contain my resume. And since that would not have been complex enough, I thought it would be cool if I also included a picture of mine, placed in my resume with a classic ```<img>``` html tag. Of course, I could have just sent my picture as a separate image file, but I wanted my Java program to intrigue and impress, to make people wonder where that picture file came from. This is why I decided to keep the image hard-coded in my Java program and generate it later, from the code, together with the html file that was supposed to be my resume.

<h3>How I started the development and how I got to the present solution</h3>

Initially, I wanted to keep everything in one single Java class, so I could send by mail one single “.class” file, as my resume.

However, for the version I published on GitHub, I decided to use a separate Java file for the hard-coded picture, so that the code that re-constructs the picture could be easier understood by whoever reads it. 

Also, when I was using the initial “all in one single Java class” approach, for large pictures I encountered lags in Eclipse editor, because it was difficult for Eclipse to navigate through the large Java generated code that represented a high resolution picture. Navigating through that large code while writing or simply copy-pasting code pre-written by me for the rest of the program logic was problematic in Eclipse for 1080p pictures, and when I tried with a 4K picture, Eclipse stopped responding and later crashed with an out of memory error. I did however manage to generate a 4K picture using the “all in one single Java class” approach, but I used Notepad as code editor and I compiled and ran the program using console commands.

My initial “all in one single Java class” approach was meeting my needs (I was going to use a small sized picture, that wouldn’t have caused freezing problems to the IDE), but I wanted to create a final product capable of handling large pictures, so I eventually chose to keep the manually written code in a separate Java file than the automatically generated code.

The code I published here works great with 4K pictures, even in Eclipse, because the generated code that represents the picture is kept separately and you don’t have to edit that code, so Eclipse will simply compile it, without going through the freezing problems encountered when trying to edit that large code. 

When it comes to how the picture itself is hard-coded, I also had to make adjustments during the development process. In the early stages, I was hard-coding a picture as one single array of numeric elements. A helper class was generating a text file containing an array definition with initialization, where each array element was a byte of data read from the input picture file. After that, I was simply copy-pasting that array to the Java code where I needed the hard-coded picture. 

However, this approach wasn’t good for large pictures, because when keeping an entire large picture in one single array, although the compiler wasn’t complaining about the array itself, it was having a problem with the method that contained that array. More specifically, it was complaining about the size of the enclosing method: "The code of method <method_name> is exceeding the 65535 bytes limit". Practically, a large array was causing its enclosing method to exceed a size limit. Moving the array outside the method and keeping it as a static class field or as an instance field did not solve the problem, because static initializers or constructors also have a 65535 bytes limit. I even tried to store the picture as an enum, but that had size limits, too.

So, I decided to store the picture fragmented into multiple arrays, with each array being kept in a method of its own, which would respect the 65535 bytes size limit. I decided that each method’s name would follow a pattern: the string “method”, followed by an incrementing number. For the picture’s reconstruction, I needed to be able to invoke those methods, in order. The problem was that those methods had to be invoked by their names, which were variable. Luckily for me, Java can invoke methods with variable names, using a feature called Reflection.

Here is a sample on how the picture is hard-coded:
```
class HardCodedFile{

  short[] method0(){
    short[] filePart = { <a lot of numbers separated by commas> };
    return filePart;
  }

  short[] method1(){
    short[] filePart = { <a lot of numbers separated by commas> };
    return filePart;
  }

  short[] method2(){
    short[] filePart = { <a lot of numbers separated by commas> };
    return filePart;
  }

  short[] method3(){
    short[] filePart = { <a lot of numbers separated by commas> };
    return filePart;
  }

  int numberOfMethods = 4;

  String fileExtension = ".jpg";

}
```
<h3>More details on how to execute the application</h3>

I developed this application using Eclipse Neon 3 (which comes with Java 8), under Windows 10.

If you want to test it with Eclipse, the simplest way to do it is to create a package called “picturehardcoding” and place “PictureEncoder.java” and “PictureGenerator.java” files in that package. You run “PictureEncoder”, give it an input picture and it will generate a Java file called “HardCodedFile.java”. If you are using Eclipse with default settings, you need to place “HardCodedFile.java” file in the same place where “PictureGenerator.java” is, and ask for a Refresh (by default, Build Automatically setting is enabled). Then, you run “PictureGenerator” and it will re-create the picture.

If you want to test everything using the command line, you must remember that, being part of a package, the class files should be placed in a directory structure that reflects the package name. 

Assuming that both “PictureEncoder.java” and “PictureGenerator.java” files are located in the same folder (“D:\Test”, let’s say), here’s an example on how to run everything:
```
D:\Test>java -version
java version "1.8.0_231"
Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.231-b11, mixed mode)
D:\Test>javac -d . PictureEncoder.java
D:\Test>java picturehardcoding.PictureEncoder
Current working path is D:\Test
Please enter the input file name: in.png
Generating output Java file. Please wait...
Finished generating file D:\Test\HardCodedFile.java
D:\Test>javac -d . HardCodedFile.java
D:\Test>javac -d . PictureGenerator.java
D:\Test>java picturehardcoding.PictureGenerator
Current working path is D:\Test
Generating output file. Please wait...
Finished generating file D:\Test\GeneratedFile.png
D:\Test>
```
<h3>Limitations</h3>

First of all, the Java file that hard-codes an image file is larger than the original file. For instance, a 4K (6000 x 4000 pixels, 24BPP, JPG) picture with the size of around 3.5 MB resulted in a 15 MB Java file.

If you try to hard-code a too large file, compilation of “HardCodedFile.java” file will fail with "too many constants" error. That is because there is a limit for the constant pool table. This pool table is where most of the literal constant values are stored and includes values such as numbers, strings, identifier names, references to classes and methods, and type descriptors. All these should not exceed 65535 entries.

Also, compilation of “HardCodedFile.java” file could fail with “java.lang.OutOfMemoryError: Java heap space”, if “HardCodedFile.java” is too large. For example, an 11 MB input file resulted in a “HardCodedFile.java” file with the size of 50 MB, and compiling such a large file exceeded my maximum heap memory size, whose default value was around 1.5 GB. I had to explicitly increase memory size, so that the compilation could run successfully (I used this command: “javac -J-Xmx2g -d . HardCodedFile.java”, which set my maximum heap memory size to 2 GB).

<h3>Did I have success with this job seeking strategy?</h3>

Although the purpose of what I did was to send resumes to companies in a non-conventional way, I have never actually sent a resume this way (at least not until the date on which I published this document). Companies receive many job applications, and recruiters don’t have too much time to read even conventional resumes. Moreover, when you send an e-mail to a company, the first person that usually reads it is a non-technical person who wouldn’t know what to do with a “.class” file. At most, they might forward it to the Java department, if you offered them enough hints in the mail where you sent them the “.class” file. And assuming your mail reaches a technical person, they may not want to run your program, for fear of malicious code. Sure, they could use a decompiler to obtain the source code, but what would they see? Some lines of understandable Java code and some Java code containing large arrays initialized with numeric data, that makes no sense to them and looks dangerous. In short, they would have to go through some trouble to obtain the resume in a readable format. Most of them wouldn’t make this effort. And those who would make time to run my program might not want to take the risk involved.

There was also the problem with file types allowed by e-mail providers. In 2020, Google still allows attaching “.class” and “.java” files, so this isn’t currently a problem, but things may change in the future.

<h3>Conclusion</h3>

With this application, I showed you how to use Java in a controversial and strange - but funny - way, where I really pushed the technical limits and the common-sense limits. I don’t recommend doing what I did, but, for the way I overcame the technical challenges I came across, I wanted to share my code with the community. 

