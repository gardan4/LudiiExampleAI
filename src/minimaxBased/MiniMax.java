package minimaxBased;

import game.Game;
import gnu.trove.list.array.TIntArrayList;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.context.TempContext;
import other.move.Move;


public class MiniMax extends AI
{

    //-------------------------------------------------------------------------

    /** Our player index */
    protected int player = -1;

    //-------------------------------------------------------------------------

    /**
     * Constructor
     */
    public MiniMax()
    {
        this.friendlyName = "MiniMax AI";
    }

    //-------------------------------------------------------------------------

    @Override
    public Move selectAction
            (
                    final Game game,
                    final Context context,
                    final double maxSeconds,
                    final int maxIterations,
                    final int maxDepth
            )
    {
        Move bestMove = null;
        int Maxdepthnew = 4;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        FastArrayList<Move> legalMoves = game.moves(context).moves();
        int maxPlayerId = context.state().mover();

        for (Move move : legalMoves) {
            // Create a new context to simulate the move
            Context simulatedContext = new TempContext(context);
            simulatedContext.game().apply(simulatedContext, move);
            int score = 0;

            if (simulatedContext.trial().over()) {
                score =  9999;
            }
            else {
                score = minimax(simulatedContext, Maxdepthnew - 1, alpha, beta, false, maxPlayerId);
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }

            alpha = Math.max(alpha, score);

            if (beta <= alpha) {
                break;
            }
        }

        return bestMove;
    }


    private int minimax(Context context, int depth, int alpha, int beta, boolean isMaximizingPlayer, int maxPlayerID)
    {
        FastArrayList<Move> nextLegalMoves = context.game().moves(context).moves();

        if (nextLegalMoves.size() != 0) {
            boolean isRemoveMove = nextLegalMoves.get(0).actionType().toString().contains("Remove");
            if (isRemoveMove)
            {
                //flip isMaximizingPlayer boolean value
                isMaximizingPlayer = !isMaximizingPlayer;
            }
        }


        if (depth == 0 || context.trial().over()) {
            // Implement your evaluation function here
            int value = evaluate(context, isMaximizingPlayer, maxPlayerID);
            return value;
        }

        if (isMaximizingPlayer)
        {
            int bestScore = Integer.MIN_VALUE;

            for (Move nextMove : nextLegalMoves) {
                Context simulatedContext = new TempContext(context);
                simulatedContext.game().apply(simulatedContext, nextMove);

                int score = minimax(simulatedContext, depth - 1, alpha, beta, false, maxPlayerID);
//                System.out.println("Scoremax: " + score);

                alpha = Math.max(alpha, score);
                bestScore = Math.max(bestScore, score);
//                System.out.println("Bestscoremax: " + bestScore);

                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        }
        else
        {
            int bestScore = Integer.MAX_VALUE;

            for (Move nextMove : nextLegalMoves) {
                Context simulatedContext = new TempContext(context);
                simulatedContext.game().apply(simulatedContext, nextMove);


                int score = minimax(simulatedContext, depth - 1, alpha, beta, true, maxPlayerID);
//                System.out.println("Scoremin: " + score);


                beta = Math.min(beta, score);
                bestScore = Math.min(bestScore, score);
//                System.out.println("Bestscoremin: " + bestScore);

                if (beta <= alpha) {
                    break;
                }
            }
            return bestScore;
        }
    }

    private int evaluate(Context context, boolean isMaximizingPlayer, int maxPlayerID) {
        int score = 0;


        if (context.trial().over() && isMaximizingPlayer) {
            return -9999;
        }
        else if (context.trial().over() && !isMaximizingPlayer) {
            return 9999;
        }

        int[] centerIndices = {29, 30, 31, 38, 39, 40, 41, 48, 49, 50};

        // Get the coordinates of the pieces for each player
        TIntArrayList coordinatesOfMaxPlayerPieces = context.state().owned().sites(maxPlayerID);
        TIntArrayList coordinatesOfMinPlayerPieces = context.state().owned().sites(3 - maxPlayerID);

        int maxPlayerPieceCount = coordinatesOfMaxPlayerPieces.size();
        int minPlayerPieceCount = coordinatesOfMinPlayerPieces.size();

        score += maxPlayerPieceCount;
        score -= minPlayerPieceCount;

        // Count the number of pieces in the center for each player
        for (int centerIndex : centerIndices) {
            if (coordinatesOfMaxPlayerPieces.contains(centerIndex)) {
                score += 5;
            }
            if (coordinatesOfMinPlayerPieces.contains(centerIndex)) {
                score -= 5;
            }
        }

        return score;
    }

    @Override
    public void initAI(final Game game, final int playerID)
    {
        this.player = playerID;
    }

    @Override
    public boolean supportsGame(final Game game)
    {
        if (game.isStochasticGame())
            return false;

        if (!game.isAlternatingMoveGame())
            return false;

        return true;
    }

    //-------------------------------------------------------------------------

}
