package com.example.windowapp

import javafx.animation.FadeTransition
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import javafx.scene.paint.ImagePattern
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import kotlin.system.exitProcess

enum class ImageStyle {
    CIRCLE, RECTANGLE
}

enum class ToastPosition {
    UPLEFT, UPRIGHT, BOTLEFT, BOTRIGHT
}

enum class AnimationType {
    FADE, SLIDE
}

class Config(
    var alpha: Double = 0.9,
    var openTime: Int = 7000,
    var imageShape: ImageStyle = ImageStyle.CIRCLE,
    var toastPosition: ToastPosition = ToastPosition.UPLEFT,
    var animationType: AnimationType = AnimationType.FADE,
    var image: String = "https://cumz.one/5RYkuRW.jpg",
    var startSound: String = "file:///C:/Users/olimp/Desktop/YUH.wav",
    var endSound: String = "file:///C:/Users/olimp/Desktop/AUGH.wav"
)

class Toast {
    private var config = Config()
    private val windows = Stage()
    private val root = BorderPane()
    private var hbox = HBox()
    private var vbox = VBox()

    class Builder {
        fun build(cfg: Config): Toast {
            val toast = Toast()
            toast.config = cfg
            toast.build()
            return toast
        }
    }


    private fun build() {
        val title = Label("TITLE")
        title.alignment = Pos.TOP_CENTER
        title.font = Font.font("Arial", 22.0)
        title.padding = Insets(5.0, 0.0, 0.0, 20.0)
        val text =
            Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit")
        text.textFill = Color.RED
        text.alignment = Pos.TOP_CENTER
        text.font = Font.font("Arial", 18.0)
        text.padding = Insets(5.0, 0.0, 0.0, 20.0)
        text.isWrapText = true

        setImage()
        vbox.children.add(title)
        vbox.children.add(text)
        hbox.children.add(vbox)

        val width = 500.0
        val height = 100.0
        windows.initStyle(StageStyle.TRANSPARENT)

        val screenBounds: Rectangle2D = Screen.getPrimary().bounds
        val (x: Double, y: Double) = when (config.toastPosition) {
            ToastPosition.UPLEFT -> Pair(0.0, 0.0)
            ToastPosition.UPRIGHT -> Pair(screenBounds.width - width, 0.0)
            ToastPosition.BOTLEFT -> Pair(0.0, screenBounds.height - height)
            else -> Pair(screenBounds.width - width, screenBounds.height - height)
        }
        windows.x = x
        windows.y = y

        windows.scene = Scene(root, width, height)
        windows.scene.fill = Color.TRANSPARENT
        windows.maxHeightProperty().bind(windows.scene.heightProperty().multiply(2))
        root.style = "-fx-background-color: #ffffff"
        root.setPrefSize(width, height)


        root.center = hbox

    }


    private fun setImage() {
        if (config.image.isEmpty())
            return
        val iconBorder = if (config.imageShape == ImageStyle.RECTANGLE)
            Rectangle(100.0, 100.0)
        else
            Circle(50.0, 50.0, 45.0)
        iconBorder.fill = ImagePattern(Image(config.image))
        hbox.children.add(iconBorder)
        hbox.padding = Insets(5.0, 0.0, 5.0, 5.0)
    }

    private fun openSlideAnimation() {
        val anim = javafx.animation.TranslateTransition(Duration.millis(250.0), root)
        anim.fromX =
            if (config.toastPosition == ToastPosition.BOTRIGHT || config.toastPosition == ToastPosition.UPRIGHT)
                windows.width
            else
                -windows.width
        anim.fromY = 0.0
        anim.toX = 0.0
        anim.toY = 0.0
        anim.play()
    }

    private fun closeSlideAnimation() {
        val anim = javafx.animation.TranslateTransition(Duration.millis(350.0), root)
        anim.fromX = 0.0
        anim.fromY = 0.0
        anim.toX = if (config.toastPosition == ToastPosition.BOTRIGHT || config.toastPosition == ToastPosition.UPRIGHT)
            windows.width
        else
            -windows.width - 10
        anim.toY = 0.0
        anim.onFinished = EventHandler {
            Platform.exit()
            exitProcess(0)
        }
        anim.play()
    }

    private fun openFadeAnimation() {
        val anim = FadeTransition(Duration.millis(1500.0), root)
        anim.fromValue = 0.0
        anim.toValue = config.alpha
        anim.cycleCount = 1
        anim.play()
    }

    private fun closeFadeAnimation() {
        val anim = FadeTransition(Duration.millis(1500.0), root)
        anim.fromValue = config.alpha
        anim.toValue = 0.0
        anim.cycleCount = 1
        anim.onFinished = EventHandler {
            Platform.exit()
            exitProcess(0)
        }
        anim.play()
    }

    private fun playSound(src: String) {
        if (src.isEmpty()) return
        val media = Media(src)
        val player = MediaPlayer(media)
        player.volume = 0.5
        player.play()
    }

    fun start() {
        windows.show()
        when (config.animationType) {
            AnimationType.FADE -> openFadeAnimation()
            AnimationType.SLIDE -> openSlideAnimation()
        }
        openSlideAnimation()

        val thread = Thread {
            playSound(config.startSound)
            Thread.sleep(config.openTime.toLong())
            playSound(config.endSound)
            when (config.animationType) {
                AnimationType.FADE -> closeFadeAnimation()
                AnimationType.SLIDE -> closeSlideAnimation()
            }
        }
        Thread(thread).start()
    }
}

class SomeClass : Application() {
    override fun start(p0: Stage?) {
        val toast = Toast.Builder().build(
            Config(
                openTime = 5000,
                imageShape = ImageStyle.CIRCLE,
                alpha = 0.9,
                animationType = AnimationType.SLIDE,
                toastPosition = ToastPosition.BOTLEFT,
            )
        )
        toast.start()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(SomeClass::class.java)
        }
    }

}