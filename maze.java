import java.util.Random;
import java.util.Scanner;
import java.util.*;

class Cell{
    int x;
    int y;
    String type;

    public Cell(int x, int y, String type){
        this.x = x;
        this.y = y;
        this.type = type;
    }

    @Override
    public String toString() {
        return "("+ x + ", " + y + ")";
    }

}

class Maze{ //class for creating the maze
    int n;
    Cell[][] maze;
    double p;
    Cell start;
    Cell goal;
    Random rand = new Random();
    

    void generateMaze(int n,double p, int S_x, int S_y, int G_x, int G_y){
        this.n = n;
        maze = new Cell[n][n];
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                double r = rand.nextDouble();
                if(r<p){
                    maze[i][j] = new Cell(i,j,"\u25FC"); //obstacle
                }else{
                    maze[i][j] = new Cell(i,j,"\u25A1"); //free cell
                }
            }
        }
        maze[n-1][0].type = "\u25A1"; //free bottom left and top right corners
        maze[0][n-1].type = "\u25A1";
        maze[S_x][S_y].type = "S";  //set starting-ending points
        maze[G_x][G_y].type = "G";
        start = maze[S_x][S_y];
        goal = maze[G_x][G_y];
    }

    void printMaze(){
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                System.out.print(maze[i][j].type + " ");
            }
            System.out.println();
        }
    }

    void restoreMaze(){ //restore the maze to its original state
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                if(maze[i][j].type.equals("x")){
                    maze[i][j].type = "\u25A1";
                }
            }
        }
    }
}

class Utilities{ //class for implementing the algorithms

    public List<Cell> getNeighbors(Cell current, List<Cell> visited, Maze m){ //returns a list of all the neighbouring cells that can be visited
        List<Cell> neighbors = new ArrayList<>();
        int x = current.x;
        int y = current.y;
        if(x-1>=0 && !visited.contains(m.maze[x-1][y])){ //up
            neighbors.add(m.maze[x-1][y]);
        }
        if(x+1<m.n && !visited.contains(m.maze[x+1][y])){ //down
            neighbors.add(m.maze[x+1][y]);
        }
        if(y-1>=0 && !visited.contains(m.maze[x][y-1])){ //left
            neighbors.add(m.maze[x][y-1]);
        }
        if(y+1<m.n && !visited.contains(m.maze[x][y+1])){ //right
            neighbors.add(m.maze[x][y+1]);
        }
        if(x-1>=0 && y-1>=0 && !visited.contains(m.maze[x-1][y-1])){ //up-left
            neighbors.add(m.maze[x-1][y-1]);
        }
        if(x-1>=0 && y+1<m.n && !visited.contains(m.maze[x-1][y+1])){ //up-right
            neighbors.add(m.maze[x-1][y+1]);
        }
        if(x+1<m.n && y-1>=0 && !visited.contains(m.maze[x+1][y-1])){ //down-left
            neighbors.add(m.maze[x+1][y-1]);
        }
        if(x+1<m.n && y+1<m.n && !visited.contains(m.maze[x+1][y+1])){ //down-right
            neighbors.add(m.maze[x+1][y+1]);
        }
        for(int i=0; i<neighbors.size(); i++){
            if(neighbors.get(i).type.equals("\u25FC") || neighbors.get(i).type.equals("S")){
                neighbors.remove(i);
                i--;
            }
        }
        return neighbors;
    }

    class PathNode implements Comparable<PathNode> { //used for ucs
        Cell cell;
        int cost;
        List<Cell> path;

        public PathNode(Cell cell, int cost, List<Cell> path) { 
            this.cell = cell;
            this.cost = cost;
            this.path = path;
        }

        @Override
        public int compareTo(PathNode other) { //using compareTo to compare the cost of the nodes and poll the minimum cost node from the priority queue
            return Integer.compare(this.cost, other.cost);
        }
    }

    public void ucs(Maze m) {
        PriorityQueue<PathNode> pq = new PriorityQueue<>();
        Set<Cell> visited = new HashSet<>();

        // Initialize
        List<Cell> startPath = new ArrayList<>();
        startPath.add(m.start);
        pq.add(new PathNode(m.start, 0, startPath));

        while (!pq.isEmpty()) {
            PathNode current = pq.poll();
            Cell currentCell = current.cell;

            if (visited.contains(currentCell)) continue;
            visited.add(currentCell);

            // Goal check
            if (currentCell == m.goal) {
                System.out.println("Goal reached with cost: " + current.cost);
                System.out.println("Path:");
                System.out.print("START -> ");
                for (int i = 1; i< current.path.size()-1; i++){
                    System.out.print(current.path.get(i) + " -> ");
                    int x = current.path.get(i).x;
                    int y = current.path.get(i).y;
                    m.maze[x][y].type = "x"; // mark the path in the maze
                }
                System.out.println(" GOAL\n");
                m.printMaze();
                return;
            }

            for (Cell neighbor : getNeighbors(currentCell, new ArrayList<>(visited), m)) {
                if (!visited.contains(neighbor)) {
                    List<Cell> newPath = new ArrayList<>(current.path); //create a copy of the current path and add the neighbor
                    newPath.add(neighbor);
                    pq.add(new PathNode(neighbor, current.cost + 1, newPath));
                }
            }

            if (currentCell.x == m.n - 1 && currentCell.y == 0) {
                Cell portalTarget = m.maze[0][m.n - 1];
                if (!visited.contains(portalTarget) && !portalTarget.type.equals("\u25FC")) {
                    List<Cell> newPath = new ArrayList<>(current.path);
                    newPath.add(portalTarget);
                    pq.add(new PathNode(portalTarget, current.cost + 2, newPath));
                }
            }

            if (currentCell.x == 0 && currentCell.y == m.n - 1) {
                Cell portalTarget = m.maze[m.n - 1][0];
                if (!visited.contains(portalTarget) && !portalTarget.type.equals("\u25FC")) {
                    List<Cell> newPath = new ArrayList<>(current.path);
                    newPath.add(portalTarget);
                    pq.add(new PathNode(portalTarget, current.cost + 2, newPath));
                }
            }
        }

        System.out.println("No path found to the goal.");
    }

    class AStarNode implements Comparable<AStarNode> {
        Cell cell;
        int gCost; // actual cost so far
        int fCost; // total estimated cost
        List<Cell> path;

        public AStarNode(Cell cell, int gCost, int fCost, List<Cell> path) {
            this.cell = cell;
            this.gCost = gCost;
            this.fCost = fCost;
            this.path = path;
        }

        @Override
        public int compareTo(AStarNode other) {
            return Integer.compare(this.fCost, other.fCost);
        }
    }

    public int heuristic(Cell start, Cell goal,int n){ //for A*
        int dx = Math.abs(start.x - goal.x);
        int dy = Math.abs(start.y - goal.y);
        int distToGoal = Math.max(dx, dy);
        int START_TO_BL_CORNER = Math.max((n-1)-(start.x) ,(start.y));
        int GOAL_TO_BL_CORNER = Math.max((n-1)-(goal.x) , (goal.y));
        int START_TO_TR_CORNER = Math.max((start.x) , (n-1)-(start.y));
        int GOAL_TO_TR_CORNER = Math.max((goal.x) , (n-1)-(goal.y));
        int dist1 = START_TO_BL_CORNER +2+ GOAL_TO_TR_CORNER;
        int dist2 = START_TO_TR_CORNER +2+ GOAL_TO_BL_CORNER;
        return Math.min(distToGoal, Math.min(dist1, dist2));
    }

    public void aStar(Maze m) {
        PriorityQueue<AStarNode> pq = new PriorityQueue<>();
        Set<Cell> visited = new HashSet<>();

        List<Cell> startPath = new ArrayList<>();
        startPath.add(m.start);
        int startH = heuristic(m.start, m.goal,m.n);
        pq.add(new AStarNode(m.start, 0, startH, startPath));

        while (!pq.isEmpty()) {
            AStarNode current = pq.poll();
            Cell currentCell = current.cell;

            if (visited.contains(currentCell)) continue;
            visited.add(currentCell);

            if (currentCell == m.goal) {
                System.out.println("Goal reached with cost: " + current.gCost);
                System.out.println("Path:");
                System.out.print("START -> ");
                for (int i = 1; i < current.path.size() - 1; i++) {
                    System.out.print(current.path.get(i) + " -> ");
                    int x = current.path.get(i).x;
                    int y = current.path.get(i).y;
                    m.maze[x][y].type = "x"; // mark the path in the maze
                }
                System.out.println(" GOAL\n");
                m.printMaze();
                return;
            }

            for (Cell neighbor : getNeighbors(currentCell, new ArrayList<>(visited), m)) {
                if (!visited.contains(neighbor)) {
                    List<Cell> newPath = new ArrayList<>(current.path);
                    newPath.add(neighbor);
                    int g = current.gCost + 1;
                    int f = g + heuristic(neighbor, m.goal,m.n);
                    pq.add(new AStarNode(neighbor, g, f, newPath));
                }
            }

            // Handle portals like in UCS
            if (currentCell.x == m.n - 1 && currentCell.y == 0) {
                Cell portalTarget = m.maze[0][m.n - 1];
                if (!visited.contains(portalTarget) && !portalTarget.type.equals("\u25FC")) {
                    List<Cell> newPath = new ArrayList<>(current.path);
                    newPath.add(portalTarget);
                    int g = current.gCost + 2;
                    int f = g + heuristic(portalTarget, m.goal,m.n);
                    pq.add(new AStarNode(portalTarget, g, f, newPath));
                }
            }

            if (currentCell.x == 0 && currentCell.y == m.n - 1) {
                Cell portalTarget = m.maze[m.n - 1][0];
                if (!visited.contains(portalTarget) && !portalTarget.type.equals("\u25FC")) {
                    List<Cell> newPath = new ArrayList<>(current.path);
                    newPath.add(portalTarget);
                    int g = current.gCost + 2;
                    int f = g + heuristic(portalTarget, m.goal,m.n);
                    pq.add(new AStarNode(portalTarget, g, f, newPath));
                }
            }
        }

        System.out.println("No path found to the goal.");
    }
}

class Main{ //main class
    public static void main(String[] args){
        Maze m = new Maze();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the size of the maze: ");
        int n = sc.nextInt();
        if (n<=2){
            System.out.println("Too small size.");
            return;
        }
        System.out.println("Enter the probability of obstacles (0-1): ");
        double p = sc.nextDouble();
        if (p<0 || p>1){
            System.out.println("Invalid probability.");
            return;
        }
        System.out.println("Enter the start point (2 numbers separated by whitespaces): ");
        int S_x = sc.nextInt();
        int S_y = sc.nextInt();
        if (S_x<0 || S_x>=n || S_y<0 || S_y>=n){
            System.out.println("Invalid start point.");
            return;
        }
        System.out.println("Enter the goal point (2 numbers separated by whitespaces): ");
        int G_x = sc.nextInt();
        int G_y = sc.nextInt();
        if (G_x<0 || G_x>=n || G_y<0 || G_y>=n){
            System.out.println("Invalid goal point.");
            return;
        }
        if (S_x==G_x && S_y==G_y){
            System.out.println("Start and goal points are same.");
            return;
        }
        m.generateMaze(n,p,S_x,S_y,G_x,G_y);
        m.printMaze();
        System.out.println("\nRunning Uniform Cost Search:\n");
        Utilities utils = new Utilities();
        utils.ucs(m);
        m.restoreMaze(); // restore the maze to its original state
        //m.printMaze();
        System.out.println("\nRunning A*:\n");
        utils.aStar(m);
        sc.close();
    }
}