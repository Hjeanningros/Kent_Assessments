//kNN_GA solution

import java.lang.Math;
import java.util.*; 
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class GA_simple{

    public static int FEATURE_SIZE=5; //no. of features
    public static double[] weight= new double[] {0.6, 3.4, 6.7, 2.9, 5.4};
  
  public static void main(String[] args) throws IOException {
  GA(); //call GA function 
  } //end main loop
  
   
  public static boolean[] GA() 
  { 
    
    int POP_SIZE=50; //population size
    int MAX_GEN=100; //maximum generation
    double[] fitness =new double[POP_SIZE];
    boolean[][] sol = new boolean[POP_SIZE][FEATURE_SIZE]; 
    boolean[][] new_sol = new boolean[POP_SIZE][FEATURE_SIZE]; 
    boolean[] final_sol = new boolean[FEATURE_SIZE]; //final best pop
  
    //create initial population
    for(int j=0; j<POP_SIZE; j++)
    { 
    int count=0;
       for(int k=0; k<FEATURE_SIZE; k++)
       {
       sol[j][k]= (Math.random()>0.5);
         if (sol[j][k])
         count++;
       }
    
      //fitness as maximum
      double sum=0.0; 
      for (int k=0; k<FEATURE_SIZE;k++)
      {
        if(sol[j][k])
        sum=sum+weight[k];
      }
    fitness[j]=sum;

    if(count>2)
    fitness[j]=0.0;
 
    } 
     
    System.arraycopy(sol, 0, new_sol, 0, sol.length); //copy initial array, new_sol=sol; 
     
    for (int gen=0; gen<MAX_GEN; gen++) {
      System.arraycopy(new_sol, 0, sol, 0, new_sol.length); //sol=new_sol; parent copied as children for GA algorithm

    //compute fitness
    for(int j=0; j<POP_SIZE; j++) 
    {
    int count=0;
       for(int k=0; k<FEATURE_SIZE; k++) 
       {
         if (sol[j][k])
         count++;
       }
    
      double sum=0.0; 
      for (int k=0; k<FEATURE_SIZE;k++)
      {
        if(sol[j][k])
        sum=sum+weight[k];
      }
    fitness[j]=sum;
    
    if(count>2)
    fitness[j]=0.0;
 
    }

    //do selection  
    double[] temp_elite = new double[5]; //array to store choosen fitness
    int[] rnd_array = new int[5]; //array of randomly choosen 5
    Random rn = new Random();
       
    for(int j=0; j<POP_SIZE; j++) 
    {
      for(int i=0; i<5; i++) { //randomly select 5
      rnd_array[i] = rn.nextInt(POP_SIZE); 
      temp_elite[i]=fitness[rnd_array[i]];
    }

      int maxAt = 0; //tournament selection
         for (int i = 0; i < temp_elite.length; i++) 
         maxAt = temp_elite[i] > temp_elite[maxAt] ? i : maxAt;
          for (int k=0; k<FEATURE_SIZE; k++)
          new_sol[j][k] = sol[rnd_array[maxAt]][k];
    } 
  
    //do mutation  
    for(int j=0; j<POP_SIZE; j++) {
      if(rn.nextDouble()<0.1) { //how many times, pm=0.1
         int rnd = rn.nextInt(POP_SIZE);  //random parent
         int rnd1 = rn.nextInt(FEATURE_SIZE);  //random gene
            if (new_sol[rnd][rnd1])
            new_sol[rnd][rnd1] = false;
            if (!new_sol[rnd][rnd1])
            new_sol[rnd][rnd1] = true;
      }
    } 
  

    //do crossover  
    boolean[] cross_array = new boolean[FEATURE_SIZE]; //array of temp crossover
    for(int j=0; j<POP_SIZE; j++) 
    {
      if(rn.nextDouble()<0.5) //how many times, pcrossover=0.5
      {
          int rnda = rn.nextInt(POP_SIZE);  //random parent
          int rndb = rn.nextInt(POP_SIZE);  //random parent
          int rnd1 = rn.nextInt(FEATURE_SIZE);  //random crossover point
          int rnd2 = rn.nextInt(FEATURE_SIZE);  //random crossover point
           
          for (int i=rnd1; i<rnd2; i++) 
          {
          cross_array[i]=new_sol[rnda][i];
          new_sol[rnda][i]=new_sol[rndb][i];
          new_sol[rndb][i]=cross_array[i];
          }
           
      }
    } 
    
    int maxAt = 0;
    for (int j = 0; j < POP_SIZE; j++) //find best solution for the generation
    maxAt = fitness[j] > fitness[maxAt] ? j : maxAt;
       for (int k=0; k<FEATURE_SIZE; k++)
       final_sol[k]=sol[maxAt][k];

      int count=0;
      for(int k=0; k<FEATURE_SIZE; k++)
         if (final_sol[k])
         count++;
                 
      double sum=0.0; 
      for (int k=0; k<FEATURE_SIZE;k++)
      {
        if(final_sol[k])
        sum=sum+weight[k];
      }
      
      if(count>2)
      sum=0.0;

    System.out.println("Best fitness = " + sum);
    
    } //end of gen loop

    int channel=0;
    for (int k=0; k<FEATURE_SIZE; k++)
       if (final_sol[k]){
       System.out.print(1 + " ");
       channel++;
       }
       else
       System.out.print(0 + " ");
    System.out.println("Channel count =  " + channel);
    
    double sum=0.0; 
    for (int k=0; k<FEATURE_SIZE;k++){
      if(final_sol[k])
      sum=sum+weight[k];
    }
    double Accuracy=sum;
   
    if (channel>2)
    Accuracy=0.0;
   
    System.out.println("Final weight =  " + Accuracy);
    
    
     //write best solution (features) to file, DO NOT MODIFY
     try
     {
        PrintWriter writer = new PrintWriter("GA_simple_output.txt", "UTF-8");
          for(int j=0; j<FEATURE_SIZE; j++) 
            if (final_sol[j])
            writer.print("1 ");
            else
            writer.print("0 ");
        writer.close();
     }
     catch(Exception e)
     {
     System.out.println(e);
     }    
    
    return final_sol;

    }

} //class 