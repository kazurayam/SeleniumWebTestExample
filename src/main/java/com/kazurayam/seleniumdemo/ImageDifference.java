package com.kazurayam.seleniumdemo;

import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * accepts 2 BufferedImages as input, compare them, make a difference image,
 * and calcurate the ratio of difference of the 2 input images.
 */
class ImageDifference {

    private BufferedImage expectedImage_ = null;
    private BufferedImage actualImage_ = null;
    private BufferedImage diffImage_ = null;
    private Double ratio_ = 0.0;        // percentage
    private Double criteria_ = 1.0;     // percentage

    ImageDifference(BufferedImage expected, BufferedImage actual)
    {
        Objects.requireNonNull(expected);
        Objects.requireNonNull(actual);
        expectedImage_ = expected;
        actualImage_ = actual;
        ImageDiff imgDiff = makeImageDiff(expectedImage_, actualImage_);
        ratio_ = calculateRatioPercent(imgDiff);
        diffImage_ = imgDiff.getMarkedImage();
    }

    private ImageDiff makeImageDiff(BufferedImage expected, BufferedImage actual)
    {
        Screenshot expectedScreenshot = new Screenshot(expected);
        Screenshot actualScreenshot = new Screenshot(actual);
        ImageDiff imgDiff = new ImageDiffer().makeDiff(expectedScreenshot, actualScreenshot);
        return imgDiff;
    }

    BufferedImage getExpectedImage() {
        return expectedImage_;
    }

    BufferedImage getActualImage() { return actualImage_; }

    BufferedImage getDiffImage() {
        return diffImage_;
    }

    void setCriteria(Double criteria) {
        criteria_ = criteria;
    }

    Double getCriteria() {
        return criteria_;
    }

    /**
     *
     * @return e.g. 0.23% or 90.0%
     */
    Double getRatio() {
        return ratio_;
    }

    /**
     * @return e.g. "0.23" or "90.00"
     */
    String getRatioAsString() {
        return this.getRatioAsString("%1$.2f");
    }

    String getRatioAsString(String fmt) {
        return String.format(fmt, this.getRatio());
    }

    /**
     *
     * Round up 0.0001 to 0.01
     *
     * @param diff
     * @return
     */
    private Double calculateRatioPercent(ImageDiff diff) {
        boolean hasDiff = diff.hasDiff();
        if (!hasDiff) {
            return 0.0;
        }
        int diffSize = diff.getDiffSize();
        int area = diff.getMarkedImage().getWidth() * diff.getMarkedImage().getHeight();
        Double diffRatio = new Double(diffSize * 100) / area;
        BigDecimal bd = new BigDecimal(diffRatio);
        BigDecimal bdUP = bd.setScale(2, BigDecimal.ROUND_UP);  // 0.001 -> 0.01
        return bdUP.doubleValue();
    }


    /**
     * @return true if the expected image and the actual image pair has
     *         greater difference than the criteria = these are different enough,
     *         otherwise false.
     */
    Boolean imagesAreDifferent() {
        return (ratio_ > criteria_);
    }

    /**
     * @return true if the expected image and the actual image pair has
     *         smaller difference than the criteria = these are similar enough,
     *         otherwise false.
     */
    Boolean imagesAreSimilar() {
        return (ratio_ <= criteria_);
    }
}