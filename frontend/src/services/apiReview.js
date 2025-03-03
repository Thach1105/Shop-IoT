import axios from "axios";
import config from "../configs/config";
import axiosInstance from "../configs/axiosInstance";

const url = config.url_Backend;

export async function getReviews(productId, number, size, rating) {
  const reqURL = `${url}/reviews/forProductByRating/${productId}?${
    number ? `number=${number}` : ""
  }${size ? `&size=${size}` : ""}${rating ? `&rating=${rating}` : ""}`;
  const response = await axios.get(reqURL);

  return response;
}

export async function getOverallReviews(productId) {
  const response = await axios.get(`${url}/reviews/overall/${productId}`);

  return response;
}

export async function getMyReviewForProduct(productId) {
  const response = await axiosInstance.get(
    `/reviews/forProductByUser/${productId}`
  );

  return response;
}

export async function createReview(review) {
  const response = await axiosInstance.post(`/reviews`, review);
  return response;
}

export async function updateReview(review, reviewId) {
  const response = await axiosInstance.put(
    `/reviews/update/${reviewId}`,
    review
  );
  return response;
}
