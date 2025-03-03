import { Link } from "react-router-dom";
import { formatNumber } from "../../../../utils/format";
import PropTypes from "prop-types";

function ProductList({ products }) {
  return (
    <div className="grid grid-cols-3 gap-4">
      {products.map((product, index) => (
        <div
          key={index}
          className="bg-white p-4 shadow-md mt-2 border-gray-200 border-2 hover:shadow-2xl"
        >
          <div className="flex justify-between items-center mb-2">
            <label className="text-gray-600">
              <input type="checkbox" className="mr-2" />
              So sánh
            </label>
            <span className="bg-gradient-to-r from-lime-500 to-green-500 text-white text-sm px-2 py-1 rounded font-semibold">
              GIẢM {product.discountPercentage}%
            </span>
          </div>
          <Link
            to={`/san-pham/${product.slug}`}
            state={product}
            className="cursor-pointer"
          >
            <img
              src={product.image_url}
              alt="Product Image"
              className="mb-2 h-72 w-fit"
            />
          </Link>
          <h2 className="text-red-500 font-bold text-lg mb-1">
            {formatNumber(product.cost)} đ
          </h2>
          <p className="text-gray-500 line-through mb-2">
            {formatNumber(product.price)} đ
          </p>
          <Link
            to={`/san-pham/${product.slug}`}
            state={product}
            className="cursor-pointer"
          >
            <p className="text-gray-700 font-semibold">{product.name}</p>
          </Link>
        </div>
      ))}
    </div>
  );
}

ProductList.propTypes = {
  products: PropTypes.arrayOf(
    PropTypes.shape({
      slug: PropTypes.string.isRequired,
      image_url: PropTypes.string.isRequired,
      cost: PropTypes.number.isRequired,
      price: PropTypes.number.isRequired,
      name: PropTypes.string.isRequired,
      discountPercentage: PropTypes.number.isRequired,
    })
  ).isRequired,
};

export default ProductList;
