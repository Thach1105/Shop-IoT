/* eslint-disable react/prop-types */
import { formatNumber } from "../../../../utils/format";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrash } from "@fortawesome/free-solid-svg-icons";
import { useState } from "react";
import {
  changeProductQuantityInCart,
  deleteProductFormCart,
} from "../../../../services/apiCart";
import { useUserState } from "../../../../provider/UserContext";

function ProductItem({ product, cartId, handleChangeCart }) {
  const { setChangedCart } = useUserState();
  const [quantity, setQuantity] = useState(product.quantity || 1);

  const handleChangeQuantity = async (quantityChange) => {
    if (quantity + quantityChange < 1) return;
    setQuantity(quantity + quantityChange);

    const reqBody = {
      product_id: product.id,
      quantity: quantityChange,
    };

    await changeProductQuantityInCart(cartId, reqBody);
    setChangedCart(true);
    handleChangeCart(true);
  };

  const handleDeleteProduct = async (productId) => {
    await deleteProductFormCart(productId);
    setChangedCart(true);
    handleChangeCart(true);
  };

  return (
    <div className="grid grid-cols-5 p-4 items-center gap-2">
      <div className="flex items-center gap-3 col-span-2">
        <img
          src={product.image}
          alt="Product"
          className="w-16 h-16 object-cover"
        />
        <p>{product.name}</p>
      </div>
      <div>{formatNumber(product.unitCost)} đ</div>
      <div className="flex items-center gap-2">
        <button
          onClick={() => handleChangeQuantity(-1)}
          className="px-2 py-1 border"
        >
          -
        </button>
        <p className="w-12 text-center border px-2 py-1">{quantity}</p>
        <button
          onClick={() => handleChangeQuantity(1)}
          className="px-2 py-1 border"
        >
          +
        </button>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-red-600">{formatNumber(product.total)}đ</span>
        <button
          onClick={() => handleDeleteProduct(product.id)}
          className="text-gray-400"
        >
          <FontAwesomeIcon className="hover:text-red-600" icon={faTrash} />
        </button>
      </div>
    </div>
  );
}

export default ProductItem;
