package codeup.leeds.tictactoe

import kotlin.random.Random

class AppState {

    val boardState = BoardState()

    private var lastWinner: Player? = null

    // Select currentPlayer randomly when class is initialised.
    var currentPlayer = if (Random.nextBoolean()) Player.Naughts else Player.Crosses
        private set

    var currentlyPlaying = true
        private set

    // Computer player and difficulty for single player mode.
    var computerPlayer: Player? = null
        private set

    var difficulty: Difficulty? = null

    // Keep a tally of game results, (wins, draws, losses), in single player mode.
    var score = intArrayOf(0, 0, 0)

    fun tileClicked(row: Int, col: Int) {

        // If we aren't playing right now, do nothing.
        if (!currentlyPlaying) {
            return
        }

        // If it's the computer's turn, do nothing.
        if (currentPlayer == computerPlayer) {
            return
        }

        // If a player has already claimed this tile, do nothing.
        if (boardState.getPlayerAtPosition(row, col) != null) {
            return
        }

        // Otherwise make the move for the current player at row, col.
        makeMove(row, col)
    }

    private fun makeMove(row: Int, col: Int) {

        // Have the current player can claim this tile.
        boardState.setPlayerAtPosition(row, col, currentPlayer)

        // Now that we have updated the board we can query its current state.
        if (boardState.playerHasWon(currentPlayer)) {
            // If the current player has won, we can stop playing, and set the current player as
            // the last winner.
            currentlyPlaying = false
            lastWinner = currentPlayer

            // Also update the score for single player mode.
            if (currentPlayer == computerPlayer) score[2] += 1 else score[0] += 1

        } else if (boardState.noFreeTiles()) {
            // Otherwise, if there are no tiles left to claim, it is a draw.
            currentlyPlaying = false
            lastWinner = null
            // Update the score for single player mode.
            score[1] += 1
        }

        // Switch to next player.
        currentPlayer = nextPlayer()
    }

    fun setComputerPlayer(singlePlayer: Boolean) {
        // In single player mode randomly select which player is the computer.
        computerPlayer = when {
            singlePlayer -> if (Random.nextBoolean()) currentPlayer else nextPlayer()
            else -> null
        }
    }

    private fun nextPlayer(): Player {
        // Switch to the next player.
        return when (currentPlayer) {
            Player.Crosses -> Player.Naughts
            Player.Naughts -> Player.Crosses
        }
    }

    fun reset() {
        // Reset the board for a new game.
        boardState.resetBoard()

        currentlyPlaying = true

        // Pick player to go first based on last winner.
        currentPlayer = when (lastWinner) {
            Player.Naughts -> Player.Crosses
            Player.Crosses -> Player.Naughts
            null -> if (Random.nextBoolean()) Player.Naughts else Player.Crosses
        }
    }

    fun makeComputerMove() {
        // Make the computer's move, copy the state of the board.
        val tmpBoardState = BoardState()

        for (row in 1..3) {
            for (col in 1..3) {
                tmpBoardState.setPlayerAtPosition(row, col, boardState.getPlayerAtPosition(row, col))
            }
        }

        // Initialise a computer object based on the board, which player the computer is and what the
        // difficulty setting is.
        val computer = ComputerMove(tmpBoardState, computerPlayer!!, difficulty!!)

        // Find and make the move.
        val move = computer.findComputerMove()
        makeMove(move[0], move[1])

        return
    }
}
