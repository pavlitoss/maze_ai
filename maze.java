import java.util.Random;
import java.util.Scanner;

class Maze{
    int n;
    String[][] maze;
    double p; //
    Random rand = new Random();
    

    void generateMaze(int n,double p, int S_x, int S_y, int G_x, int G_y){
        this.n = n;
        maze = new String[n][n];
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                double r = rand.nextDouble();
                if(r<p){
                    maze[i][j] = "x";
                }else{
                    maze[i][j] = "o";
                }
            }
        }
        maze[S_x][S_y] = "S";
        maze[G_x][G_y] = "G";
        maze[n-1][0] = "o";
        maze[0][n-1] = "o";
    }

    void printMaze(){
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                System.out.print(maze[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args){
        Maze m = new Maze();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the size of the maze: ");
        int n = sc.nextInt();
        System.out.println("Enter the probability of obstacles (0-1): ");
        double p = sc.nextDouble();
        System.out.println("Enter the start point (2 numbers separated by whitespaces): ");
        int S_x = sc.nextInt();
        int S_y = sc.nextInt();
        System.out.println("Enter the goal point (2 numbers separated by whitespaces): ");
        int G_x = sc.nextInt();
        int G_y = sc.nextInt();
        m.generateMaze(n,p,S_x,S_y,G_x,G_y);
        m.printMaze();
    }
}