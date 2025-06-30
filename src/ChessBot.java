import java.util.ArrayList;
import java.util.Random;
//import Move;

public class ChessBot {
    private Chess game; // Reference to the Chess game

    public ChessBot(Chess game) {
        this.game = game;
    }

    public void makeRandomMove() {
        ArrayList<Move> legalMoves = new ArrayList<>();
        String[][] coins = game.coins;

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                String piece = coins[fromRow][fromCol];
                if (piece == null || !game.isBlack(piece)) continue;

                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        boolean isLegal = false;
                        if (piece.equals("♟") && game.isLegalPawnMove(fromRow, fromCol, toRow, toCol, piece)) {
                            isLegal = true;
                        } else if (piece.equals("♞") && game.isLegalKnightMove(fromRow, fromCol, toRow, toCol, piece)) {
                            isLegal = true;
                        } else if (piece.equals("♝") && game.isLegalBishopMove(fromRow, fromCol, toRow, toCol, piece)) {
                            isLegal = true;
                        } else if (piece.equals("♜") && game.isLegalRookMove(fromRow, fromCol, toRow, toCol, piece)) {
                            isLegal = true;
                        } else if (piece.equals("♛") && game.isLegalQueenMove(fromRow, fromCol, toRow, toCol, piece)) {
                            isLegal = true;
                        } else if (piece.equals("♚") && game.isLegalKingMove(fromRow, fromCol, toRow, toCol, piece)) {
                            isLegal = true;
                        }

                        if (isLegal) {
                            legalMoves.add(new Move(fromRow, fromCol, toRow, toCol));
                        }
                    }
                }
            }
        }

        if (!legalMoves.isEmpty()) {
            Move chosen = legalMoves.get(new Random().nextInt(legalMoves.size()));
            game.makeBotMove(chosen); // Call method in Chess.java to execute the bot's move
        }
    }
    

    // ////////////////////////////////////
    public Move getBestMove(Board board, int depth, boolean maximizingPlayer) {
    ArrayList<Move> legalMoves = board.getAllLegalMoves();

        legalMoves.sort((a, b) -> {
    String capturedA = board.getPieceAt(a.toRow, a.toCol);
    String capturedB = board.getPieceAt(b.toRow, b.toCol);
    return board.pieceValue(capturedB) - board.pieceValue(capturedA);
});


    
    Move bestMove = null;
    int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;


    
    for (Move move : legalMoves) {
        if (board.isRepetitive(move)) continue;
        
        Board simulated = board.makeMove(move);
        int score = minimax(simulated, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !maximizingPlayer);


        if (maximizingPlayer && score > bestScore) {
            bestScore = score;
            bestMove = move;
        } else if (!maximizingPlayer && score < bestScore) {
            bestScore = score;
            bestMove = move;
        }
    }

    return bestMove;
}

public int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
    if (depth == 0) return board.evaluate();

    ArrayList<Move> legalMoves = board.getAllLegalMoves();
    if (legalMoves.isEmpty()) return board.evaluate();

    if (maximizingPlayer) {
        int maxEval = Integer.MIN_VALUE;
        for (Move move : legalMoves) {
            Board simulated = board.makeMove(move);
            int eval = minimax(simulated, depth - 1, alpha, beta, false);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) break; // β cut-off
        }
        return maxEval;
    } else {
        int minEval = Integer.MAX_VALUE;
        for (Move move : legalMoves) {
            Board simulated = board.makeMove(move);
            int eval = minimax(simulated, depth - 1, alpha, beta, true);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (beta <= alpha) break; // α cut-off
        }
        return minEval;
    }
}


public void makeSmartMove() {
    Board board = new Board(game.coins, false); // black bot turn
    Move bestMove = getBestMove(board, 3, false); // depth 2 for now

    if (bestMove != null) {
        game.makeBotMove(bestMove);
    }
}


}
