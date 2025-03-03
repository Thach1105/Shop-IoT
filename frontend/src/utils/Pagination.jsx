/* eslint-disable react/prop-types */
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

function Pagination({
  handleIncPage,
  handleDecPage,
  totalElements,
  handleChangePageSize,
  pageSize,
}) {
  return (
    <div className="px-5 py-5 bg-white border-t flex flex-col xs:flex-row items-center xs:justify-between">
      <span className="text-xs xs:text-sm text-gray-900">
        Rows per page:
        <select
          onChange={handleChangePageSize}
          value={pageSize}
          className="ml-2 border border-gray-300 rounded-md"
        >
          <option value={7}>7</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={30}>30</option>
        </select>
      </span>
      <div className="flex flex-col flex-1 justify-between items-center text-xs">
        <span className="mt-1 text-xs xs:text-sm text-gray-900">
          {`${totalElements} results`}
        </span>
        <div className="inline-flex mt-2 xs:mt-0">
          <button
            onClick={handleDecPage}
            className="text-sm text-gray-600 hover:text-gray-900 bg-gray-200 border border-gray-300 rounded-l-md px-4 py-1"
          >
            <FontAwesomeIcon icon={faChevronLeft} /> Prev
          </button>
          <button
            onClick={handleIncPage}
            className="text-sm text-gray-600 hover:text-gray-900 bg-gray-200 border border-gray-300 rounded-r-md px-4 py-1"
          >
            Next <FontAwesomeIcon icon={faChevronRight} />
          </button>
        </div>
      </div>
    </div>
  );
}

export default Pagination;
