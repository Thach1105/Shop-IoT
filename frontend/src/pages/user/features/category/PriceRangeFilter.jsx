import PropTypes from "prop-types";
import { useState } from "react";

function PriceRangeFilter({ handleChangePriceRange }) {
  const [min, setMin] = useState();
  const [max, setMax] = useState();

  return (
    <div>
      <h2 className="font-bold text-lg mt-6 mb-4">GIÁ</h2>
      <ul className="space-y-2 text-gray-700">
        <li>
          <button
            onClick={() => handleChangePriceRange(0, 200000)}
            className="hover:underline bg-gray-200 py-1 px-2 rounded-lg"
          >
            Dưới 200.000
          </button>
        </li>
        <li>
          <button
            onClick={() => handleChangePriceRange(200000, 500000)}
            className="hover:underline bg-gray-200 py-1 px-2 rounded-lg"
          >
            Từ 200.000 đến 500.000
          </button>
        </li>
        <li>
          <button
            onClick={() => handleChangePriceRange(500000, 1000000)}
            className="hover:underline bg-gray-200 py-1 px-2 rounded-lg"
          >
            Từ 500.000 đến 1.000.000
          </button>
        </li>
        <li>
          <button
            onClick={() => handleChangePriceRange(1000000, 100000000)}
            className="hover:underline bg-gray-200 py-1 px-2 rounded-lg"
          >
            Trên 1.000.000
          </button>
        </li>
      </ul>

      <div className="mt-2">
        <p className="my-2 text-stone-400 text-base">Chọn khoảng giá</p>
        <div className="flex justify-evenly items-center">
          <input
            type="number"
            placeholder="Từ"
            value={min}
            onChange={(e) => setMin(Number(e.target.value))}
            className="border-2 rounded-md p-2 w-28 text-stone-500 text-right"
          ></input>
          <span className="text-stone-500">-</span>
          <input
            type="number"
            value={max}
            placeholder="Đến"
            onChange={(e) => setMax(Number(e.target.value))}
            className="border-2 rounded-md p-2 w-28 text-stone-500 text-right"
          ></input>
        </div>

        <div className="w-full text-center">
          <button
            onClick={() =>
              handleChangePriceRange(Number(min) || 0, Number(max) || 100000000)
            }
            className="bg-blue-600 text-white border-blue-600 border-2 px-3 py-2 mt-3 w-1/2 cursor-pointer hover:bg-blue-700"
            disabled={max === 0 || max < min}
          >
            Áp dụng
          </button>
        </div>
      </div>
    </div>
  );
}

PriceRangeFilter.propTypes = {
  handleChangePriceRange: PropTypes.func.isRequired,
  // maxPrice: PropTypes.number.isRequired,
  // minPrice: PropTypes.number.isRequired,
};

export default PriceRangeFilter;
