import java.util.ArrayList;
import java.util.List;

public class Board {

    public String[][] coins; // 8x8 chessboard
    public boolean isWhiteTurn;
    List<Move> history = new ArrayList<>();

    public Board(String[][] coins, boolean isWhiteTurn) {
        this.coins = new String[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(coins[i], 0, this.coins[i], 0, 8);
        }
        this.isWhiteTurn = isWhiteTurn;
    }

    // Clone constructor (for simulation)
    public Board(Board other) {
        this(other.coins, other.isWhiteTurn);
    }

    public String getPieceAt(int row, int col) {
    return coins[row][col];
}

public int pieceValue(String piece) {
    if (piece == null) return 0;

    switch (piece) {
        case "♙": case "♟": return 10;
        case "♘": case "♞": return 30;
        case "♗": case "♝": return 30;
        case "♖": case "♜": return 50;
        case "♕": case "♛": return 90;
        case "♔": case "♚": return 900;
        default: return 0;
    }
}


    public List<Move> getAllLegalMoves(boolean forWhite) {
        List<Move> moves = new ArrayList<>();

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                String piece = coins[fromRow][fromCol];
                if (piece == null) continue;

                if ((forWhite && isWhite(piece)) || (!forWhite && isBlack(piece))) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            if (isLegalMove(fromRow, fromCol, toRow, toCol, piece)) {
                                moves.add(new Move(fromRow, fromCol, toRow, toCol));
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    public ArrayList<Move> getAllLegalMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                String piece = coins[fromRow][fromCol];
                if (piece == null) continue;

                if ((isWhiteTurn && isWhite(piece)) || (!isWhiteTurn && isBlack(piece))) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            if (isLegalMove(fromRow, fromCol, toRow, toCol, piece)) {
                                moves.add(new Move(fromRow, fromCol, toRow, toCol));
                            }
                        }
                    }
                }
            }
        }

        return moves;
    }

    public Board makeMove(Move move) {
        Board newBoard = new Board(this);

        newBoard.history.addAll(this.history);
        newBoard.history.add(move);
        String piece = newBoard.coins[move.fromRow][move.fromCol];
        newBoard.coins[move.toRow][move.toCol] = piece;
        newBoard.coins[move.fromRow][move.fromCol] = null;
        newBoard.isWhiteTurn = !newBoard.isWhiteTurn;

        return newBoard;
    }

    public boolean isRepetitive(Move move) {
        int count = 0;
        for (int i = history.size() - 4; i >= 0 && count < 4; i--) {
            if (history.get(i).equals(move)) count++;
        }
        return count >= 2;
    }

    private static final double[][] PAWN_TABLE = {
    { 0, 0, 0, 0, 0, 0, 0, 0 },
    { 5, 5, 5, -5, -5, 5, 5, 5 },
    { 1, 1, 2, 3, 3, 2, 1, 1 },
    { 0.5, 0.5, 1, 2.5, 2.5, 1, 0.5, 0.5 },
    { 0, 0, 0, 2, 2, 0, 0, 0 },
    { 0.5, -0.5, -1, 0, 0, -1, -0.5, 0.5 },
    { 0.5, 1, 1, -2, -2, 1, 1, 0.5 },
    { 0, 0, 0, 0, 0, 0, 0, 0 }
};

private static final double[][] KNIGHT_TABLE = {
    {-5, -4, -3, -3, -3, -3, -4, -5},
    {-4, -2, 0, 0, 0, 0, -2, -4},
    {-3, 0, 1, 1.5, 1.5, 1, 0, -3},
    {-3, 0.5, 1.5, 2, 2, 1.5, 0.5, -3},
    {-3, 0, 1.5, 2, 2, 1.5, 0, -3},
    {-3, 0.5, 1, 1.5, 1.5, 1, 0.5, -3},
    {-4, -2, 0, 0.5, 0.5, 0, -2, -4},
    {-5, -4, -3, -3, -3, -3, -4, -5}
};

private static final double[][] BISHOP_TABLE = {
    {-2, -1, -1, -1, -1, -1, -1, -2},
    {-1, 0, 0, 0, 0, 0, 0, -1},
    {-1, 0, 0.5, 1, 1, 0.5, 0, -1},
    {-1, 0.5, 0.5, 1, 1, 0.5, 0.5, -1},
    {-1, 0, 1, 1, 1, 1, 0, -1},
    {-1, 1, 1, 1, 1, 1, 1, -1},
    {-1, 0.5, 0, 0, 0, 0, 0.5, -1},
    {-2, -1, -1, -1, -1, -1, -1, -2}
};

private static final double[][] ROOK_TABLE = {
    {0, 0, 0, 0.5, 0.5, 0, 0, 0},
    {-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    {-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    {-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    {-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    {-0.5, 0, 0, 0, 0, 0, 0, -0.5},
    {0.5, 1, 1, 1, 1, 1, 1, 0.5},
    {0, 0, 0, 0, 0, 0, 0, 0}
};

private static final double[][] QUEEN_TABLE = {
    {-2, -1, -1, -0.5, -0.5, -1, -1, -2},
    {-1, 0, 0, 0, 0, 0, 0, -1},
    {-1, 0, 0.5, 0.5, 0.5, 0.5, 0, -1},
    {-0.5, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5},
    {0, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5},
    {-1, 0.5, 0.5, 0.5, 0.5, 0.5, 0, -1},
    {-1, 0, 0.5, 0, 0, 0, 0, -1},
    {-2, -1, -1, -0.5, -0.5, -1, -1, -2}
};

private static final double[][] KING_TABLE = {
    {-3, -4, -4, -5, -5, -4, -4, -3},
    {-3, -4, -4, -5, -5, -4, -4, -3},
    {-3, -4, -4, -5, -5, -4, -4, -3},
    {-3, -4, -4, -5, -5, -4, -4, -3},
    {-2, -3, -3, -4, -4, -3, -3, -2},
    {-1, -2, -2, -2, -2, -2, -2, -1},
    {2, 2, 0, 0, 0, 0, 2, 2},
    {2, 3, 1, 0, 0, 1, 3, 2}
};




    public int evaluate() {
    int score = 0;
    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            String piece = coins[r][c];
            if (piece == null) continue;

            int value = 0;
            double positional = 0;

            switch (piece) {
                case "♙":
                    value = 10;
                    positional = PAWN_TABLE[r][c];
                    break;
                case "♘":
                    value = 30;
                    positional = KNIGHT_TABLE[r][c];
                    break;
                case "♗":
                    value = 30;
                    positional = BISHOP_TABLE[r][c];
                    break;
                case "♖":
                    value = 50;
                    positional = ROOK_TABLE[r][c];
                    break;
                case "♕":
                    value = 90;
                    positional = QUEEN_TABLE[r][c];
                    break;
                case "♔":
                    value = 900;
                    positional = KING_TABLE[r][c];
                    break;

                case "♟":
                    value = -10;
                    positional = -PAWN_TABLE[7 - r][c];
                    break;
                case "♞":
                    value = -30;
                    positional = -KNIGHT_TABLE[7 - r][c];
                    break;
                case "♝":
                    value = -30;
                    positional = -BISHOP_TABLE[7 - r][c];
                    break;
                case "♜":
                    value = -50;
                    positional = -ROOK_TABLE[7 - r][c];
                    break;
                case "♛":
                    value = -90;
                    positional = -QUEEN_TABLE[7 - r][c];
                    break;
                case "♚":
                    value = -900;
                    positional = -KING_TABLE[7 - r][c];
                    break;
            }

            score += value + (int) positional;
        }
    }

    return score;
}



    public boolean isWhite(String piece) {
        return piece != null && "♖♘♗♕♔♙".contains(piece);
    }

    public boolean isBlack(String piece) {
        return piece != null && "♜♞♝♛♚♟".contains(piece);
    }

    public boolean isSameTeam(String p1, String p2) {
        return (isWhite(p1) && isWhite(p2)) || (isBlack(p1) && isBlack(p2));
    }

    public boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        if (piece == null || (coins[toRow][toCol] != null && isSameTeam(piece, coins[toRow][toCol])))
            return false;

        switch (piece) {
            case "♙": case "♟": return isLegalPawnMove(fromRow, fromCol, toRow, toCol, piece);
            case "♘": case "♞": return isLegalKnightMove(fromRow, fromCol, toRow, toCol, piece);
            case "♗": case "♝": return isLegalBishopMove(fromRow, fromCol, toRow, toCol, piece);
            case "♖": case "♜": return isLegalRookMove(fromRow, fromCol, toRow, toCol, piece);
            case "♕": case "♛": return isLegalQueenMove(fromRow, fromCol, toRow, toCol, piece);
            case "♔": case "♚": return isLegalKingMove(fromRow, fromCol, toRow, toCol, piece);
            default: return false;
        }
    }

    public boolean isLegalPawnMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int direction = isWhite(piece) ? -1 : 1;
        int startRow = isWhite(piece) ? 6 : 1;
        String destinationPiece = coins[toRow][toCol];

        if (fromCol == toCol && toRow == fromRow + direction && destinationPiece == null) {
            return true;
        }

        if (fromCol == toCol && fromRow == startRow && toRow == fromRow + 2 * direction
                && destinationPiece == null && coins[fromRow + direction][fromCol] == null) {
            return true;
        }

        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction && destinationPiece != null
                && !isSameTeam(piece, destinationPiece)) {
            return true;
        }

        return false;
    }

    public boolean isLegalKnightMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        String targetPiece = coins[toRow][toCol];

        boolean isLShape = ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2));
        return isLShape && (targetPiece == null || !isSameTeam(targetPiece, piece));
    }

    public boolean isLegalBishopMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff != colDiff) return false;

        int rowDirection = (toRow > fromRow) ? 1 : -1;
        int colDirection = (toCol > fromCol) ? 1 : -1;

        int r = fromRow + rowDirection;
        int c = fromCol + colDirection;

        while (r != toRow || c != toCol) {
            if (r < 0 || r >= 8 || c < 0 || c >= 8) return false;
            if (coins[r][c] != null) return false;
            r += rowDirection;
            c += colDirection;
        }

        String destinationPiece = coins[toRow][toCol];
        return destinationPiece == null || !isSameTeam(piece, destinationPiece);
    }

    public boolean isLegalRookMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        boolean isVertical = fromCol == toCol && fromRow != toRow;
        boolean isHorizontal = fromRow == toRow && fromCol != toCol;

        if (!isVertical && !isHorizontal) return false;

        int rowDirection = Integer.compare(toRow, fromRow);
        int colDirection = Integer.compare(toCol, fromCol);

        int r = fromRow + rowDirection;
        int c = fromCol + colDirection;

        while (r != toRow || c != toCol) {
            if (coins[r][c] != null) return false;
            r += rowDirection;
            c += colDirection;
        }

        String destinationPiece = coins[toRow][toCol];
        return destinationPiece == null || !isSameTeam(piece, destinationPiece);
    }

    public boolean isLegalQueenMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        return isLegalBishopMove(fromRow, fromCol, toRow, toCol, piece) ||
               isLegalRookMove(fromRow, fromCol, toRow, toCol, piece);
    }

    public boolean isLegalKingMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff <= 1 && colDiff <= 1) {
            String targetPiece = coins[toRow][toCol];
            return targetPiece == null || !isSameTeam(piece, targetPiece);
        }

        return false;
    }
}
