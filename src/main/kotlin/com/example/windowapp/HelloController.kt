package com.example.windowapp

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import java.util.StringJoiner

class HelloController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var button1: Button


    @FXML
    private fun onHelloButtonClick() {
        for (i in 1..100) {
           rotate(i)
        }
    }
    @FXML private fun rotate(i:Int)
    {
        val thread = Thread {
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            button1.style = "-fx-rotate: $i;"
        }
        Thread(thread).start()
    }

}