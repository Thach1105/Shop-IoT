import { useLoaderData, useNavigate } from "react-router-dom";
import EmtyCart from "./EmtyCart";
import { useEffect, useState } from "react";
import ProductItem from "./ProductItem";
import { formatNumber } from "../../../../utils/format";
import { getMyCart } from "../../../../services/apiCart";
import { checkPrevOrder } from "../../../../services/apiOrder";
import { Backdrop, Box, Modal, Typography } from "@mui/material";

const styleModal = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
  p: 4,
};

function CartPage() {
  const cart = useLoaderData();
  const navigate = useNavigate();
  const [products, setProducts] = useState(cart?.products);
  const [cartChanged, setCartChanged] = useState(false);
  const [cartSummary, setCartSummary] = useState(cart?.cartSummary);
  const [err, setErr] = useState();

  const handleResetError = () => setErr(null);

  useEffect(() => {
    async function fetchDataCart() {
      if (cartChanged) {
        try {
          const response = await getMyCart();
          const { data } = response;
          setProducts(data?.content.products);
          setCartSummary(data?.content.cartSummary);
          setCartChanged(false);
        } catch (error) {
          if (error.response?.status === 400) {
            const { data } = error.response;
            if (data.code === 1010 && data.message === "Your cart is empty") {
              return null;
            }
          }
          throw new Error(error.response?.data?.message);
        }
      }
    }

    fetchDataCart();
  }, [cartChanged]);

  async function handleCheckPreviousOrder() {
    const bodyRequest = [];

    products.map((product) => {
      const item = {
        productId: product.id,
        quantity: product.quantity,
      };

      bodyRequest.push(item);
    });

    try {
      const response = await checkPrevOrder({ prevOrderList: bodyRequest });
      if (response.status === 200) {
        const orderDetail = {
          products,
          cartSummary,
        };
        navigate("/dat-hang", { state: orderDetail });
      }
    } catch (error) {
      const { response } = error;
      const { data } = response;
      const err = {
        errorCode: data?.code,
        errorMessage: data?.message,
      };

      setErr(err);
    }
  }

  return (
    <div className="mx-auto px-4">
      <h1 className="text-3xl font-bold mb-6">GIỎ HÀNG CỦA BẠN</h1>
      <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        open={err !== null && err !== undefined}
        onClose={handleResetError}
        closeAfterTransition
        slots={{ backdrop: Backdrop }}
        slotProps={{
          backdrop: {
            timeout: 500,
          },
        }}
      >
        <Box sx={styleModal}>
          <h1 className="text-xl text-red-500 underline">Thông báo:</h1>
          <Typography id="transition-modal-description" sx={{ mt: 2 }}>
            {err?.errorMessage}
          </Typography>
        </Box>
      </Modal>

      {cart ? (
        <div className="flex gap-6">
          {/* Bảng giỏ hàng bên trái */}
          <div className="w-3/4">
            <div className="bg-white rounded-lg shadow">
              <div className="grid grid-cols-5 p-4 border-b gap-2 text-gray-600">
                <div className="col-span-2">Sản phẩm</div>
                <div>Đơn giá</div>
                <div>Số lượng</div>
                <div>Thành tiền</div>
              </div>

              {/* Item trong giỏ hàng */}
              {products.map((product, index) => (
                <ProductItem
                  key={index}
                  product={product}
                  cartId={cart.cart_id}
                  handleChangeCart={setCartChanged}
                />
              ))}
            </div>
          </div>

          {/* Phần tổng tiền bên phải */}
          <div className="w-1/4">
            <div className="bg-white rounded-lg shadow p-4">
              <div className="pt-4">
                <div className="flex justify-between mb-2">
                  <span>Tạm tính</span>
                  <span>{formatNumber(cartSummary.total)} đ</span>
                </div>
                <div className="flex justify-between border-t py-2 font-bold mb-2">
                  <span>Tổng cộng</span>
                  <span className="text-red-600">
                    {formatNumber(cartSummary.total)} đ
                  </span>
                </div>
                <div className="text-sm text-gray-500 mb-4">
                  <p>(Đã bao gồm VAT)</p>
                  <p>Chưa bao gồm phí vận chuyển</p>
                </div>
                <button
                  onClick={handleCheckPreviousOrder}
                  className="w-full bg-red-600 text-white py-3 rounded"
                >
                  Mua hàng
                </button>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <EmtyCart />
      )}
    </div>
  );
}

export default CartPage;
