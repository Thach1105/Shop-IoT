import { faStar } from "@fortawesome/free-regular-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, ButtonGroup, Rating } from "@mui/material";
import { useEffect, useState } from "react";
import { useLoaderData } from "react-router-dom";
import { formatDateTime, formatNumber } from "../../../../utils/format";
// import ReactQuill from "react-quill";
// import "react-quill/dist/quill.snow.css";
import loadable from "@loadable/component";
import { getOverallReviews, getReviews } from "../../../../services/apiReview";
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";

function convertDetailToArray(details) {
  return Object.keys(details).map((option) => ({
    option,
    value: details[option],
  }));
}

function ProductDetail() {
  const productInfo = useLoaderData();
  const options = convertDetailToArray(productInfo.productDetails);
  const [btnValue, setBtnValue] = useState(1);

  console.log(productInfo);

  return (
    <div className="container mx-auto p-2">
      <h1 className="text-2xl font-bold mb-4">Chi tiết sản phẩm</h1>
      <div className="bg-white p-4 rounded-lg shadow-lg flex-col mb-6">
        <div className="grid grid-cols-2 gap-3">
          <div className="col-span-1">
            <div className="flex flex-col items-center">
              <img
                src={productInfo.image_url}
                alt="Product image"
                className="mb-6"
              />
            </div>
          </div>

          <div className="px-2">
            <h1 className="text-3xl font-bold">{productInfo.name}</h1>
            <div className="flex items-center mt-2">
              <a
                onClick={() => setBtnValue(1)}
                href="#review"
                className="ml-2 text-sm text-gray-500"
              >
                Xem tất cả đánh giá
              </a>
            </div>
            <div className="flex items-center mt-4">
              <span className="text-gray-400 line-through">
                {`${formatNumber(productInfo.price)} VND`}
              </span>
              <span className="text-3xl font-bold text-green-500 ml-4">
                {`${formatNumber(productInfo.cost)} VND`}
              </span>
              <span className="bg-orange-500 text-white text-sm font-bold ml-2 px-2 py-1 rounded">
                {`-${productInfo.discountPercentage}%`}
              </span>
            </div>

            <div className="mt-6">
              <div className="flex">
                <span className="font-bold">BRAND:</span>
                <span className="ml-2">{productInfo.brand?.name}</span>
              </div>
              <div className="flex mt-2">
                <span className="font-bold">SKU:</span>
                <span className="ml-2">{productInfo.sku}</span>
              </div>
              <div className="flex mt-2">
                <span className="font-bold">CATEGORY:</span>
                <span className="ml-2">{productInfo.category?.name}</span>
              </div>
            </div>
            <p className="text-gray-600 my-4">{productInfo.shortDescription}</p>
          </div>
        </div>
      </div>

      <ButtonGroup
        className="mb-4"
        variant="contained"
        aria-label="Basic button group"
      >
        <Button onClick={() => setBtnValue(1)}>Đánh giá</Button>
        <Button onClick={() => setBtnValue(2)}>Mô tả</Button>
        <Button onClick={() => setBtnValue(3)}>Thông số kỹ thuật</Button>
      </ButtonGroup>

      {btnValue === 1 ? (
        <Reviews productId={productInfo.id} />
      ) : btnValue === 2 ? (
        <Descriptions description={productInfo.longDescription} />
      ) : (
        <Specifications options={options} />
      )}
    </div>
  );
}

// eslint-disable-next-line react/prop-types
function Reviews({ productId }) {
  const [overall, setOverall] = useState();
  const [pageNum, setPageNum] = useState(1);
  const [reviews, setReviews] = useState([]);
  const [pageDetail, setPageDetail] = useState();
  console.log(pageNum);

  useEffect(() => {
    async function fetchData() {
      try {
        const response = await getOverallReviews(productId);
        const { data } = response;
        setOverall(data?.content);
      } catch (error) {
        console.log(error);
      }
    }

    fetchData();
  }, [productId]);

  useEffect(() => {
    async function fetchReviews() {
      try {
        const response = await getReviews(productId, pageNum, 3, null);
        const { data } = response;

        console.log(data);
        setReviews(data?.content);
        setPageDetail(data?.pageDetails);
      } catch (error) {
        console.log(error);
      }
    }

    fetchReviews();
  }, [productId, pageNum]);

  function handleIncPageNum() {
    if (pageDetail.hasNext) {
      setPageNum((pageNum) => pageNum + 1);
    }
  }

  function handleDecPageNum() {
    if (pageDetail.hasPrevious) {
      setPageNum((pageNum) => pageNum - 1);
    }
  }

  return (
    <div id="review" className="bg-white p-4 rounded-lg shadow-lg">
      <div className="flex">
        {overall && (
          <div className="w-1/2 pr-6">
            <div className="text-4xl font-bold">
              {overall?.averageRating ?? 0}
            </div>
            <div className="text-gray-600">
              based on {overall.totalReviews} ratings
            </div>
            <Rating
              name="half-rating-read"
              defaultValue={overall?.averageRating}
              precision={0.01}
              size="large"
              readOnly
            />
            <div className="space-y-2">
              {[
                {
                  stars: 5,
                  count: overall.detail?._5Star ?? 0,
                  color: "bg-green-400",
                },
                {
                  stars: 4,
                  count: overall.detail?._4Star ?? 0,
                  color: "bg-blue-400",
                },
                {
                  stars: 3,
                  count: overall.detail?._3Star ?? 0,
                  color: "bg-yellow-400",
                },
                {
                  stars: 2,
                  count: overall.detail?._2Star ?? 0,
                  color: "bg-red-400",
                },
                {
                  stars: 1,
                  count: overall.detail?._1Star ?? 0,
                  color: "bg-pink-400",
                },
              ].map((rating, index) => (
                <div key={index} className="flex items-center">
                  <div className="w-8 text-gray-600">
                    {rating.stars} <FontAwesomeIcon icon={faStar} />
                  </div>
                  <div className="flex-1 mx-2 bg-gray-200 rounded-full">
                    <div
                      className={`${rating.color} h-2`}
                      style={{
                        width: `${
                          (rating.count / overall.totalReviews) * 100
                        }%`,
                      }}
                    ></div>
                  </div>
                  <div className="w-12 text-gray-600">{rating.count}</div>
                </div>
              ))}
            </div>
          </div>
        )}

        {reviews && (
          <div className="w-1/2">
            {reviews.map((review, index) => (
              <div
                key={index}
                className="mb-6 p-4 bg-gray-50 rounded-lg shadow"
              >
                <div className="flex items-center mb-2">
                  <div className="w-10 h-10 bg-gray-300 rounded-full mr-4"></div>
                  <div>
                    <div className="font-bold">{review.user.fullName}</div>
                    <div className="text-gray-600 text-xs">
                      {formatDateTime(review.createdAt)}
                    </div>
                  </div>
                </div>
                <div className="text-gray-700 mb-2 text-sm">
                  {review.comment}
                </div>
                <div className="text-yellow-500 mb-2">
                  {Array.from({ length: review.rating }, (_, i) => (
                    <i key={i} className="fas fa-star"></i>
                  ))}
                </div>
              </div>
            ))}

            {pageDetail && (
              <div className="text-center">
                <button
                  className="py-2 px-3 border-2 rounded-lg hover:text-blue-600"
                  onClick={handleDecPageNum}
                >
                  <FontAwesomeIcon icon={faChevronLeft} />
                </button>
                <button
                  className="py-2 px-3 border-2 rounded-lg hover:text-blue-600"
                  onClick={handleIncPageNum}
                >
                  <FontAwesomeIcon icon={faChevronRight} />
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

const ReactQuill = loadable(() => import("react-quill"), {
  ssr: false, // Đảm bảo không render phía server
  fallback: <div>Loading Editor...</div>,
});
import "react-quill/dist/quill.snow.css";
// eslint-disable-next-line react/prop-types
function Descriptions({ description }) {
  const modules = {
    toolbar: false, // Không hiển thị toolbar cho chế độ xem
  };

  const formats = [
    "header",
    "bold",
    "italic",
    "underline",
    "strike",
    "blockquote",
    "list",
    "bullet",
    "link",
    "color",
    "background",
    "align",
    "size",
    "font",
  ];

  return (
    <div className="bg-white p-4 rounded-lg shadow-lg">
      <ReactQuill
        value={description}
        readOnly={true}
        //theme="snow"
        modules={modules}
        formats={formats}
      />
    </div>
  );
}

// eslint-disable-next-line react/prop-types
function Specifications({ options }) {
  const formatText = (text) => {
    return text.split("\n").map((line, index) => (
      <span key={index}>
        {line}
        <br />
      </span>
    ));
  };
  return (
    <div className="justify-center items-center py-4">
      <div className="bg-white rounded-lg shadow-md p-6 flex-1 ">
        <h2 className="text-lg font-bold mb-4 underline">
          Thông số kỹ thuật sản phẩm
        </h2>
        <div className="space-y-2">
          {/*  eslint-disable-next-line react/prop-types */}
          {options.map((otp, index) => (
            <div
              key={index}
              className={`flex justify-between ${
                index % 2 || "bg-gray-100"
              } p-2 rounded`}
            >
              <span>{otp.option}:</span>
              <div className="text-right">{formatText(otp.value)}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default ProductDetail;
