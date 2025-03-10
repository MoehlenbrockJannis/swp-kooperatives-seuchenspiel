package de.uol.swp.client.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Utility class to load files and read their content.
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public final class FileLoader {
    public static final String IMAGES_PATH_PREFIX = "images/";

    /**
     * Interface representing a {@link Function} that throws a {@link Throwable}.
     *
     * @param <T> type given to the {@link Function}
     * @param <U> returned type
     * @param <E> the type of {@link Throwable}
     */
    private interface ThrowingFunction<T, U, E extends Throwable> {
        U apply(T t) throws E;
    }

    /**
     * Reads an image file and returns a {@link File} object.
     *
     * @param imageUrl url of the image file to read
     * @return {@link File} object of the image
     */
    public static File readImageFile(final String imageUrl) {
        return readFile(IMAGES_PATH_PREFIX + imageUrl);
    }

    /**
     * Reads a file and returns a {@link File} object.
     *
     * @param url url of the file to read
     * @return {@link File} object for given url
     */
    public static File readFile(final String url) {
        return readFileAndExecuteWithInputStream(
                url,
                is -> {
                    final File tempFile = File.createTempFile("tempResource", ".tmp");
                    tempFile.deleteOnExit();
                    try (final FileOutputStream fos = new FileOutputStream(tempFile)) {
                        fos.write(is.readAllBytes());
                    }
                    return tempFile;
                },
                null
        );
    }

    /**
     * Reads a given url as a file and returns its text content as string.
     *
     * @param url url of the file to read
     * @return text content of the file
     */
    public static String readFileAsString(final String url) {
        return readFileAndExecuteWithInputStream(
                url,
                is -> new String(is.readAllBytes(), StandardCharsets.UTF_8),
                ""
        );
    }

    /**
     * Reads a file and converts it to an {@link InputStream} and executes given {@code transformer} with the given {@link InputStream}.
     * Returns a {@code defaultValue} if any {@link Exception} occurs.
     *
     * @param url url of the file to read
     * @param transformer {@link ThrowingFunction} to execute with the read {@link InputStream}
     * @param defaultValue value returned when an {@link Exception} occurs
     * @return value returned by given {@code transformer} or {@code defaultValue}
     * @param <T> returned type
     */
    private static <T> T readFileAndExecuteWithInputStream(final String url,
                                                           final ThrowingFunction<InputStream, T, Exception> transformer,
                                                           final T defaultValue) {
        try (final InputStream is = FileLoader.class.getClassLoader().getResourceAsStream(url)) {
            return transformer.apply(is);
        } catch (final Exception e) {
            return defaultValue;
        }
    }
}