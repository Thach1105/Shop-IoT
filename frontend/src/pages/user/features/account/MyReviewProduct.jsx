/* eslint-disable react/prop-types */
import {
  Alert,
  Box,
  Modal,
  Rating,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import {
  createReview,
  getMyReviewForProduct,
  updateReview,
} from "../../../../services/apiReview";

const style = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 600,
  bgcolor: "background.paper",
  border: "2px solid #000",
  boxShadow: 24,
  p: 4,
};
function MyReviewProduct({ selectedProductRv, setSelectedProductRV }) {
  const [rating, setRating] = useState(3);
  const [comment, setComment] = useState("");
  const [review, setReview] = useState();
  const [errMessage, setErrMessage] = useState();

  useEffect(() => {
    async function fetchDate() {
      const response = await getMyReviewForProduct(selectedProductRv.productId);
      const { data } = response;
      console.log(response);
      if (data?.content) {
        setRating(data.content.rating);
        setComment(data.content.comment);
        setReview(data.content);
      } else {
        setRating(0);
        setComment("");
        setReview();
      }
    }

    fetchDate();
  }, [selectedProductRv]);

  const handleCreateReview = async () => {
    const review = {
      comment,
      rating,
      productId: selectedProductRv.productId,
    };
    try {
      const response = await createReview(review);
      setErrMessage("");
      console.log(response);
    } catch (error) {
      console.log(error);
      const { status } = error;
      if (status === 400) {
        const { response } = error;
        const { data } = response;
        const { message } = data;
        if (message === "Comment must not be empty") {
          setErrMessage("Bạn phải viết phần đánh giá của bản thân");
        }
        if (
          message === "Rating must not be null" ||
          message === "You have rated this product"
        ) {
          setErrMessage("Bạn cần đánh giá số sao cho sản phẩm này");
        }
      } else {
        setErrMessage("Đã xảy ra lỗi");
      }
    }
  };

  const handleUpdateReview = async () => {
    const postReview = {
      comment,
      rating,
      productId: selectedProductRv.productId,
    };
    console.log(postReview);
    try {
      const response = await updateReview(postReview, review.id);
      console.log(response);
      setErrMessage("");
    } catch (error) {
      console.log(error);
      const { status } = error;
      if (status === 400) {
        const { response } = error;
        const { data } = response;
        const { message } = data;
        if (message === "Comment must not be empty") {
          setErrMessage("Bạn phải viết phần đánh giá của bản thân");
        }
        if (
          message === "Rating must not be null" ||
          message === "You have rated this product"
        ) {
          setErrMessage("Bạn cần đánh giá số sao cho sản phẩm này");
        }
      } else {
        setErrMessage("Đã xảy ra lỗi");
      }
    }
  };
  return (
    <Modal
      open={selectedProductRv !== undefined && selectedProductRv !== null}
      onClose={() => setSelectedProductRV()}
      aria-labelledby="modal-modal-title"
      aria-describedby="modal-modal-description"
    >
      <Box sx={style}>
        <Typography id="modal-modal-title" variant="h5" component="h2">
          Đánh giá sản phẩm
        </Typography>
        {review && (
          <p className="text-blue-500 text-sm italic">
            Bạn đã đánh giá sản phẩm này trước đây
          </p>
        )}
        <div className="flex justify-start items-center gap-2 mt-2">
          <img src={selectedProductRv?.imageUrl} className="h-16 w-fit" />
          <p>{selectedProductRv?.productName}</p>
        </div>
        {errMessage && (
          <div className="flex justify-center m-3 items-center">
            <Alert severity="error">{errMessage}</Alert>
          </div>
        )}
        <div className="flex flex-col justify-center items-center">
          <Rating
            name="size-large"
            defaultValue={rating}
            value={rating}
            onChange={(event, newValue) => {
              setRating(newValue);
            }}
            size="large"
            className="mb-2"
          />
          <TextField
            id="filled-textarea"
            label="Đánh giá của bạn"
            placeholder="Hãy viết đánh giá của bạn"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            multiline
            variant="filled"
            className="w-full"
          />

          {!review ? (
            <button
              onClick={handleCreateReview}
              className="w-fit border-2 mt-3 px-2 py-1 bg-sky-400 text-white hover:bg-sky-700 hover:font-semibold"
            >
              Đánh giá
            </button>
          ) : (
            <button
              onClick={handleUpdateReview}
              className="w-fit border-2 mt-3 px-2 py-1 bg-sky-400 text-white hover:bg-sky-700 hover:font-semibold"
            >
              Cập nhật
            </button>
          )}
        </div>
      </Box>
    </Modal>
  );
}

export default MyReviewProduct;
