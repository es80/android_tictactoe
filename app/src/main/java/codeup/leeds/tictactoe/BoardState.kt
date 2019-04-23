package codeup.leeds.tictactoe

class BoardState {
    // We represent the board as a 2-dimensional array of Players.
    private val boardState = arrayOf(
        arrayOf<Player?>(null, null, null),
        arrayOf<Player?>(null, null, null),
        arrayOf<Player?>(null, null, null)
    )

    fun getPlayerAtPosition(row: Int, col: Int): Player? {
        // We subtract 1 on each, because array indexes start at 0, not 1.
        return boardState[row - 1][col - 1]
    }

    fun setPlayerAtPosition(row: Int, col: Int, player: Player?) {
        // We subtract 1 on each, because array indexes start at 0, not 1.
        boardState[row - 1][col - 1] = player
    }

    fun playerHasWon(player: Player): Boolean {
        return (winningHorizontally(player) || winningVertically(player) ||
               winningDiagonally1(player) || winningDiagonally2(player))
    }

    private fun winningHorizontally(player: Player): Boolean {
        for (num in 0..2) {
            if (boardState[num][0] == player &&
                boardState[num][1] == player &&
                boardState[num][2] == player
            ) {
                return true
            }
        }
        return false
    }

    private fun winningVertically(player: Player): Boolean {
        for (num in 0..2) {
            if (boardState[0][num] == player &&
                boardState[1][num] == player &&
                boardState[2][num] == player
            ) {
                return true
            }
        }
        return false
    }

    private fun winningDiagonally1(player: Player): Boolean {
        for (num in 0..2) {
            if (boardState[num][num] != player) {
                return false
            }
        }
        return true
    }

    private fun winningDiagonally2(player: Player): Boolean {
        for (num in 0..2) {
            if (boardState[num][2-num] != player) {
                return false
            }
        }
        return true
    }

    fun noFreeTiles(): Boolean {
        // We loop over all tiles to see if unclaimed.
        for (row in boardState) {
            for (player in row) {
                if (player == null) {
                    return false
                }
            }
        }
        return true
    }

    fun resetBoard() {
        // Reset the board array to nulls.
        for (row in 1..3) {
            for (col in 1..3) {
                setPlayerAtPosition(row, col, null)
            }
        }
    }
}
