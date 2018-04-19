package com.dragons.aurora.model;

public class ReviewBuilder {

    public static Review build(com.dragons.aurora.playstoreapiv2.Review reviewProto) {
        Review review = new Review();
        review.setComment(reviewProto.getComment());
        review.setTitle(reviewProto.getTitle());
        review.setRating(reviewProto.getStarRating());
        review.setUserName(reviewProto.getAuthor2().getName());
        review.setUserPhotoUrl(reviewProto.getAuthor2().getUrls().getUrl());
        return review;
    }
}
