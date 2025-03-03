import { useEffect, useState } from "react";
import { faAngleLeft, faAngleRight } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { getPopularProduct } from "../../../../services/apiProduct";
import { formatNumber } from "../../../../utils/format";
import { Link } from "react-router-dom";

function PopularProducts() {
  const [products, setProducts] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const maxVisible = 6;

  useEffect(() => {
    const fetchdData = async () => {
      const response = await getPopularProduct(24);
      const { data } = response;
      setProducts(data.content);
    };
    fetchdData();
  }, []);

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex((prevIndex) => prevIndex - 6);
    }
  };

  const handleNext = () => {
    if (currentIndex < products.length - maxVisible) {
      setCurrentIndex((prevIndex) => prevIndex + 6);
    }
  };

  const visibleProducts = products.slice(
    currentIndex,
    currentIndex + maxVisible
  );

  return (
    products && (
      <div className="relative bg-gradient-to-r from-green-800 to-blue-500 p-4 rounded-lg mt-6">
        <div className="flex justify-between items-center mb-2">
          <h2 className="text-xl text-right font-bold text-white border-b-4 border-y-emerald-500 border-spacing-y-4">
            Sản phẩm được ưa chuộng
          </h2>
          <a href="#" className="text-white hover:underline hover:italic">
            Xem tất cả &gt;
          </a>
        </div>
        <p className="mb-4 text-emerald-200 font-semibold">
          Các thiết bị được ưu chuộng nhất tại Shop IoT,
          {/* <br /> */}
          nâng cao trải nghiệm cuộc sống của khách hàng
        </p>
        <div className="relative">
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {visibleProducts.map((product, index) => (
              <Link
                to={`/san-pham/${product.slug}`}
                key={index}
                className="cursor-pointer border p-4 rounded-lg bg-white shadow-lg relative group overflow-hidden transform transition duration-300 hover:scale-105"
              >
                <div className="relative">
                  <img
                    src={product.image_url}
                    alt={product.name}
                    className="h-40 w-full object-cover mb-4"
                  />
                  <div className="absolute top-2 right-2 bg-blue-500 text-white text-xs px-2 py-1 rounded">
                    GIẢM {product.discountPercentage}%
                  </div>
                </div>
                <h3 className="text-sm font-semibold mb-2">{product.name}</h3>
                <div className="text-red-500 text-lg font-bold mb-1">
                  {`${formatNumber(product.cost)} đ`}
                </div>
                <div className="text-gray-500 text-sm line-through">
                  {`${formatNumber(product.price)} đ`}
                </div>
              </Link>
            ))}
          </div>

          {/* Nút điều hướng */}
          {products.length > maxVisible && (
            <div className="absolute inset-0 flex items-center justify-between px-2 pointer-events-none">
              <button
                onClick={handlePrev}
                disabled={currentIndex === 0}
                className={`${
                  currentIndex === 0 ? "opacity-50 cursor-not-allowed" : ""
                } bg-gray-500/20 text-white px-3 py-4 rounded hover:bg-opacity-75 pointer-events-auto`}
              >
                <FontAwesomeIcon icon={faAngleLeft} />
              </button>

              <button
                onClick={handleNext}
                disabled={currentIndex >= products.length - maxVisible}
                className={`${
                  currentIndex >= products.length - maxVisible
                    ? "opacity-50 cursor-not-allowed"
                    : ""
                } bg-gray-500/20 text-white px-3 py-4 rounded hover:bg-opacity-75 pointer-events-auto`}
              >
                <FontAwesomeIcon icon={faAngleRight} />
              </button>
            </div>
          )}
        </div>
      </div>
    )
  );
}

export default PopularProducts;
