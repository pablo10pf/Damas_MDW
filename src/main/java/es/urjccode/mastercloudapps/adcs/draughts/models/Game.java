package es.urjccode.mastercloudapps.adcs.draughts.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private Board board;
    private Turn turn;

    Game(Board board) {
        this.turn = new Turn();
        this.board = board;
    }

    public Game() {
        this(new Board());
        this.reset();
    }

    public void reset() {
        for (int i = 0; i < Coordinate.getDimension(); i++)
            for (int j = 0; j < Coordinate.getDimension(); j++) {
                Coordinate coordinate = new Coordinate(i, j);
                Color color = Color.getInitialColor(coordinate);
                Piece piece = null;
                if (color != null)
                    piece = new Pawn(color);
                this.board.put(coordinate, piece);
            }
        if (this.turn.getColor() != Color.WHITE)
            this.turn.change();
    }

    public Error move(Coordinate... coordinates) {
        Error error = null;
        List<Coordinate> removedCoordinates = new ArrayList<Coordinate>();
        int pair = 0;
        do {
            error = this.isCorrectPairMove(pair, coordinates);
            if (error == null) {
                this.pairMove(removedCoordinates, pair, coordinates);
                pair++;
            }
        } while (pair < coordinates.length - 1 && error == null);

        checkEatingEnemyWasPossible(error, coordinates);

        error = this.isCorrectGlobalMove(error, removedCoordinates, coordinates);
        if (error == null)
            this.turn.change();
        else
            this.unMovesUntilPair(removedCoordinates, pair, coordinates);
        return error;
    }

    private void checkEatingEnemyWasPossible(Error error, Coordinate[] coordinates) {
        if (error == null && coordinates.length == 2) {
            if (this.isPossibleEatingEnemyInDiagonals(coordinates[0]))
                this.board.remove(coordinates[1]);
            removeRandomPieceIfIsPossibleEatingEnemyInTurn();
        }
        if (error == null && coordinates.length == 3) {
            removeRandomPieceIfIsPossibleEatingEnemyInTurn();
        }
    }

    private Error isCorrectPairMove(int pair, Coordinate... coordinates) {
        assert coordinates[pair] != null;
        assert coordinates[pair + 1] != null;
        if (board.isEmpty(coordinates[pair]))
            return Error.EMPTY_ORIGIN;
        if (this.turn.getOppositeColor() == this.board.getColor(coordinates[pair]))
            return Error.OPPOSITE_PIECE;
        if (!this.board.isEmpty(coordinates[pair + 1]))
            return Error.NOT_EMPTY_TARGET;
        List<Piece> betweenDiagonalPieces =
            this.board.getBetweenDiagonalPieces(coordinates[pair], coordinates[pair + 1]);
        return this.board.getPiece(coordinates[pair]).isCorrectMovement(betweenDiagonalPieces, pair, coordinates);
    }

    private void pairMove(List<Coordinate> removedCoordinates, int pair, Coordinate... coordinates) {
        Coordinate forRemoving = this.getBetweenDiagonalPiece(pair, coordinates);
        if (forRemoving != null) {
            removedCoordinates.add(0, forRemoving);
            this.board.remove(forRemoving);
        }
        this.board.move(coordinates[pair], coordinates[pair + 1]);
    }

    private Coordinate getBetweenDiagonalPiece(int pair, Coordinate... coordinates) {
        assert coordinates[pair].isOnDiagonal(coordinates[pair + 1]);
        List<Coordinate> betweenCoordinates = coordinates[pair].getBetweenDiagonalCoordinates(coordinates[pair + 1]);
        if (betweenCoordinates.isEmpty())
            return null;
        for (Coordinate coordinate : betweenCoordinates) {
            if (this.getPiece(coordinate) != null)
                return coordinate;
        }
        return null;
    }

    private Error isCorrectGlobalMove(Error error, List<Coordinate> removedCoordinates, Coordinate... coordinates) {
        if (error != null)
            return error;
        if (coordinates.length > 2 && coordinates.length > removedCoordinates.size() + 1)
            return Error.TOO_MUCH_JUMPS;
        return null;
    }

    private void unMovesUntilPair(List<Coordinate> removedCoordinates, int pair, Coordinate... coordinates) {
        for (int j = pair; j > 0; j--)
            this.board.move(coordinates[j], coordinates[j - 1]);
        for (Coordinate removedPiece : removedCoordinates)
            this.board.put(removedPiece, new Pawn(this.getOppositeTurnColor()));
    }

    public boolean isBlocked() {
        for (Coordinate coordinate : this.getCoordinatesWithActualColor())
            if (!this.isBlocked(coordinate))
                return false;
        return true;
    }

    private List<Coordinate> getCoordinatesWithActualColor() {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        for (int i = 0; i < this.getDimension(); i++) {
            for (int j = 0; j < this.getDimension(); j++) {
                Coordinate coordinate = new Coordinate(i, j);
                Piece piece = this.getPiece(coordinate);
                if (piece != null && piece.getColor() == this.getTurnColor())
                    coordinates.add(coordinate);
            }
        }
        return coordinates;
    }

    private boolean isBlocked(Coordinate coordinate) {
        for (int i = 1; i <= 2; i++)
            for (Coordinate target : coordinate.getDiagonalCoordinates(i))
                if (this.isCorrectPairMove(0, coordinate, target) == null)
                    return false;
        return true;
    }

    private boolean isPossibleEatingEnemyInDiagonals(Coordinate coordinate) {
        List<Coordinate> destinationDiagonalCoordinates = coordinate.getCoordinatesFromDestinationDiagonals();
        if (!destinationDiagonalCoordinates.isEmpty()) {
            for (Coordinate destinationCoordinate : destinationDiagonalCoordinates) {
                if (this.board.getPiece(destinationCoordinate) == null) {
                    Coordinate[] coordinates = {coordinate, destinationCoordinate};
                    Coordinate betweenDiagonalCoordinate = this.getBetweenDiagonalPiece(0, coordinates);
                    if (betweenDiagonalCoordinate != null && this.board.getPiece(betweenDiagonalCoordinate) != null)
                        if (this.turn.getColor() != this.board.getColor(betweenDiagonalCoordinate))
                            return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    private List<Coordinate> getCoordinatesWhereIsPossibleEatingEnemyInTurn() {
        List<Coordinate> canBeRemovedCoordinates = new ArrayList<Coordinate>();
        for (Coordinate coordinate : this.getCoordinatesWithActualColor()) {
            if (this.isPossibleEatingEnemyInDiagonals(coordinate))
                canBeRemovedCoordinates.add(coordinate);
        }
        return canBeRemovedCoordinates;
    }

    private void removeRandomPieceIfIsPossibleEatingEnemyInTurn() {
        List<Coordinate> canBeRemovedCoordinates = this.getCoordinatesWhereIsPossibleEatingEnemyInTurn();
        if (!canBeRemovedCoordinates.isEmpty()) {
            Collections.shuffle(canBeRemovedCoordinates);
            this.board.remove(canBeRemovedCoordinates.get(0));
        }
    }

    public void cancel() {
        for (Coordinate coordinate : this.getCoordinatesWithActualColor())
            this.board.remove(coordinate);
        this.turn.change();
    }

    public Color getColor(Coordinate coordinate) {
        assert coordinate != null;
        return this.board.getColor(coordinate);
    }

    public Color getTurnColor() {
        return this.turn.getColor();
    }

    private Color getOppositeTurnColor() {
        return this.turn.getOppositeColor();
    }

    public Piece getPiece(Coordinate coordinate) {
        assert coordinate != null;
        return this.board.getPiece(coordinate);
    }

    public int getDimension() {
        return Coordinate.getDimension();
    }

    @Override
    public String toString() {
        return this.board + "\n" + this.turn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((turn == null) ? 0 : turn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Game other = (Game) obj;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        if (turn == null) {
            if (other.turn != null)
                return false;
        } else if (!turn.equals(other.turn))
            return false;
        return true;
    }

}
