package es.urjccode.mastercloudapps.adcs.draughts.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RemovingRandomPieceAfterNotEatingEnemyTest extends GameTest {

    private void assertMove(Coordinate... coordinates) {
        assertNull(this.game.move(coordinates));
        assertEquals(this.game, this.expectedGame);
    }

    @Test
    public void testRemoveWhenMoveAndNotEatWhenPossibleInWhite() {
        this.setGame(Color.WHITE,
            "        ",
            "        ",
            "        ",
            "        ",
            "     n  ",
            "      b ",
            "        ",
            "        ");
        this.setExpectedGame(Color.BLACK,
            "        ",
            "        ",
            "        ",
            "        ",
            "     n  ",
            "        ",
            "        ",
            "        ");
        this.assertMove(
            new Coordinate(5, 6),
            new Coordinate(4, 7)
        );
    }

    @Test
    public void testRemoveWhenMoveAndNotEatWhenPossibleInBlack() {
        this.setGame(Color.BLACK,
            "        ",
            "        ",
            "        ",
            "      n ",
            "     b  ",
            "        ",
            "        ",
            "        ");
        this.setExpectedGame(Color.WHITE,
            "        ",
            "        ",
            "        ",
            "        ",
            "     b  ",
            "        ",
            "        ",
            "        ");
        this.assertMove(
            new Coordinate(3, 6),
            new Coordinate(4, 7)
        );
    }

    @Test
    public void testRemoveWhenOneMoveDoneAndNotEatWhenPossibleInWhite() {
        this.setGame(Color.WHITE,
            "        ",
            "        ",
            "        ",
            "    n   ",
            "        ",
            "      n ",
            "       b",
            "        ");
        this.setExpectedGame(Color.BLACK,
            "        ",
            "        ",
            "        ",
            "    n   ",
            "        ",
            "        ",
            "        ",
            "        ");
        this.assertMove(
            new Coordinate(6, 7),
            new Coordinate(4, 5)
        );
    }

    @Test
    public void testRemoveWhenOneMoveDoneAndNotEatWhenPossibleInBlack() {
        this.setGame(Color.BLACK,
            "        ",
            "        ",
            "   n    ",
            "    b   ",
            "        ",
            "      b ",
            "        ",
            "        ");
        this.setExpectedGame(Color.WHITE,
            "        ",
            "        ",
            "        ",
            "        ",
            "        ",
            "      b ",
            "        ",
            "        ");
        this.assertMove(
            new Coordinate(2, 3),
            new Coordinate(4, 5)
        );
    }

    @Test
    public void testRemoveWhenTwoMovesDoneAndNotEatWhenPossibleInWhite() {
        this.setGame(Color.WHITE,
            "        ",
            "  n     ",
            "        ",
            "    n   ",
            "        ",
            "      n ",
            "       b",
            "        ");
        this.setExpectedGame(Color.BLACK,
            "        ",
            "  n     ",
            "        ",
            "        ",
            "        ",
            "        ",
            "        ",
            "        ");
        this.assertMove(
            new Coordinate(6, 7),
            new Coordinate(4, 5),
            new Coordinate(2, 3)
        );
    }

    @Test
    public void testRemoveWhenTwoMovesDoneAndNotEatWhenPossibleInBlack() {
        this.setGame(Color.BLACK,
            " n      ",
            "  b     ",
            "        ",
            "    b   ",
            "        ",
            "      b ",
            "        ",
            "        ");
        this.setExpectedGame(Color.WHITE,
            "        ",
            "        ",
            "        ",
            "        ",
            "        ",
            "      b ",
            "        ",
            "        ");
        this.assertMove(
            new Coordinate(0, 1),
            new Coordinate(2, 3),
            new Coordinate(4, 5)
        );
    }
}
