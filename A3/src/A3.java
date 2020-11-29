/*
* C3338047
* Yuji Ishikawa
* Purpose: import files and run each algorithm
* */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class A3 {
    private static int argLength;
    private static int frame, unit;
    private static ArrayList<Process>[] myLRUProcess; // some Java environments do not work Array of arrayList
    private static ArrayList<Process>[] myClockProcess; // some Java environments do not work Array of arrayList
    private static int IDNumber = 0; //number for each Process
    private LRU myLRU;
    private Clock myClock;
    private static String[] fileName;

    private static void importFile(String fileName)
    {
        String importName = null;
        Scanner importStream = null;
        String new_iD = "P"+ Integer.toString(IDNumber+1)+ "-"; //ID number+1 is IDNumber = argLength size
        int new_page = -999; //not necessary to initialize the value here
        int count = 0; //sub number for each process like P1-1, P1-2 and so on

        try //this try is try to access the file
        {
            importName = fileName;
            importStream = new Scanner(new File(importName));

            while(importStream.hasNextLine())
            {
                try //this try is try to scan the contexts
                {
                    String line = importStream.nextLine(); //reading lines
                    if(line.equals("")) //to preventing to stop run when the beginning of the line is nothing
                    {
                        continue;
                    }

                    String[] parts = line.split(" "); //splitting by a space

                    try
                    {
                        if(!parts[0].equalsIgnoreCase("begin") || !parts[0].equalsIgnoreCase("end"))
                        {
                            new_page = Integer.parseInt(parts[0]);
                            if(new_page < 0)
                            {
                                System.err.println("Negative Values: Terminate this programming: ");
                                System.exit(0);
                            }
                            count++; //count start from 0
                            myLRUProcess[IDNumber].add(new Process(new_iD+Integer.toString(count), new_page));
                            myClockProcess[IDNumber].add(new Process(new_iD+Integer.toString(count), new_page));
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        //nothing to display because of begin and end
                    }
                } //end of try to read

                catch(ArrayIndexOutOfBoundsException a)
                {
                    System.err.println("Invalid Line Format: Not Enough Information");
                }
                catch(NoSuchElementException | NullPointerException n)
                {
                    System.err.println(n.getMessage());
                }
            } // end of while loop

            IDNumber++; //ID number increased when each file finish
        } // end of try to access the files

        catch(FileNotFoundException e) //catch for access files' errors
        {
            System.err.println("!!! Rage Mode !!! ");
            System.err.println("!!! Why you gave me wrong file name !!! ");
            System.err.println("After I count to 10, I'll be a nice girl");
            System.err.println("10, 9, 8, ... , 2, 1 ...");
            System.out.println("Error Opening The File " + importName);
        }
        finally //finally done to store the values from the text file
        {
            if(importStream !=null)
            {
                importStream.close(); //closing import stream for the next
            }
        }
    }


    private void run()
    {
        System.out.println("Looks Like You Gave Me a File Name.");
        System.out.println("...Importing...");

        for(int i =0; i<argLength; i++)
        {
            importFile(fileName[i]);
        }

        System.out.println("Importing is done. Let simulate LRU and Clock =) \n"); //not necessary to show the message

        myLRU = new LRU (frame, unit, argLength);
        for(int i=0; i< IDNumber; i++)
        {
            LRU.setMyProcess(myLRUProcess[i]);
        }
        myLRU.LRUAlgo();
        myLRU.print(fileName);

        System.out.println("------------------------------------------------------------");

        myClock = new Clock(frame, unit, argLength);
        for(int i=0; i< IDNumber; i++)
        {
            Clock.setMyProcess(myClockProcess[i]);
        }

        myClock.clockAlgo();
        myClock.print(fileName);

    }

    public static void main(String[] args)
    {
        System.out.println("G'day, I'm AggRetsuko, How are you?");

        argLength = args.length - 2;; // first two args not need for array
        fileName = new String [argLength];

        try
        {
            frame = Integer.parseInt(args[0]);
            unit = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        for (int i=0; i < argLength; i++)
        {
            fileName[i] = args[i+2];
        }

        myLRUProcess = new ArrayList[argLength]; //set the array length here
        myClockProcess  = new ArrayList[argLength];
        for(int i=0; i< argLength; i++) //initializing arrayList here MUST do this before saving the data
        {
            myLRUProcess[i] = new ArrayList<Process>();
            myClockProcess[i] = new ArrayList<Process>();
        }

        A3 sim = new A3();
        sim.run();
    }
}
