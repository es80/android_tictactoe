package codeup.leeds.tictactoe

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.play_activity.*

class PlayActivity : AppCompatActivity() {

    private val state = AppState()

    private var singlePlayer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_activity)

        // Call the tileClicked method when a user taps a tile
        row1Col1.setOnClickListener { tileClicked(1, 1) }
        row1Col2.setOnClickListener { tileClicked(1, 2) }
        row1Col3.setOnClickListener { tileClicked(1, 3) }
        row2Col1.setOnClickListener { tileClicked(2, 1) }
        row2Col2.setOnClickListener { tileClicked(2, 2) }
        row2Col3.setOnClickListener { tileClicked(2, 3) }
        row3Col1.setOnClickListener { tileClicked(3, 1) }
        row3Col2.setOnClickListener { tileClicked(3, 2) }
        row3Col3.setOnClickListener { tileClicked(3, 3) }

        // For a new game, reset the state and the computer player.
        newGameButton.setOnClickListener {
            state.setComputerPlayer(singlePlayer)
            resetState()
        }

        // Return to main menu.
        mainMenuButton.setOnClickListener {
            startActivity(Intent(this@PlayActivity, MainActivity::class.java))
        }

        // Set variables for single player mode.
        singlePlayer = intent.getBooleanExtra("singlePlayer", false)
        state.setComputerPlayer(singlePlayer)
        state.difficulty = when (intent.getStringExtra("difficulty")) {
            "easy" -> Difficulty.Easy
            "tricky" -> Difficulty.Tricky
            else -> Difficulty.Impossible
        }
        // If the current player is the computer we must make the first move.
        if (state.currentPlayer == state.computerPlayer) {
            state.makeComputerMove()
        }

        invalidate()
    }

    private fun tileClicked(row: Int, col: Int) {

        state.tileClicked(row, col)

        // If playing the computer, make its move too.
        if (state.currentlyPlaying && state.currentPlayer == state.computerPlayer) {
            state.makeComputerMove()
        }

        invalidate()
    }

    // This method updates the UI.
    // This should be the only place that we do this, and it should update based on the AppState
    private fun invalidate() {

        // Set visibility of buttons when game is finished.
        if (!state.currentlyPlaying) {
            newGameButton.visibility = View.VISIBLE
            mainMenuButton.visibility = View.VISIBLE
        }
        else {
            newGameButton.visibility = View.INVISIBLE
            mainMenuButton.visibility = View.INVISIBLE
        }

        setMessage()

        // Set visibility of scores when in single player mode.
        if (singlePlayer) {
            setScore()
        }
        else {
            scoreTable.visibility = View.INVISIBLE
        }

        // Update each tile's image.
        setTileImage(row1Col1, 1, 1)
        setTileImage(row1Col2, 1, 2)
        setTileImage(row1Col3, 1, 3)
        setTileImage(row2Col1, 2, 1)
        setTileImage(row2Col2, 2, 2)
        setTileImage(row2Col3, 2, 3)
        setTileImage(row3Col1, 3, 1)
        setTileImage(row3Col2, 3, 2)
        setTileImage(row3Col3, 3, 3)
    }

    private fun setMessage() {
        // Update the message to the player, either an end of game message or
        // whose move it is.
        message.text = when {
            state.boardState.playerHasWon(Player.Naughts) -> getString(R.string.win_naughts)
            state.boardState.playerHasWon(Player.Crosses) -> getString(R.string.win_crosses)
            state.boardState.noFreeTiles() -> getString(R.string.drawn)
            singlePlayer ->
                if (state.computerPlayer == state.currentPlayer) {
                    getString(R.string.computer_turn)
                } else {
                    getString(R.string.user_turn)
                }
            else -> getString(R.string.turn, state.currentPlayer)
        }
    }

    private fun setScore() {
        // Display the score tally.
        scoreTable.visibility = View.VISIBLE
        winsNum.text = state.score[0].toString()
        drawsNum.text = state.score[1].toString()
        lossesNum.text = state.score[2].toString()

    }

    private fun setTileImage(tile: ImageView, row: Int, col: Int) {
        val imageToUse = getPlayerImageForTile(row, col)

        tile.setImageDrawable(imageToUse)
    }

    private fun getPlayerImageForTile(row: Int, col: Int): Drawable? {
        return when (state.boardState.getPlayerAtPosition(row, col)) {
            Player.Naughts -> getDrawable(R.drawable.ic_circle)
            Player.Crosses -> getDrawable(R.drawable.ic_cross)
            else -> getDrawable(R.color.white)
        }
    }

    private fun resetState() {
        state.reset()

        // For single player mode we have already reset the computer player, now check to see if
        // the computer moves first.
        if (state.currentPlayer == state.computerPlayer) {
            state.makeComputerMove()
        }

        invalidate()
    }
}
