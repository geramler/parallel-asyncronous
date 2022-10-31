package com.learnjava.thread;

import com.learnjava.domain.Product;
import com.learnjava.domain.ProductInfo;
import com.learnjava.domain.Review;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ProductService;
import com.learnjava.service.ReviewService;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

public class ProductServiceUsingThread {

    private ProductInfoService productInfoService;
    private ReviewService reviewService;

    public ProductServiceUsingThread(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public Product retrieveProductDetails(String productId) throws InterruptedException {
        stopWatch.start();
        /*
        ProductInfo productInfo = productInfoService.retrieveProductInfo(productId); // blocking call
        Review review = reviewService.retrieveReviews(productId); // blocking call
        */

        ProductInfoRunnable productInfoRunnable = new ProductInfoRunnable(productId);
        ProductReviewRunnable productReviewRunnable = new ProductReviewRunnable(productId);

        Thread productInfoThread = new Thread(productInfoRunnable);
        Thread productReviewThread =  new Thread(productReviewRunnable);

        productInfoThread.start();
        productReviewThread.start();

        // joins current thread
        productInfoThread.join();
        productReviewThread.join();

        ProductInfo productInfo = productInfoRunnable.getProductInfo();
        Review productReview = productReviewRunnable.getReview();

        stopWatch.stop();
        log("Total Time Taken : "+ stopWatch.getTime());
        return new Product(productId, productInfo, productReview);
    }

    public static void main(String[] args) {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductService productService = new ProductService(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        log("Product is " + product);

    }

    private class ProductInfoRunnable implements Runnable {
        private String productId;

        private ProductInfo productInfo;

        public ProductInfo getProductInfo() {
            return productInfo;
        }

        public ProductInfoRunnable(String productId) {
            this.productId = productId;
        }

        @Override
        public void run() {
            this.productInfo = productInfoService.retrieveProductInfo(productId);
        }
    }

    private class ProductReviewRunnable implements Runnable {
        private String productId;

        private Review review;

        public Review getReview() {
            return review;
        }

        public ProductReviewRunnable(String productId) {
            this.productId = productId;
        }

        @Override
        public void run() {
            this.review = reviewService.retrieveReviews(productId);
        }
    }
}
