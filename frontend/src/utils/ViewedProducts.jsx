import { formatNumber } from "./format";
import { useUserState } from "../provider/UserContext";
import { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faAngleLeft, faAngleRight } from "@fortawesome/free-solid-svg-icons";
import { Link } from "react-router-dom";

function ViewedProducts() {
  const products = useUserState().viewedProducts || [];
  const [currentIndex, setCurrentIndex] = useState(0);
  const maxVisible = 6;

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex((prevIndex) => prevIndex - 1);
    }
  };

  const handleNext = () => {
    if (currentIndex < products.length - maxVisible) {
      setCurrentIndex((prevIndex) => prevIndex + 1);
    }
  };

  const visibleProducts = products.slice(
    currentIndex,
    currentIndex + maxVisible
  );

  return (
    products.length > 0 && (
      <div className="relative bg-gradient-to-r from-green-800 to-blue-500 p-4 rounded-lg mt-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl text-right text-white font-bold">
            Sản phẩm đã xem
          </h2>
          <a href="#" className="text-white hover:underline hover:italic">
            Xem tất cả &gt;
          </a>
        </div>

        {/* Hiển thị danh sách sản phẩm */}
        <div className="relative overflow-hidden">
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 transition-transform duration-500 ease-in-out">
            {visibleProducts.map((product, index) => (
              <Link
                key={index}
                to={`/san-pham/${product.slug}`}
                className="border p-4 rounded-lg bg-white hover:shadow-lg hover:scale-105 transform transition"
              >
                <div className="relative">
                  <div className="flex items-center justify-center">
                    <img
                      src={product.image_url}
                      alt={product.name}
                      className="h-40 w-fit object-cover mb-4 cursor-pointer"
                    />
                  </div>
                  <div className="absolute top-2 right-2 bg-blue-500 text-white text-xs px-2 py-1 rounded">
                    GIẢM {product.discountPercentage}%
                  </div>
                </div>
                <h3 className="text-sm font-semibold mb-2">{product.name}</h3>
                <div className="text-red-500 text-lg font-bold mb-1">
                  {formatNumber(product.cost)} đ
                </div>
                <div className="text-gray-500 text-sm line-through">
                  {formatNumber(product.price)} đ
                </div>
              </Link>
            ))}
          </div>

          {/* Nút điều hướng */}
          {products.length > maxVisible && (
            <>
              <button
                onClick={handlePrev}
                disabled={currentIndex === 0}
                className={`absolute left-0 top-1/2 -translate-y-1/2 bg-gray-500/20 text-white px-3 py-4 rounded hover:bg-opacity-75 ${
                  currentIndex === 0 ? "opacity-50 cursor-not-allowed" : ""
                }`}
              >
                <FontAwesomeIcon icon={faAngleLeft} />
              </button>

              <button
                onClick={handleNext}
                disabled={currentIndex >= products.length - maxVisible}
                className={`absolute right-0 top-1/2 -translate-y-1/2 bg-gray-500/20 text-white px-3 py-4 rounded hover:bg-opacity-75 ${
                  currentIndex >= products.length - maxVisible
                    ? "opacity-50 cursor-not-allowed"
                    : ""
                }`}
              >
                <FontAwesomeIcon icon={faAngleRight} />
              </button>
            </>
          )}
        </div>
      </div>
    )
  );
}

export default ViewedProducts;
