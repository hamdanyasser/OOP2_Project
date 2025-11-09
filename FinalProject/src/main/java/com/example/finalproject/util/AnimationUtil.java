package com.example.finalproject.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Utility class for creating smooth animations and transitions in JavaFX
 * Provides reusable animation methods for UI elements
 */
public class AnimationUtil {

    // ══════════════════════════════════════════════════════════════
    // FADE ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Fade in animation - smoothly shows a node
     * @param node The node to animate
     * @param duration Animation duration in milliseconds
     */
    public static void fadeIn(Node node, double duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Fade out animation - smoothly hides a node
     * @param node The node to animate
     * @param duration Animation duration in milliseconds
     */
    public static void fadeOut(Node node, double duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }

    /**
     * Fade in with scale animation - modern entrance animation
     * @param node The node to animate
     */
    public static void fadeInWithScale(Node node) {
        node.setOpacity(0);
        node.setScaleX(0.9);
        node.setScaleY(0.9);

        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(400), node);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition parallel = new ParallelTransition(fade, scale);
        parallel.play();
    }

    // ══════════════════════════════════════════════════════════════
    // SLIDE ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Slide in from bottom animation
     * @param node The node to animate
     * @param duration Animation duration in milliseconds
     */
    public static void slideInFromBottom(Node node, double duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), node);
        slide.setFromY(50);
        slide.setToY(0);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        parallel.play();
    }

    /**
     * Slide in from right animation
     * @param node The node to animate
     * @param duration Animation duration in milliseconds
     */
    public static void slideInFromRight(Node node, double duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), node);
        slide.setFromX(100);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        parallel.play();
    }

    /**
     * Slide in from left animation
     * @param node The node to animate
     * @param duration Animation duration in milliseconds
     */
    public static void slideInFromLeft(Node node, double duration) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(duration), node);
        slide.setFromX(-100);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(slide, fade);
        parallel.play();
    }

    // ══════════════════════════════════════════════════════════════
    // SCALE ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Scale up animation - grows the element
     * @param node The node to animate
     * @param duration Animation duration in milliseconds
     */
    public static void scaleUp(Node node, double duration) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(duration), node);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.play();
    }

    /**
     * Pulse animation - quick scale effect for attention
     * @param node The node to animate
     */
    public static void pulse(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    /**
     * Bounce animation - playful bounce effect
     * @param node The node to animate
     */
    public static void bounce(Node node) {
        TranslateTransition bounce = new TranslateTransition(Duration.millis(300), node);
        bounce.setFromY(0);
        bounce.setToY(-15);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);
        bounce.setInterpolator(Interpolator.EASE_OUT);
        bounce.play();
    }

    // ══════════════════════════════════════════════════════════════
    // ROTATION ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Rotate animation - spins the element
     * @param node The node to animate
     * @param duration Animation duration in milliseconds
     * @param angle Target rotation angle
     */
    public static void rotate(Node node, double duration, double angle) {
        RotateTransition rotate = new RotateTransition(Duration.millis(duration), node);
        rotate.setByAngle(angle);
        rotate.play();
    }

    /**
     * Shake animation - side-to-side shake (for errors)
     * @param node The node to animate
     */
    public static void shake(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    // ══════════════════════════════════════════════════════════════
    // PAGE TRANSITION ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Scene transition - fade out old scene, fade in new scene
     * @param oldScene The scene to fade out
     * @param newScene The scene to fade in
     * @param container The container holding both scenes
     */
    public static void sceneTransition(Node oldScene, Node newScene, Pane container) {
        // Fade out old scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldScene);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            // Remove old scene and add new scene
            container.getChildren().remove(oldScene);
            container.getChildren().add(newScene);

            // Fade in new scene
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newScene);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    /**
     * Modern page entrance animation - combines fade and slide
     * Use this when a new page loads
     * @param node The page node to animate
     */
    public static void pageEntrance(Node node) {
        node.setOpacity(0);
        node.setTranslateY(30);

        // Fade in
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Slide up
        TranslateTransition slide = new TranslateTransition(Duration.millis(500), node);
        slide.setFromY(30);
        slide.setToY(0);

        // Play together
        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.setInterpolator(Interpolator.EASE_OUT);
        parallel.play();
    }

    // ══════════════════════════════════════════════════════════════
    // STAGGERED ANIMATIONS (for lists/grids)
    // ══════════════════════════════════════════════════════════════

    /**
     * Stagger animation for multiple nodes (like product cards)
     * Animates each node with a delay for a cascading effect
     * @param nodes The nodes to animate
     * @param delayBetween Delay between each animation in milliseconds
     */
    public static void staggerFadeIn(Node[] nodes, double delayBetween) {
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            node.setOpacity(0);

            FadeTransition fade = new FadeTransition(Duration.millis(400), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(i * delayBetween));
            fade.play();
        }
    }

    /**
     * Stagger slide-in animation for multiple nodes
     * @param nodes The nodes to animate
     * @param delayBetween Delay between each animation in milliseconds
     */
    public static void staggerSlideIn(Node[] nodes, double delayBetween) {
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            node.setOpacity(0);
            node.setTranslateY(20);

            TranslateTransition slide = new TranslateTransition(Duration.millis(400), node);
            slide.setFromY(20);
            slide.setToY(0);

            FadeTransition fade = new FadeTransition(Duration.millis(400), node);
            fade.setFromValue(0);
            fade.setToValue(1);

            ParallelTransition parallel = new ParallelTransition(slide, fade);
            parallel.setDelay(Duration.millis(i * delayBetween));
            parallel.play();
        }
    }

    // ══════════════════════════════════════════════════════════════
    // HOVER EFFECT ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Add hover lift effect to a node (rises slightly on hover)
     * @param node The node to add the effect to
     */
    public static void addHoverLiftEffect(Node node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        node.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    /**
     * Add hover glow effect (scale with subtle movement)
     * @param node The node to add the effect to
     */
    public static void addHoverGlowEffect(Node node) {
        node.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
            scale.setToX(1.03);
            scale.setToY(1.03);

            TranslateTransition translate = new TranslateTransition(Duration.millis(150), node);
            translate.setToY(-2);

            ParallelTransition parallel = new ParallelTransition(scale, translate);
            parallel.play();
        });

        node.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), node);
            scale.setToX(1.0);
            scale.setToY(1.0);

            TranslateTransition translate = new TranslateTransition(Duration.millis(150), node);
            translate.setToY(0);

            ParallelTransition parallel = new ParallelTransition(scale, translate);
            parallel.play();
        });
    }

    // ══════════════════════════════════════════════════════════════
    // LOADING ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Infinite rotation animation (for loading spinners)
     * @param node The node to rotate
     * @return The Timeline that can be stopped later
     */
    public static Timeline createSpinnerAnimation(Node node) {
        RotateTransition rotate = new RotateTransition(Duration.millis(1000), node);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();
        return null; // Return a Timeline if you need to control it
    }

    /**
     * Pulsing animation (for loading indicators)
     * @param node The node to pulse
     * @return The Timeline that can be stopped later
     */
    public static Timeline createPulseAnimation(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
        return null;
    }

    // ══════════════════════════════════════════════════════════════
    // SUCCESS/ERROR ANIMATIONS
    // ══════════════════════════════════════════════════════════════

    /**
     * Success animation - scale in with bounce
     * @param node The node to animate
     */
    public static void successAnimation(Node node) {
        node.setOpacity(0);
        node.setScaleX(0.5);
        node.setScaleY(0.5);

        ScaleTransition scale = new ScaleTransition(Duration.millis(400), node);
        scale.setFromX(0.5);
        scale.setFromY(0.5);
        scale.setToX(1.1);
        scale.setToY(1.1);

        FadeTransition fade = new FadeTransition(Duration.millis(400), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(scale, fade);
        parallel.setOnFinished(e -> {
            ScaleTransition settle = new ScaleTransition(Duration.millis(200), node);
            settle.setToX(1.0);
            settle.setToY(1.0);
            settle.play();
        });

        parallel.play();
    }

    /**
     * Error animation - shake with red flash
     * @param node The node to animate
     */
    public static void errorAnimation(Node node) {
        // Shake effect
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}
