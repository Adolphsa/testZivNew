package com.yeejay.yplay.model;

import com.tencent.imsdk.TIMImageType;

/**
 * 图片信息
 * Created by Adolph on 2018/1/2.
 */

public class ImageInfo {

    private int imageFormat;
    private OriginalImage originalImage;
    private ThumbImage thumbImage;
    private LargeImage largeImage;

    public int getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(int imageFormat) {
        this.imageFormat = imageFormat;
    }

    public OriginalImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(OriginalImage originalImage) {
        this.originalImage = originalImage;
    }

    public ThumbImage getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(ThumbImage thumbImage) {
        this.thumbImage = thumbImage;
    }

    public LargeImage getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(LargeImage largeImage) {
        this.largeImage = largeImage;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "originalImage=" + originalImage +
                ", thumbImage=" + thumbImage +
                ", largeImage=" + largeImage +
                '}';
    }


    public static class OriginalImage{

        private TIMImageType imageType;
        private int imageWidth;
        private int imageHeight;
        private String imageUrl;
        private int imageSize;


        public TIMImageType getImageType() {
            return imageType;
        }

        public void setImageType(TIMImageType imageType) {
            this.imageType = imageType;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        @Override
        public String toString() {
            return "OriginalImage{" +
                    "imageType=" + imageType +
                    ", imageWidth=" + imageWidth +
                    ", imageHeight=" + imageHeight +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", imageSize=" + imageSize +
                    '}';
        }
    }

    public static class ThumbImage{

        private TIMImageType imageType;
        private int imageWidth;
        private int imageHeight;
        private String imageUrl;
        private int imageSize;

        public TIMImageType getImageType() {
            return imageType;
        }

        public void setImageType(TIMImageType imageType) {
            this.imageType = imageType;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        @Override
        public String toString() {
            return "ThumbImage{" +
                    "imageType=" + imageType +
                    ", imageWidth=" + imageWidth +
                    ", imageHeight=" + imageHeight +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", imageSize=" + imageSize +
                    '}';
        }
    }

    public static class LargeImage{

        private TIMImageType imageType;
        private int imageWidth;
        private int imageHeight;
        private String imageUrl;
        private int imageSize;

        public TIMImageType getImageType() {
            return imageType;
        }

        public void setImageType(TIMImageType imageType) {
            this.imageType = imageType;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHright) {
            this.imageHeight = imageHright;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        @Override
        public String toString() {
            return "LargeImage{" +
                    "imageType=" + imageType +
                    ", imageWidth=" + imageWidth +
                    ", imageHeight=" + imageHeight +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", imageSize=" + imageSize +
                    '}';
        }
    }
}
