/*
 * C3338047
 * Yuji Ishikawa
 * Purpose: Run Clock algorithm
 * Read Me: this is using arrayLists.
 * So, basic clock policy does not work correctly in particular pointer
 * Therefore, the pointer is fixed into head and the Arraylist is rotating
 * */

import java.util.ArrayList;
import java.util.Collections;

public class Clock
{
    private static int time = 0, count = 0, totalpage = 0, penalty = -6, head = 0, frame, unit, argLength, numOfFrames; //30 frames / 4 = 7 each process can have 7 frames
    private static ArrayList<Process>[] myProcess; //each process have own page
    private static ArrayList<Process>[] pFrame; //each process have own main frame p
    private static ArrayList<Integer>[] fault; //save fault time and this size is number of faults
    private static int[] TA; //start - end time -> equivalent just End time because the start time is 0
    private ArrayList<Process> readyQ = new ArrayList();

    public Clock(int frame, int unit, int argLength)
    {
        this.frame = frame;
        this.unit = unit;
        this.argLength = argLength;

        //do not write in global variables because Java calculate the value before the constructor -> error
        numOfFrames = (int) frame/argLength;

        //set the array length here
        myProcess = new ArrayList[argLength];
        pFrame = new ArrayList[argLength];
        fault = new  ArrayList[argLength];
        TA = new int[argLength];

        //initializing array: MUST do this before saving the data
        for(int i=0; i < argLength; i++)
        {
            myProcess[i] = new ArrayList<Process>();
            pFrame[i] = new ArrayList<Process>();
            fault[i] = new ArrayList<Integer>();
        }
    }

    public static void setMyProcess(ArrayList<Process> newProcess) //copying each process[] of values
    {
        for(int i = 0; i < newProcess.size(); i++)
        {
            myProcess[count].add(newProcess.get(i));
            totalpage++;
        }
        count++;
    }

    public void print(String[] fileName) //
    {
        System.out.println("Clock - Fixed:");
        System.out.println("PID  Process Name      Turnaround Time  # Faults  Fault Times");
        for(int i=0; i< argLength; i++)
        {
            String printOut ="";
            for(int j =0; j<fault[i].size(); j++)
            {
                printOut += fault[i].get(j);
                if(j<fault[i].size()-1)
                {
                    printOut += ", ";
                }
            }

            System.out.println(String.format("%-3s  %-12s      %-15s  %-7s   %-11s", i+1, fileName[i],  TA[i], fault[i].size(),  "{"+ printOut +"}"));
        }
    }

    public void clockAlgo()
    {
        ArrayList<Process> cpu = new ArrayList();

        while(totalpage > 0)
        {
            //dispatching phase

            dispatch();

            if(readyQ.isEmpty())
            {
                time();
            }

            //cpu and clock phase
            while(!readyQ.isEmpty())
            {
                cpu.add(readyQ.get(head));
                readyQ.remove(head);

                //extract an index value from name ex) P1-1
                int index = Integer.parseInt(cpu.get(head).getName().substring(1,2)) - 1; // P"1"-1 "1" is for Index of pFrame, but index starts from 0. So need -1

                //dispatching to pFrame based on index values
                pFrame[index].add(cpu.get(head));

                if(pFrame[index].size() > numOfFrames)  //if numOfFrames are full.
                {
                    boolean replaced =false;
                    //if the page is exist in the pFrame -> just replace aka remove the previous one
                    for (int j = 0; j < pFrame[index].size() - 1; j++)
                    {
                        if (pFrame[index].get(j).getPage() == pFrame[index].get(pFrame[index].size() - 1).getPage()) //numOfFrames-1 is the new comer position, and this process want to takeover
                        {
                            //todo swap
                            Collections.swap(pFrame[index], j, pFrame[index].size() - 1);
                            pFrame[index].remove(pFrame[index].size() - 1); //remove old itself
                            replaced =true;
                            break;
                        }
                    }

                    if(!replaced)
                    {
                        //clock policy apply but the pointer is fixed because of arrayList behavior
                        pFrame[index].remove(head);
                    }
                }

                //removing from cpu
                cpu.remove(head);

                //updating time because of dispatching to pFrame
                time();

                //updating Turn Around time after time increased
                TA[index] = time;

                //already 1 unit is used for the above
                int currentUnit = 1;

                while(currentUnit < unit) // ex) 1 < 3
                {
                    //chance to know the same page in the pFrame
                    //if the same page is in the pFrame change the value to true
                    for (int j = 0; j < pFrame[index].size(); j++)
                    {
                        if(!myProcess[index].isEmpty())
                        {
                            if (pFrame[index].get(j).getPage() == myProcess[index].get(head).getPage())
                            {
                                myProcess[index].get(head).setInMain(true);
                                break;
                            }
                        }
                    }

                    //penalty
                    if(!myProcess[index].isEmpty() && !myProcess[index].get(head).isInMain())
                    {
                        setPenalty(index);
                        currentUnit = unit; //break the while loop
                    }

                    //if in the main page, add to the pFrame total page -- and current Unit ++
                    if(!myProcess[index].isEmpty() && myProcess[index].get(head).isInMain())
                    {
                        pFrame[index].add(myProcess[index].get(head));
                        myProcess[index].remove(head);
                        totalpage--;
                        time();
                        TA[index] = time;
                        currentUnit++;
                    }

                    if(pFrame[index].size() > numOfFrames)  //if numOfFrames are full.
                    {
                        boolean replaced =false;

                        //if the page is exist in the pFrame -> just replace aka remove the previous one
                        for (int j = 0; j < pFrame[index].size()-1; j++)
                        {
                            if (pFrame[index].get(j).getPage() == pFrame[index].get(pFrame[index].size()-1).getPage()) //numOfFrames-1 is the new comer position, and this process want to takeover
                            {
                                //swap same one
                                Collections.swap(pFrame[index], j, pFrame[index].size() - 1);
                                pFrame[index].remove(pFrame[index].size() - 1); //remove old itself
                                replaced = true;
                                break;
                            }
                        }

                        //if it does not allocate in the pFrame the longest page will be replaced here
                        if(!replaced)
                        {
                            //clock policy apply but the pointer is fixed because of arrayList behavior
                            pFrame[index].remove(head);
                        }
                    }

                    //special case
                    if(myProcess[index].isEmpty())
                    {
                        currentUnit = unit;
                    }

                } //end of while(currentUnit < unit)

            }// end of while(!readyQ.isEmpty())

        }//end of while(totalpage > 0)
    }

    public void dispatch() //a regular dispatch route
    {
        for(int i=0; i < argLength; i++)
        {
            if(!myProcess[i].isEmpty())
            {
                //first time
                if(!myProcess[i].get(head).isInMain() && myProcess[i].get(head).getPenalty() == 0 && pFrame[i].isEmpty())
                {
                    setPenalty(i);
                }

                //check from the 2nd time and if the current head one is not in the main
                if(!pFrame[i].isEmpty() && !myProcess[i].get(head).isInMain())
                {
                    for(int j=0; j< pFrame[i].size(); j++)
                    {
                        if(pFrame[i].get(j).getPage() == myProcess[i].get(head).getPage())
                        {
                            myProcess[i].get(head).setInMain(true); //no more penalty
                            break;
                        }
                    }
                }

                if(!myProcess[i].get(head).isInMain() && myProcess[i].get(head).getPenalty() == 0) //regular penalty
                {
                    setPenalty(i);
                }

                //dispatch here if the process is in main
                if(myProcess[i].get(head).isInMain())
                {
                    readyQ.add(myProcess[i].get(head));
                    myProcess[i].remove(head);

                    totalpage--;
                }
            } // end of if(!myProcess[i].isEmpty())
        } //end of for loop
    }

    public void setPenalty(int index)
    {
        //set penalty here and log the event in the buffer stream print
        myProcess[index].get(head).setPenalty(penalty);
        fault[index].add(time);
    }

    public void time()
    {
        time++;

        //relief penalty here
        for(int i=0; i < argLength; i++)
        {
            if(!myProcess[i].isEmpty())
            {
                if(myProcess[i].get(head).getPenalty() < 0)
                {
                    myProcess[i].get(head).setPenalty(myProcess[i].get(head).getPenalty()+1);
                    if(myProcess[i].get(head).getPenalty() == 0)//dispatch here if the process is in main
                    {
                        myProcess[i].get(head).setInMain(true); //no more penalty
                    }
                }

                if(myProcess[i].get(head).isInMain())
                {
                    readyQ.add(myProcess[i].get(head));
                    myProcess[i].remove(head);

                    totalpage--;
                }
            }
        }
    }

}
