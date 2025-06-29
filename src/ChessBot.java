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

    Move bestMove = null;
    int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    for (Move move : legalMoves) {
        Board simulated = board.makeMove(move);
        int score = minimax(simulated, depth - 1, !maximizingPlayer);

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

public int minimax(Board board, int depth, boolean maximizingPlayer) {
    if (depth == 0) return board.evaluate();

    ArrayList<Move> legalMoves = board.getAllLegalMoves();
    if (legalMoves.isEmpty()) return board.evaluate();

    int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    for (Move move : legalMoves) {
        Board simulated = board.makeMove(move);
        int score = minimax(simulated, depth - 1, !maximizingPlayer);

        if (maximizingPlayer) {
            bestScore = Math.max(bestScore, score);
        } else {
            bestScore = Math.min(bestScore, score);
        }
    }

    return bestScore;
}

public void makeSmartMove() {
    Board board = new Board(game.coins, false); // black bot turn
    Move bestMove = getBestMove(board, 3, false); // depth 2 for now

    if (bestMove != null) {
        game.makeBotMove(bestMove);
    }
}


}
