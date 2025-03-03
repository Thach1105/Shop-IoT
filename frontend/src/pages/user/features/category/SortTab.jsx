import PropTypes from "prop-types";

function SortTab({ handleChangeSortField, sortField }) {
  return (
    <div className="flex space-x-4 mb-4">
      <button
        className={`${
          sortField === ""
            ? "text-blue-500 border-b-2 border-blue-500"
            : "text-gray-600 hover:text-blue-500 hover:border-blue-500 hover:border-b-2"
        } pb-1`}
        onClick={() => handleChangeSortField("")}
      >
        Phổ biến
      </button>
      <button
        onClick={() => handleChangeSortField("discountPercentage_desc")}
        className={`${
          sortField === "discountPercentage_desc"
            ? "text-blue-500 border-b-2 border-blue-500"
            : "text-gray-600 hover:text-blue-500 hover:border-blue-500 hover:border-b-2"
        } pb-1`}
      >
        Giảm giá nhiều
      </button>
      <button
        onClick={() => handleChangeSortField("price_asc")}
        className={`${
          sortField === "price_asc"
            ? "text-blue-500 border-b-2 border-blue-500"
            : "text-gray-600 hover:text-blue-500 hover:border-blue-500 hover:border-b-2"
        } pb-1`}
      >
        Giá thấp
      </button>
      <button
        onClick={() => handleChangeSortField("price_desc")}
        className={`${
          sortField === "price_desc"
            ? "text-blue-500 border-b-2 border-blue-500"
            : "text-gray-600 hover:text-blue-500 hover:border-blue-500 hover:border-b-2"
        } pb-1`}
      >
        Giá cao
      </button>
    </div>
  );
}

SortTab.propTypes = {
  handleChangeSortField: PropTypes.func.isRequired,
  sortField: PropTypes.string.isRequired,
};

export default SortTab;
