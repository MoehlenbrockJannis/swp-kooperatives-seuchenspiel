package de.uol.swp.client.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * A {@link StackPane} extension that displays a scalable SVG image.
 * The SVG is automatically resized to fit the available space while maintaining its aspect ratio.
 */
public class ScalableSVGStackPane extends StackPane {
    private static final Logger LOG = LogManager.getLogger(ScalableSVGStackPane.class);
    private final ImageView imageView = new ImageView();
    private final String svgPath;
    private final double imagePaddingPercentage;

    /**
     * Constructor
     * <p>
     * Creates a ScalableSVGPane from the given SVG file.
     *
     * @param svgFile                The SVG file to be loaded.
     * @param imagePaddingPercentage The padding to be applied to the created image, expressed as a percentage of the
     *                               {@link ScalableSVGStackPane}'s size. The value must be greater than or equal to
     *                               0 and less than 1. If a value outside this range is provided, a value of 0 is used.
     */
    public ScalableSVGStackPane(File svgFile, double imagePaddingPercentage) {
        this(svgFile.toURI().toString(), imagePaddingPercentage);
    }

    /**
     * Creates a ScalableSVGPane from an SVG file path.
     *
     * @param svgPath                The path to the SVG file as a URI string.
     * @param imagePaddingPercentage The padding to be applied to the created image, expressed as a percentage of the
     *                               {@link ScalableSVGStackPane}'s size. The value must be greater than or equal to
     *                               0 and less than 1. If a value outside this range is provided, a value of 0 is used.
     */
    private ScalableSVGStackPane(String svgPath, double imagePaddingPercentage) {
        this.svgPath = svgPath;
        this.imagePaddingPercentage =
                (imagePaddingPercentage < 0 || imagePaddingPercentage >= 1) ? 0 : imagePaddingPercentage;

        this.setMinSize(0, 0);
        this.getChildren().add(imageView);

        this.widthProperty().addListener((obs, oldVal, newVal) -> createNewImage());
        this.heightProperty().addListener((obs, oldVal, newVal) -> createNewImage());
    }

    /**
     * Creates a new {@link Image} based on {@link #svgPath} using {@link #createImage(double, double)}.
     * The size of the new {@link Image} is calculated by reducing the {@link ScalableSVGStackPane}'s width and height
     * by the {@link #imagePaddingPercentage}.
     */
    private void createNewImage() {
        double stackPaneWidth = this.getWidth() * (1 - imagePaddingPercentage);
        double stackPaneHeight = this.getHeight() * (1 - imagePaddingPercentage);

        if (stackPaneWidth > 0 && stackPaneHeight > 0) {
            createImage(stackPaneWidth, stackPaneHeight);
        }
    }

    /**
     * Creates a {@link Image} based on {@link #svgPath} with the given width and height and adds it to {@link #imageView}.
     */
    private void createImage(double width, double height) {
        Image svgImage = loadSVGAsImage(svgPath, (float) width, (float) height);
        if (svgImage != null) {
            imageView.setImage(svgImage);
            imageView.setPreserveRatio(true);
        }
    }

    /**
     * Loads an SVG file and converts it into a JavaFX Image.
     *
     * @param svgPath The path to the SVG file.
     * @param width   The desired initial width.
     * @param height  The desired initial height.
     * @return The generated Image.
     */
    private Image loadSVGAsImage(String svgPath, float width, float height) {
        try {
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);

            TranscoderInput input = new TranscoderInput(svgPath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(input, output);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (TranscoderException | IOException e) {
            LOG.error(e);
            return null;
        }
    }
}
