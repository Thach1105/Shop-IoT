import { useState, useEffect } from "react";
import { faAngleLeft, faAngleRight } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

function Slide() {
  const images = ["./slide1.webp", "./slide2.webp", "./slide3.jpg"];

  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
    }, 5000);
    return () => clearInterval(interval);
  }, [images.length, currentIndex]);

  const handlePrev = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex === 0 ? images.length - 1 : prevIndex - 1
    );
  };

  const handleNext = () => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
  };
  return (
    <div className="relative rounded-md">
      <img
        src={images[currentIndex]}
        alt={`Slide ${currentIndex + 1}`}
        className="h-[400px] transition-opacity duration-500 w-full"
      />
      <div className="absolute inset-0 flex items-center justify-between px-4">
        <button
          onClick={handlePrev}
          className="bg-gray-500/20 text-white px-4 py-2 rounded hover:bg-opacity-75"
        >
          <FontAwesomeIcon icon={faAngleLeft} />
        </button>

        <button
          onClick={handleNext}
          className="bg-gray-500/20 text-white px-4 py-2 rounded hover:bg-opacity-75"
        >
          <FontAwesomeIcon icon={faAngleRight} />
        </button>
      </div>
      {/* <div className="absolute inset-0 flex flex-col items-center justify-center text-white">
    <h1 className="text-4xl font-bold bg-black bg-opacity-50 px-4 py-2 rounded">
      The best performing products in the world
    </h1>
  </div> */}
    </div>
  );
}

export default Slide;
