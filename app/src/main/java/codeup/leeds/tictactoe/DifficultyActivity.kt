package codeup.leeds.tictactoe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.difficulty_activity.*

class DifficultyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.difficulty_activity)

        // Start the play activity with singlePlayer true and selected difficulty.
        val intent = Intent(this@DifficultyActivity, PlayActivity::class.java)
        intent.putExtra("singlePlayer", true)

        easy.setOnClickListener {
            intent.putExtra("difficulty", "easy")
            startActivity(intent)
        }
        tricky.setOnClickListener {
            intent.putExtra("difficulty", "tricky")
            startActivity(intent)
        }
        impossible.setOnClickListener {
            intent.putExtra("difficulty", "impossible")
            startActivity(intent)
        }
    }
}
