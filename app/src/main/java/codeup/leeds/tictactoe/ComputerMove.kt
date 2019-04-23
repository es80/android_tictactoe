package codeup.leeds.tictactoe

import kotlin.random.Random

class ComputerMove(private val boardState: BoardState,
                   private val computerPlayer: Player,
                   private val difficulty: Difficulty) {

    fun findComputerMove(): IntArray {

        // Get the available moves.
        val available: MutableList<IntArray> = availableMoves()

        // Lists to store possible moves.
        val winning = mutableListOf<IntArray>()
        val drawing = mutableListOf<IntArray>()
        val losing = mutableListOf<IntArray>()

        // On the toughest setting we will win the game in the fewest moves if possible or else
        // draw the game selecting randomly from equivalent moves where possible.
        if (difficulty == Difficulty.Impossible) {

            // If it is the first move, return a random corner.
            if (available.size == 9) {
                return intArrayOf(if (Random.nextBoolean()) 1 else 3, if (Random.nextBoolean()) 1 else 3)
            } else {

                val maxDepth = available.size - 1
                // Loop over depths so that if we can win we do so in the fewest moves.
                for (depth in 0..maxDepth) {
                    for (m in available) {
                        // For each possible move, make the move.
                        boardState.setPlayerAtPosition(m[0], m[1], computerPlayer)
                        // Evaluate at the given depth and store move by evaluation.
                        when (minMax(boardState, nextPlayer(computerPlayer), depth)) {
                            1 -> winning.add(m)
                            0 -> drawing.add(m)
                            else -> losing.add(m)
                        }
                        // Undo the move in preparation for the next.
                        boardState.setPlayerAtPosition(m[0], m[1], null)
                    }
                    if (winning.size != 0) {
                        // If we found wining moves at current depth, return a random choice.
                        return winning.random()
                    } else if (depth < maxDepth) {
                        // Clear evaluations before next depth level.
                        winning.clear()
                        drawing.clear()
                        losing.clear()
                    }
                }
                // No win is found so return random drawing move.
                return drawing.random()
            }
        }

        // For other difficulties make the first move random.
        if (available.size == 9) {
            return availableMoves().random()
        }

        // Tricky difficultly works similarly to impossible except depth is limited to 3 so
        // evaluation considers in total computer move - response - computer move - response.
        // As well as returning the earliest winning move we also return any loss avoiding move.
        if (difficulty == Difficulty.Tricky) {

            val maxDepth = 3
            for (depth in 0..maxDepth) {
                for (m in available) {
                    boardState.setPlayerAtPosition(m[0], m[1], computerPlayer)
                    when (minMax(boardState, nextPlayer(computerPlayer), depth)) {
                        1 -> winning.add(m)
                        0 -> drawing.add(m)
                        else -> losing.add(m)
                    }
                    boardState.setPlayerAtPosition(m[0], m[1], null)
                }
                if (winning.size != 0) {
                    // If we found wining moves at current depth, return a random choice.
                    return winning.random()
                }
                if (drawing.size == 1) {
                    // If all but one move loses, play the game saving move rather than giving
                    // up at later depth with a random move when all moves lose.
                    return drawing[0]
                } else if (depth < maxDepth) {
                    winning.clear()
                    drawing.clear()
                    losing.clear()
                }
            }
            // There were no winning moves nor game saving moves at the depths searched.
            // Return a random drawing move if possible or else a random losing move.
            if (drawing.size != 0) {
                return drawing.random()
            }
            return losing.random()
        }

        // Easy difficultly only finds immediate winning moves with probability 90% and game saving
        // moves with probability 60%.
        if (difficulty == Difficulty.Easy) {
            for (m in available) {
                boardState.setPlayerAtPosition(m[0], m[1], computerPlayer)
                // If the move wins, play it with probability 90%.
                if (boardState.playerHasWon(computerPlayer)) {
                    if (Random.nextDouble() < 0.9) {
                        return m
                    }
                }
                // Else evaluate only the responses to the move, then can only be draws or losses.
                val score: Int = minMax(boardState, nextPlayer(computerPlayer), 1)
                if (score == 0) {
                    drawing.add(m)
                }
                boardState.setPlayerAtPosition(m[0], m[1], null)
            }
            // If a game saving move is found, play it with probability 60%.
            if (drawing.size == 1 && Random.nextDouble() < 0.6) {
                return drawing[0]
            }
        }

        // If no move yet played for easy difficulty, return a random move.
        return availableMoves().random()
    }

    private fun availableMoves(): MutableList<IntArray> {
        // Return a list of all the free positions.
        val moves = mutableListOf<IntArray>()
        for (row in 1..3) {
            for (col in 1..3) {
                if (boardState.getPlayerAtPosition(row, col) == null) {
                    moves.add(intArrayOf(row, col))
                }
            }
        }
        return moves
    }

    private fun nextPlayer(currentPlayer: Player): Player {
        // Switch to the next player.
        return when (currentPlayer) {
            Player.Crosses -> Player.Naughts
            Player.Naughts -> Player.Crosses
        }
    }

    private fun minMax(possBoardState: BoardState, player: Player, depth: Int): Int {
        // Perform MinMax evaluation on possBoardState, for player, as far as depth.
        // The function always returns either -1 for losing, 0 for drawing or 1 for winning.

        // Check for either side winning.
        if (possBoardState.playerHasWon(computerPlayer)) {
            return 1
        }
        if (possBoardState.playerHasWon(nextPlayer(computerPlayer))) {
            return -1
        }
        // Cut off search when depth is 0.
        if (depth == 0) {
            return 0
        }

        // Initial score at -2 or 2 so that a losing move is preferred to no move.
        var score: Int = if (player == computerPlayer) -2 else 2

        for (row in 1..3) {
            for (col in 1..3) {
                if (possBoardState.getPlayerAtPosition(row, col) == null) {

                    // Found a neighbour, make the move.
                    possBoardState.setPlayerAtPosition(row, col, player)

                    // Get the score for this node.
                    val thisScore: Int = minMax(possBoardState, nextPlayer(player), depth - 1)

                    // Update the best score.
                    if (player == computerPlayer) {
                        if (thisScore > score) {
                            score = thisScore
                        }
                    } else {
                        if (thisScore < score) {
                            score = thisScore
                        }
                    }

                    // Undo the move before looking at next neighbour.
                    possBoardState.setPlayerAtPosition(row, col, null)
                }
            }
        }

        // If score is still -2 or 2 we found not valid moves so it must be a draw.
        if (score == -2 || score == 2) {
            return 0
        }

        return score
    }
}
