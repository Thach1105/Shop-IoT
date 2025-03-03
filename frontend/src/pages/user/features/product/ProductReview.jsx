import { useEffect, useState } from "react";
import { getOverallReviews, getReviews } from "../../../../services/apiReview";
import { Rating } from "@mui/material";
import PropTypes from "prop-types";
import { formatDateTime } from "../../../../utils/format";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleCheck } from "@fortawesome/free-regular-svg-icons";

function ProductReview({ productId }) {
  const [overallReview, setOverallReview] = useState();
  const [starFilters, setStarFilters] = useState(0);
  const [page, setPage] = useState(1);
  const [reviews, setReviews] = useState([]);
  const [hasNextReviews, setHasNextReviews] = useState(false);

  useEffect(() => {
    setReviews([]);
    setPage(1);
    setStarFilters(0);
    setOverallReview(null);
  }, [productId]);

  useEffect(() => {
    async function fetchOverallReview() {
      const response = await getOverallReviews(productId);
      const { data } = response;
      setOverallReview(data?.content);
    }

    fetchOverallReview();
  }, [productId]);

  useEffect(() => {
    async function fetchReviews() {
      const response = await getReviews(
        productId,
        page,
        null,
        starFilters === 0 ? null : starFilters
      );

      const { data } = response;
      const nextReviews = data?.content;
      setReviews((reviews) => {
        return [...reviews, ...nextReviews];
      });
      setHasNextReviews(data?.pageDetails?.hasNext);
    }

    fetchReviews();
  }, [productId, page, starFilters]);

  function handleChangeStarFilter(star) {
    setReviews([]);
    setStarFilters(star);
    setPage(1);
  }

  return (
    <div className="mx-auto p-4 rounded-lg">
      {overallReview && (
        <div className="flex gap-8 mb-8 border-b pb-6">
          {/* Điểm đánh giá tổng quan */}
          <div className="text-center">
            <div className="text-3xl font-bold">{`${overallReview.averageRating}/5`}</div>
            <Rating
              name="half-rating-read"
              defaultValue={overallReview.averageRating}
              precision={0.1}
              readOnly
            />
            <p className="text-blue-600">{`${overallReview.totalReviews} đánh giá`}</p>
          </div>

          {/* Chi tiết đánh giá */}
          <div className="flex-1">
            {Array.from({ length: 5 }, (_, i) => i + 1)
              .reverse()
              .map((i) => (
                <div key={i} className="flex items-center gap-2 mb-1">
                  <span>
                    {i}
                    <span className="text-yellow-400">{"★"}</span>
                  </span>
                  <div className="flex-1 h-2 bg-gray-200 rounded-full">
                    <div
                      className="h-full bg-red-600 rounded-full"
                      style={{
                        width: `${
                          overallReview.totalReviews &&
                          ((overallReview.detail[`_${i}Star`] || 0) /
                            overallReview.totalReviews) *
                            100
                        }%`,
                      }}
                    ></div>
                  </div>
                  <span>{`${
                    overallReview.detail[`_${i}Star`] || 0
                  } đánh giá`}</span>
                </div>
              ))}
          </div>
        </div>
      )}

      {/* Phần đánh giá */}
      {/* <div className="text-center mb-8">
        <p className="mb-4">Bạn đánh giá sao về sản phẩm này?</p>
        <button className="bg-red-600 text-white px-6 py-2 rounded-md">
          Đánh giá ngay
        </button>
      </div> */}

      {/* Bộ lọc */}
      <div className="mb-8">
        <h3 className="font-medium mb-4">Lọc theo</h3>
        <div className="flex flex-wrap gap-2">
          <button
            className={`px-4 py-1 rounded-full border  ${
              starFilters === 0 ? "bg-red-600 text-white" : "hover:bg-gray-100"
            }`}
            onClick={() => handleChangeStarFilter(0)}
          >
            Tất cả
          </button>
          {Array.from({ length: 5 }, (_, i) => i + 1)
            .reverse()
            .map((star) => (
              <button
                key={star}
                className={`px-4 py-1 rounded-full border  ${
                  starFilters === star
                    ? "bg-red-600 text-white"
                    : "hover:bg-gray-100"
                }`}
                onClick={() => handleChangeStarFilter(star)}
              >
                {star}
                <span className="text-yellow-500"> ★</span>
              </button>
            ))}
        </div>
      </div>

      {/* Đánh giá */}
      <div className="border-t pt-6">
        {reviews.map((review, index) => (
          <div key={index} className="flex items-start gap-4 mb-6">
            <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center text-white font-bold">
              {review.user.fullName[0]}
            </div>

            <div className="flex-1">
              <div className="flex items-center gap-2 mb-1">
                <h4 className="font-medium">{review.user.fullName}</h4>
                <span className="text-sm text-gray-500">
                  {formatDateTime(review.createdAt)}
                </span>
              </div>

              <div className="flex items-center text-sm text-green-600 mb-2">
                <svg
                  className="w-4 h-4 mr-1"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <FontAwesomeIcon icon={faCircleCheck} />
                </svg>
                Đã mua
              </div>

              <div className="mb-2">
                <Rating
                  name="read-only"
                  value={review.rating}
                  readOnly
                  size="small"
                />
              </div>
              <p className="text-gray-600">{review.comment}</p>
            </div>
          </div>
        ))}

        {hasNextReviews && (
          <button
            onClick={() => setPage((page) => page + 1)}
            className="w-full text-center"
          >
            Xem thêm...
          </button>
        )}
      </div>
    </div>
  );
}

ProductReview.propTypes = {
  productId: PropTypes.number.isRequired,
};
export default ProductReview;
