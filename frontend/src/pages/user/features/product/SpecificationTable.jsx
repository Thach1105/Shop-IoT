import PropTypes from "prop-types";

function SpecificationTable({ productDetails }) {
  const specifications = Object.entries(productDetails).map(
    ([label, value]) => ({
      label,
      value,
    })
  );

  return (
    <div className="w-full border rounded-lg overflow-hidden">
      {specifications.map((spec, index) => (
        <div
          key={index}
          className={`flex ${index % 2 === 0 ? "bg-gray-50" : "bg-white"}`}
        >
          <div className="w-1/3 p-3 border-r border-b">
            <span className="text-gray-600 font-medium">{spec.label}</span>
          </div>
          <div className="w-2/3 p-3 border-b">
            {Array.isArray(spec.value) ? (
              <ul className="list-disc list-inside">
                {spec.value.map((item, i) => (
                  <li key={i} className="text-gray-800">
                    {item}
                  </li>
                ))}
              </ul>
            ) : (
              <span className="text-gray-800">{spec.value}</span>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

SpecificationTable.propTypes = {
  productDetails: PropTypes.object.isRequired,
};

export default SpecificationTable;
