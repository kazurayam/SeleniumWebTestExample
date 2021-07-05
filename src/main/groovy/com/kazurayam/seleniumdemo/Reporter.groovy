package com.kazurayam.seleniumdemo

import groovy.xml.MarkupBuilder

import javax.imageio.ImageIO
import java.awt.image.RenderedImage;
import java.nio.file.Path;
import org.apache.commons.codec.digest.DigestUtils


class Reporter {

    private Path reportDir
    private List<ImageDifference> imgDifferences

    Reporter(Path reportDir) {
        this.reportDir = reportDir
        imgDifferences = new ArrayList<ImageDifference>()
    }

    void add(ImageDifference imageDifference) {
        imgDifferences.add(imageDifference)
    }

    void report() {
        def writer = new StringWriter()
        def markup = new MarkupBuilder(writer)
        markup.html {
            body {
                for (imgDifference in imgDifferences) {
                    table {
                        thead {
                            tr {
                                th("expected")
                                th("diff: " + imgDifference.getRatioAsString() + "%")
                                th("actual")
                            }
                        }
                        tbody {
                            tr {
                                td {
                                    Path expectedPath = saveImageIntoSHA1File(imgDifference.getExpectedImage(), "png", reportDir)
                                    img(src: "./" + expectedPath.getFileName().toString(),
                                            alt: "expected")
                                    /*
                                    img(src: "data:image/png;base64," +
                                            imgToBase64String(imgDifference.getExpectedImage(), 'png'),
                                            alt: "expected")
                                     */
                                }
                                td {
                                    Path diffPath = saveImageIntoSHA1File(imgDifference.getDiffImage(), "png", reportDir)
                                    img(src: "./" + diffPath.getFileName().toString(),
                                            alt: "diff")
                                    /*
                                    img(src: "data:image/png;base64," +
                                            imgToBase64String(imgDifference.getDiffImage(), 'png'),
                                            alt: "diff")
                                     */
                                }
                                td {
                                    Path actualPath = saveImageIntoSHA1File(imgDifference.getActualImage(), "png", reportDir)
                                    img(src: "./" + actualPath.getFileName().toString(),
                                            alt: "actual")
                                    /*
                                    img(src: "data:image/png:base64," +
                                            imgToBase64String(imgDifference.getActualImage(), 'png'),
                                            alt: "actual")
                                     */
                                }
                            }
                        }
                    }
                }
            }
        }
        Path html = reportDir.resolve("report.html")
        html.toFile().text = writer.toString()
    }

    /**
     *
     * @param img
     * @param formatName
     * @return
     */
    static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, formatName, os)
            return Base64.getEncoder().encodeToString(os.toByteArray())
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe)
        }
    }

    static Path saveImageIntoSHA1File(final RenderedImage img, final String formatName, final Path dir) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream()
        try {
            ImageIO.write(img, formatName, os)
            String fileName = DigestUtils.sha1Hex(os.toByteArray())
            Path filePath = dir.resolve(fileName + '.' + formatName).normalize()
            FileOutputStream fos = new FileOutputStream(filePath.toFile())
            fos.write(os.toByteArray())
            fos.flush()
            fos.close()
            return filePath
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe)
        }
    }
}