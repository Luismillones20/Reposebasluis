package org.e2e.labe2e02.review.dto;

import lombok.Data;

@Data
public class NewReviewDto {
    private String comment;
    private Integer rating;
    private Long rideId;
    private Long targetId;
}