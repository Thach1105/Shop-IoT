import PropTypes from "prop-types";
import { useState } from "react";

const ImageMagnifier = ({
  imageUrl,
  altText,
  magnifierHeight = 200,
  magnifierWidth = 200,
  zoomLevel = 2,
}) => {
  const [[x, y], setXY] = useState([0, 0]);
  const [[imgWidth, imgHeight], setSize] = useState([0, 0]);
  const [showMagnifier, setShowMagnifier] = useState(false);
  const imgURL = imageUrl.replaceAll(" ", "%20");
  console.log(imgURL);

  return (
    <div className="relative">
      <img
        src={imgURL}
        alt={altText}
        className="h-[500px] object-contain cursor-crosshair"
        onLoad={(e) => {
          const { width, height } = e.currentTarget.getBoundingClientRect();
          setSize([width, height]);
        }}
        onMouseEnter={(e) => {
          const elem = e.currentTarget;
          const { width, height } = elem.getBoundingClientRect();
          setSize([width, height]);
          setShowMagnifier(true);
        }}
        onMouseLeave={() => setShowMagnifier(false)}
        onMouseMove={(e) => {
          const elem = e.currentTarget;
          const { top, left } = elem.getBoundingClientRect();
          const x = e.pageX - left - window.scrollX;
          const y = e.pageY - top - window.scrollY;
          setXY([x, y]);
        }}
      />

      {showMagnifier && (
        <div
          style={{
            position: "absolute",

            left: `100%`,
            top: `0`,
            marginLeft: "20px",

            border: "1px solid #0e8eb9 ",
            height: `${magnifierHeight}px`,
            width: `${magnifierWidth}px`,
            backgroundColor: "white",
            backgroundImage: `url(${imgURL})`,
            backgroundRepeat: "no-repeat",
            backgroundPosition: `
                  ${(x * 100) / imgWidth}% 
                  ${(y * 100) / imgHeight}%
                `,
            backgroundSize: `${imgWidth * zoomLevel}px ${
              imgHeight * zoomLevel
            }px`,
            pointerEvents: "none",
            zIndex: 50,
          }}
        />
      )}
    </div>
  );
};

function ProductImage({ imageUrl, altText }) {
  return (
    <div className="text-center p-4 shadow-lg flex flex-col justify-center items-center">
      <ImageMagnifier
        imageUrl={imageUrl}
        altText={altText}
        magnifierHeight={300}
        magnifierWidth={300}
        zoomLevel={2.5}
      />
      <p className="text-center text-gray-600 mt-2">
        Di chuột qua hình ảnh để phóng to
      </p>
    </div>
  );
}

export default ProductImage;

ProductImage.propTypes = {
  imageUrl: PropTypes.string.isRequired,
  altText: PropTypes.string.isRequired,
};

ImageMagnifier.propTypes = {
  imageUrl: PropTypes.string.isRequired,
  altText: PropTypes.string.isRequired,
  magnifierHeight: PropTypes.number,
  magnifierWidth: PropTypes.number,
  zoomLevel: PropTypes.number,
};
