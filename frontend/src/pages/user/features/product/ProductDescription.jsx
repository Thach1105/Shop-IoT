import PropTypes from "prop-types";
import { useState } from "react";

function ProductDescription({ description }) {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className="bg-white ">
      <div
        className={`relative transition-all duration-300 ease-in-out
          ${!isExpanded ? "h-[200px] overflow-hidden" : "h-auto"}`}
      >
        <div
          className="ql-content
            /* Text Alignment */
            [&_.ql-align-center]:text-center 
            [&_.ql-align-justify]:text-justify
            [&_.ql-align-right]:text-right
            
            /* Headers */
            [&_h1]:text-2xl [&_h1]:font-bold [&_h1]:my-4
            [&_h2]:text-xl [&_h2]:font-bold [&_h2]:my-4
            [&_h3]:text-lg [&_h3]:font-bold [&_h3]:my-3
            [&_h4]:text-base [&_h4]:font-bold [&_h4]:my-3
            [&_h5]:text-sm [&_h5]:font-bold [&_h5]:my-2
            [&_h6]:text-xs [&_h6]:font-bold [&_h6]:my-2
            
            /* Basic Text */
            [&_p]:my-3 [&_p]:leading-relaxed
            
            /* Lists */
            [&_ul]:list-disc [&_ul]:pl-6 [&_ul]:my-3 [&_ul]:space-y-2
            [&_ol]:list-decimal [&_ol]:pl-6 [&_ol]:my-3 [&_ol]:space-y-2
            
            /* Blockquotes */
            [&_blockquote]:border-l-4 [&_blockquote]:border-gray-300 
            [&_blockquote]:pl-4 [&_blockquote]:my-4 [&_blockquote]:italic
            
            /* Text Formatting */
            [&_strong]:font-bold
            [&_em]:italic
            [&_u]:underline
            [&_s]:line-through
            
            /* Links */
            [&_a]:text-blue-600 [&_a]:underline hover:[&_a]:text-blue-800
            
            /* Font Sizes */
            [&_.ql-size-small]:text-sm
            [&_.ql-size-large]:text-lg
            [&_.ql-size-huge]:text-xl

            /* Font Families */
            [&_.ql-font-serif]:font-serif
            [&_.ql-font-monospace]:font-mono
            
            /* Text Colors */
            [&_.ql-color-red]:text-red-500
            [&_.ql-color-\#785412]:text-[#785412]
            [&_.ql-color-green]:text-green-500
            [&_.ql-color-yellow]:text-yellow-500
            [&_.ql-color-black]:text-black
            [&_.ql-color-blue]:text-blue-500
            [&_.ql-color-pink]:text-pink-500
            [&_.ql-color-gray]:text-gray-500

            /* Background Colors */
            [&_.ql-bg-red]:bg-red-500
            [&_.ql-bg-\#785412]:bg-[#785412]
            [&_.ql-bg-green]:bg-green-500
            [&_.ql-bg-yellow]:bg-yellow-500
            [&_.ql-bg-black]:bg-black
            [&_.ql-bg-blue]:bg-blue-500
            [&_.ql-bg-pink]:bg-pink-500
            [&_.ql-bg-gray]:bg-gray-500

            /* Images */
            [&_img]:max-w-full [&_img]:h-auto [&_img]:my-4 
            [&_img.ql-align-center]:mx-auto [&_img.ql-align-center]:block
            [&_img.ql-align-right]:ml-auto [&_img.ql-align-right]:block"
          dangerouslySetInnerHTML={{ __html: description }}
        />

        {!isExpanded && (
          <div className="absolute bottom-0 left-0 right-0 h-20 bg-gradient-to-t from-white to-transparent" />
        )}
      </div>

      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="mt-4 px-4 py-2 text-center w-full text-blue-700"
      >
        {isExpanded ? "Thu gọn" : "Xem thêm ..."}
      </button>
    </div>
  );
}

ProductDescription.propTypes = {
  description: PropTypes.string.isRequired,
};

export default ProductDescription;
