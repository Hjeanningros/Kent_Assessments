//use this kNN function as an example template 

import java.lang.Math;
import java.util.*; 
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class kNN_test 
{
    public static void main(String[] args)
    {
        try {
            //run_with_val(args);
            run_without_val(args);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void run_with_val(String[] args) throws IOException 
    {

        int TRAIN_SIZE=400; //no. training patterns 
        int VAL_SIZE=200; //no. testing patterns 
        int FEATURE_SIZE=61; //no. of features

        double[][] train = loadTrainData("alco_train_data.txt", TRAIN_SIZE, FEATURE_SIZE); //training data 
        double[][] val = loadTrainData("alco_val_data.txt", VAL_SIZE, FEATURE_SIZE); //validation data
        int[] train_label= loadTrainLabel("alco_train_label.txt", TRAIN_SIZE); //training label
        int[] val_label= loadTrainLabel("alco_val_label.txt", VAL_SIZE); //actual target/class label for validation data 
      
        System.out.println("Distance(train,val):");
  
        double[][] dist_label = new double[TRAIN_SIZE][2]; //distance array, no of columns is increased by 1 to accomodate distance
        double[] y = new double[FEATURE_SIZE]; //temp variable for validation data
        double[] x = new double[FEATURE_SIZE]; //temp variable for train data
     
        int NUM_NEIGHBOUR = 14; //k value in kNN
        int[] neighbour = new int[NUM_NEIGHBOUR];
        int[] predicted_class = new int[VAL_SIZE];
            
        for (int j=0; j<VAL_SIZE; j++) //for every validation data
        {
            for (int f=0; f<FEATURE_SIZE; f++)
            y[f]=val[j][f]; 

            for (int i=0; i<TRAIN_SIZE; i++)
            {
                for (int f=0; f<FEATURE_SIZE; f++)
                    x[f]=train[i][f]; 
    
                double sum=0.0;
                for (int f=0; f<FEATURE_SIZE; f++)
                    sum=sum + (Math.abs(x[f] - y[f])); //(x[f]-y[f])*(x[f]-y[f])); //Euclidean distance
    
                dist_label[i][0] = sum; //Euclidean distance
                dist_label[i][1] = train_label[i]; //add the target label
            }
    
            Sort(dist_label,1); //sorting
        
            for (int n=0; n<NUM_NEIGHBOUR; n++) //training label from required neighbours
                neighbour[n]=(int) dist_label[n][1];
        
            
            predicted_class[j]=Mode(neighbour);
        
        } //end test data loop
        
        //accuracy computation 
        //only if labels are provided, eg for validation data
        //disable if using test data 
        int success=0;
        for (int j=0; j<VAL_SIZE; j++)
            if (predicted_class[j]==val_label[j])
            success=success+1;
        double accuracy=(success*100.0)/VAL_SIZE;
        System.out.print("Accuracy = " + accuracy + "\n");
            
        //writing kNN_output.txt in the required format 
        try
        {
            PrintWriter writer = new PrintWriter("kNN_output.txt", "UTF-8");
                for(int j=0; j<VAL_SIZE; j++) 
                writer.print(predicted_class[j] + " ");
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    } //end main loop

    public static void run_without_val(String[] args) throws IOException 
    {

        int TRAIN_SIZE=400; //no. training patterns 
        int VAL_SIZE=200; //no. testing patterns 
        int FEATURE_SIZE=61; //no. of features

        int[] train_label= loadTrainLabel("alco_train_label.txt", TRAIN_SIZE); //training label
        double[][] val = loadTrainData("alco_test_data.txt", TRAIN_SIZE, FEATURE_SIZE); //actual target/class label for validation data 
        double[][] train = loadTrainData("alco_train_data.txt", TRAIN_SIZE, FEATURE_SIZE); //training data 

        System.out.println("Distance(train,val):");
  
        double[][] dist_label = new double[TRAIN_SIZE][2]; //distance array, no of columns is increased by 1 to accomodate distance
        double[] y = new double[FEATURE_SIZE]; //temp variable for validation data
        double[] x = new double[FEATURE_SIZE]; //temp variable for train data
     
        int NUM_NEIGHBOUR = 14; //k value in kNN
        int[] neighbour = new int[NUM_NEIGHBOUR];
        int[] predicted_class = new int[VAL_SIZE];
            
        for (int j=0; j<VAL_SIZE; j++) //for every validation data
        {
            for (int f=0; f<FEATURE_SIZE; f++)
            y[f]=val[j][f]; 

            for (int i=0; i<TRAIN_SIZE; i++)
            {
                for (int f=0; f<FEATURE_SIZE; f++)
                    x[f]=train[i][f]; 
    
                double sum=0.0;
                for (int f=0; f<FEATURE_SIZE; f++)
                    sum=sum + (Math.abs(x[f] - y[f])); //(x[f]-y[f])*(x[f]-y[f])); //Euclidean distance
    
                dist_label[i][0] = sum; //Euclidean distance
                dist_label[i][1] = train_label[i]; //add the target label
                    }
    
            Sort(dist_label,1); //sorting
    
        
            for (int n=0; n<NUM_NEIGHBOUR; n++) //training label from required neighbours
                neighbour[n]=(int) dist_label[n][1];

            predicted_class[j]=Mode(neighbour);
        
        } //end test data loop

            
        //writing kNN_output.txt in the required format 
        try
        {
            PrintWriter writer = new PrintWriter("kNN_output.txt", "UTF-8");
                for(int j=0; j<VAL_SIZE; j++) 
                writer.print(predicted_class[j] + " ");
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    } //end main loop



    
    public static void Sort (double[][] sort_array, final int column_sort) //sorting function
    {
    Arrays.sort(sort_array, new Comparator<double[]>() 
        {
            @Override
            public int compare(double[] a, double[] b) 
            {
            if(a[column_sort-1] > b[column_sort-1]) return 1;
            else return -1;
            }
        });
    }
        
    
    public static int Mode(int neigh[]) //function to find mode
    {
    int modeVal=0;
    int maxCnt=0;
        
    for (int i = 0; i < neigh.length; ++i) 
        {
        int count = 0;
        for (int j = 0; j < neigh.length; ++j)
        {
            if (neigh[j] == neigh[i]) 
            count=count+1;
        }
            if (count > maxCnt) 
            {
            maxCnt = count;
            modeVal = neigh[i];
            }
        }

    return modeVal;
    }
    
    public static double [][] loadTrainData(String train_file, int TRAIN_SIZE, int FEATURE_SIZE)
    {
        double[][] train = new double[TRAIN_SIZE][FEATURE_SIZE];
        try (Scanner tmp = new Scanner(new File(train_file))) {
            for (int i=0; i<TRAIN_SIZE; i++) {
                for (int j=0; j<FEATURE_SIZE; j++) {
                    if(tmp.hasNextDouble()) {
                        train[i][j]=tmp.nextDouble();
                        //System.out.print(train[i][j] + " ");
                    }
                }
            }
        tmp.close();
        } catch (Exception ex) {
            System.out.println(ex);;
        }
        return train;
    }

    public static int[] loadTrainLabel(String train_label_file, int TRAIN_SIZE)
    {
        int[] train_label = new int[TRAIN_SIZE];
        try (Scanner tmp = new Scanner(new File(train_label_file))) {
            for (int i=0; i<TRAIN_SIZE; i++) {
                if(tmp.hasNextInt()) {
                    train_label[i]=tmp.nextInt();
                    //System.out.print(train_label[i] + " ");
                }
            }
        tmp.close();
        } catch (Exception ex) {
            return null;
        }
        return train_label;
    }
    
} //end class loop