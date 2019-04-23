package codeup.leeds.tictactoe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // One player button starts DifficultyActivity, two player goes straight to PlayActivity.
        onePlayer.setOnClickListener {
            val intent = Intent(this@MainActivity, DifficultyActivity::class.java)
            startActivity(intent)
        }
        twoPlayer.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayActivity::class.java)
            intent.putExtra("singlePlayer", false)
            startActivity(intent) }
    }
}
