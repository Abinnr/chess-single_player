import java.util.ArrayList;
import java.util.List;

public class Board {

    public String[][] coins; // 8x8 chessboard
    public boolean isWhiteTurn;

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


public Board makeMove(Move move) {
    Board newBoard = new Board(this); // Deep clone

    String piece = newBoard.coins[move.fromRow][move.fromCol];
    newBoard.coins[move.toRow][move.toCol] = piece;
    newBoard.coins[move.fromRow][move.fromCol] = null;

    newBoard.isWhiteTurn = !this.isWhiteTurn; // Switch turn
    return newBoard;
}


    public int evaluate() {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String piece = coins[r][c];
                if (piece == null) continue;

                int value = 0;
                switch (piece) {
                    case "♙": case "♟": value = 10; break;
                    case "♘": case "♞": value = 30; break;
                    case "♗": case "♝": value = 30; break;
                    case "♖": case "♜": value = 50; break;
                    case "♕": case "♛": value = 90; break;
                    case "♔": case "♚": value = 900; break;
                }

                if (isWhite(piece)) score += value;
                else score -= value;
            }
        }
        return score;
    }




public boolean isLegalMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    if (piece == null || (coins[toRow][toCol] != null && isSameTeam(piece, coins[toRow][toCol])))
        return false;

    switch (piece) {
        case "♙": case "♟":
            return isLegalPawnMove(fromRow, fromCol, toRow, toCol, piece);
        case "♘": case "♞":
            return isLegalKnightMove(fromRow, fromCol, toRow, toCol, piece);
        case "♗": case "♝":
            return isLegalBishopMove(fromRow, fromCol, toRow, toCol, piece);
        case "♖": case "♜":
            return isLegalRookMove(fromRow, fromCol, toRow, toCol, piece);
        case "♕": case "♛":
            return isLegalQueenMove(fromRow, fromCol, toRow, toCol, piece);
        case "♔": case "♚":
            return isLegalKingMove(fromRow, fromCol, toRow, toCol, piece);
        default:
            return false;
    }
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

    public boolean isLegalPawnMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int direction = isWhite(piece) ? -1 : 1;
        int startRow = isWhite(piece) ? 6 : 1;
        String destinationPiece = coins[toRow][toCol];

        // Forward by 1. Need to be the same column, to the row just 1(direction) above/below from current position, no other coin should be there in destination cell.
        if (fromCol == toCol && toRow == fromRow + direction && destinationPiece == null) {
            return true;
        }

        // Forward by 2 from start row. Need to be the same column, to the row of 2 cells(direction *2) above/below from current position, no other coin should be there in destination cell, no coins should be infront of it.
        if (fromCol == toCol && fromRow == startRow && toRow == fromRow + 2 * direction
                && destinationPiece == null && coins[fromRow + direction][fromCol] == null) {
            return true;
        }

        // Diagonal capture. Either left or right column(so abs used), to the row just 1 cell(direction) above or below from current position, destination shouldn't be null(there should be a coin to capture), the destination coin shouldn't be same team coin.
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction && destinationPiece != null
                && !isSameTeam(piece, destinationPiece)) {
            return true;
        }

        return false;
    }

    public boolean isLegalKnightMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int rowDiff=Math.abs(fromRow-toRow);
        int colDiff=Math.abs(fromCol-toCol);
        String targetPiece=coins[toRow][toCol];

        boolean isLShape=((rowDiff==2&& colDiff==1)||(rowDiff==1&& colDiff==2));
        return isLShape && (targetPiece ==null || isSameTeam(targetPiece,piece));// target should be L-shape. move to empty cell or capture opponent(not same team).
    }


    public boolean isLegalBishopMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        // Not a diagonal move
        if (rowDiff != colDiff) {
            return false;
        }

        int rowDirection = (toRow > fromRow) ? 1 : -1;
        int colDirection = (toCol > fromCol) ? 1 : -1;

        int r = fromRow + rowDirection;
        int c = fromCol + colDirection;

        // Check path for obstacles
        while (r != toRow || c != toCol) {
            if (r < 0 || r >= 8 || c < 0 || c >= 8) return false;
            if (coins[r][c] != null) return false;
            r += rowDirection;
            c += colDirection;
        }

        // Final destination
        String destinationPiece = coins[toRow][toCol];
        return destinationPiece == null || !isSameTeam(piece, destinationPiece);
    }

public boolean isLegalRookMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    // Rook moves only in straight lines through rows or columns.
    
    boolean isVertical = fromCol == toCol && fromRow != toRow;
    boolean isHorizontal = fromRow == toRow && fromCol != toCol;

    if (!isVertical && !isHorizontal) return false;// Not a valid rook move

    int rowDirection = Integer.compare(toRow, fromRow);// Compare toRow and fromRow to determine direction
    int colDirection = Integer.compare(toCol, fromCol);

    int r = fromRow + rowDirection;
    int c = fromCol + colDirection;

    while (r != toRow || c != toCol) {// check until we reach destination cell
        // If any square in between is occupied by a coin, return false
        if (coins[r][c] != null) return false; // path is blocked
        r += rowDirection;
        c += colDirection;
    }

    // Final destination must be empty or an enemy
    String destinationPiece = coins[toRow][toCol];
    return destinationPiece == null || !isSameTeam(piece, destinationPiece);
}


public boolean isLegalQueenMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    // Use bishop or rook logic for queen
    return isLegalBishopMove(fromRow, fromCol, toRow, toCol, piece) ||
           isLegalRookMove(fromRow, fromCol, toRow, toCol, piece);
}

public boolean isLegalKingMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
    int rowDiff = Math.abs(toRow - fromRow);
    int colDiff = Math.abs(toCol - fromCol);

    // Move only 1 square in any direction
    if (rowDiff <= 1 && colDiff <= 1) {
        String targetPiece = coins[toRow][toCol];
        return targetPiece == null || !isSameTeam(piece, targetPiece);
    }

    return false;
}


    // --- To be added next ---
    // List<Move> getAllLegalMoves(boolean isWhite)
    // Board makeMove(Move move)
    // boolean isLegalMove(...)
}
