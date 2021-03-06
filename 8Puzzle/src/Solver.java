/*************************************************************************
* Name: Paolo Re
*
* Description: The Solver class
* Algorithms, Part I - Week 4
*
*************************************************************************/

public class Solver 
{
   private SearchNode result;
   
   private class SearchNode implements Comparable<SearchNode> 
   {
      private final Board board;
      private final int moves;
      private final SearchNode previous;
      private final int priority;

      private SearchNode(Board b, SearchNode p) 
      {
          board = b;
          previous = p;
         
          if (previous == null)
             moves = 0;
          else 
             moves = previous.moves + 1;
          
          priority = board.manhattan() + moves;
      }

      public int compareTo(SearchNode that)
      {
         return this.priority - that.priority; 
      }
  }

   // find a solution to the initial board (using the A* algorithm)
   public Solver(Board initial)
   {
      if (initial.isGoal())
         result = new SearchNode(initial, null);
      else
         result = solve(initial, initial.twin());
   }
   
   private SearchNode solve(Board initial, Board twin) 
   {
      SearchNode next;
      MinPQ<SearchNode> mainpq = new MinPQ<SearchNode>();
      MinPQ<SearchNode> twinpq = new MinPQ<SearchNode>();
      mainpq.insert(new SearchNode(initial, null));
      twinpq.insert(new SearchNode(twin, null));
      
      while (true) 
      {
          next = doStep(mainpq);
          if (next.board.isGoal())
             return next;
          if (doStep(twinpq).board.isGoal())
             return null;
      }
  }
   
   private SearchNode doStep(MinPQ<SearchNode> pq) 
   {
      SearchNode nextNode = pq.delMin();
      for (Board neighbor: nextNode.board.neighbors()) 
      {
          if (nextNode.previous == null || !neighbor.equals(nextNode.previous.board))
              pq.insert(new SearchNode(neighbor, nextNode));
      }
      return nextNode;
  }

   // is the initial board solvable?
   public boolean isSolvable()
   {
      return result != null;
   }
   
   // min number of moves to solve initial board; -1 if no solution
   public int moves()
   {
      if (isSolvable())
         return result.moves;
      else
         return -1;
   }
   
   // sequence of boards in a shortest solution; null if no solution
   public Iterable<Board> solution()
   {
      if (result == null)
         return null;
      
     Stack<Board> s = new Stack<Board>();
     
     for (SearchNode n = result; n != null; n = n.previous)
         s.push(n.board);
     return s;
   }
   
   // solve a slider puzzle (given below)
   public static void main(String[] args) 
   {
      // create initial board from file
      In in = new In(args[0]);
      int N = in.readInt();
      int[][] blocks = new int[N][N];
      for (int i = 0; i < N; i++)
          for (int j = 0; j < N; j++)
              blocks[i][j] = in.readInt();
      Board initial = new Board(blocks);

      // solve the puzzle
      Solver solver = new Solver(initial);

      // print solution to standard output
      if (!solver.isSolvable())
          StdOut.println("No solution possible");
      else {
          StdOut.println("Minimum number of moves = " + solver.moves());
          for (Board board : solver.solution())
              StdOut.println(board);
      }
   }
}