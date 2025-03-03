import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { formatNumber } from "../../../../utils/format";
import axios from "axios";
import { TextField } from "@mui/material";
import { Backdrop, Box, Modal, Typography } from "@mui/material";
import { useForm, Controller } from "react-hook-form";
import Select from "react-select";
import { createOrder } from "../../../../services/apiOrder";
import CircularIndeterminate from "../../../../utils/CircularIndeterminate";

const styleModal = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 400,
  bgcolor: "background.paper",
  p: 4,
};

const customStyles = {
  control: (base) => ({
    ...base,
    minHeight: "42px",
    border: "1px solid #e2e8f0",
    borderRadius: "0.375rem",
    boxShadow: "none",
    "&:hover": {
      border: "1px solid #e2e8f0",
    },
  }),
  option: (base, state) => ({
    ...base,
    backgroundColor: state.isFocused ? "#EBF5FF" : "white",
    color: "#1F2937",
    "&:hover": {
      backgroundColor: "#EBF5FF",
    },
  }),
  placeholder: (base) => ({
    ...base,
    color: "#9CA3AF",
  }),
};

function CreateOrder() {
  const location = useLocation();
  const { cartSummary } = location.state;
  const { products } = location.state;
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const {
    register,
    handleSubmit,
    control,
    watch,
    formState: { errors },
  } = useForm({
    defaultValues: {
      fullName: "",
      phone: "",
      email: "",
      province: null,
      district: null,
      ward: null,
      address: "",
    },
  });

  const [paymentMethod, setPaymentMethod] = useState("COD");

  const [shippingFee, setShippingFee] = useState(30000);
  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [wards, setWards] = useState([]);
  const [err, setErr] = useState();
  const [note, setNote] = useState("");
  const [homeDelivery, setHomeDelivery] = useState(true);

  const selectedProvince = watch("province");
  const selectedDistrict = watch("district");

  useEffect(() => {
    const fetchProvinces = async () => {
      try {
        const response = await axios.get(
          "https://provinces.open-api.vn/api/p/"
        );
        const formattedProvinces = response.data.map((province) => ({
          value: province.code,
          label: province.name,
        }));
        setProvinces(formattedProvinces);
      } catch (error) {
        console.error("Error fetching provinces:", error);
      }
    };
    fetchProvinces();
  }, []);

  useEffect(() => {
    const fetchDistricts = async () => {
      if (selectedProvince) {
        try {
          const response = await axios.get(
            `https://provinces.open-api.vn/api/p/${selectedProvince.value}?depth=2`
          );
          const formattedDistricts = response.data.districts.map(
            (district) => ({
              value: district.code,
              label: district.name,
            })
          );
          setDistricts(formattedDistricts);
        } catch (error) {
          console.error("Error fetching districts:", error);
        }
      } else {
        setDistricts([]);
      }
    };
    fetchDistricts();
  }, [selectedProvince]);

  useEffect(() => {
    const fetchWards = async () => {
      if (selectedDistrict) {
        try {
          const response = await axios.get(
            `https://provinces.open-api.vn/api/d/${selectedDistrict.value}?depth=2`
          );
          const formattedWards = response.data.wards.map((ward) => ({
            value: ward.code,
            label: ward.name,
          }));
          setWards(formattedWards);
        } catch (error) {
          console.error("Error fetching wards:", error);
        }
      } else {
        setWards([]);
      }
    };
    fetchWards();
  }, [selectedDistrict]);

  const onSubmit = async (data) => {
    setLoading(true);
    let address = "Nhận tại cửa hàng";
    if (homeDelivery) {
      address = `${data.address}, ${data.ward.label}, ${data.district.label}, ${data.province.label}`;
    } else {
      address =
        "Số 87-89, phố Hạ Đình, Phường Thanh Xuân Trung, Quận Thanh Xuân, Hà Nội";
    }
    const order = {
      consigneeName: data.fullName,
      address,
      homeDelivery,
      phone: data.phone,
      paymentType: paymentMethod,
      totalPrice: cartSummary.total + shippingFee,
      notes: note,
      details: products.map((p) => {
        return {
          product: p.id,
          quantity: p.quantity,
          unitPrice: p.unitCost,
          totalPrice: p.total,
        };
      }),
    };

    try {
      const response = await createOrder(order);
      console.log(response);
      const { data } = response;
      const newOrder = data?.content;
      console.log(newOrder);
      setLoading(false);
      navigate("/tai-khoan/quan-ly-don-hang", { state: newOrder });
    } catch (error) {
      const { response } = error;
      const { data } = response;
      const err = {
        errorCode: data?.code,
        errorMessage: data?.message,
      };

      setErr(err);
    }
  };

  const handleResetError = () => setErr(null);

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="flex min-h-screen bg-white gap-5 relative"
    >
      {/* Phần form bên trái */}
      <div className="w-3/4 p-6 shadow-xl">
        <h1 className="text-2xl font-bold mb-6">ĐẶT HÀNG</h1>

        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">
            THÔNG TIN KHÁCH HÀNG
          </h2>
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
            {/* <Fade in={err}> */}
            <Box sx={styleModal}>
              <h1 className="text-xl text-red-500 underline">Thông báo:</h1>
              <Typography id="transition-modal-description" sx={{ mt: 2 }}>
                {`${err?.errorMessage} (code:${err?.errorCode})`}
              </Typography>
            </Box>
            {/* </Fade> */}
          </Modal>

          <div className="space-y-4">
            <div>
              <label className="block text-gray-600 mb-1">
                Họ và tên <span className="text-red-500">*</span>
              </label>
              <input
                {...register("fullName", {
                  required: "Vui lòng nhập họ tên",
                })}
                type="text"
                className={`w-7/12 border rounded p-2 focus:outline-none focus:ring-1 
                    ${
                      errors.fullName
                        ? "focus:ring-red-500"
                        : "focus:ring-blue-500"
                    }`}
                placeholder="Nhập họ và tên"
              />
              {errors.fullName && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.fullName.message}
                </p>
              )}
            </div>

            <div>
              <label className="block text-gray-600 mb-1">
                Số điện thoại <span className="text-red-500">*</span>
              </label>
              <input
                {...register("phone", {
                  required: "Vui lòng nhập số điện thoại",
                  pattern: {
                    value: /(84|0[3|5|7|8|9])+([0-9]{8})\b/,
                    message: "Số điện thoại không hợp lệ",
                  },
                })}
                type="tel"
                className={`w-7/12 border rounded p-2 focus:outline-none focus:ring-1 focus:ring-blue-500
                    ${
                      errors.phone
                        ? "focus:ring-red-500"
                        : "focus:ring-blue-500"
                    }`}
                placeholder="Nhập số điện thoại"
              />
              {errors.phone && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.phone.message}
                </p>
              )}
            </div>

            <div>
              <label className="block text-gray-600 mb-1">Email</label>
              <input
                {...register("email", {
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: "Email không hợp lệ",
                  },
                })}
                type="email"
                className={`w-7/12 border rounded p-2 focus:outline-none focus:ring-1 focus:ring-blue-500
                    ${
                      errors.email
                        ? "focus:ring-red-500"
                        : "focus:ring-blue-500"
                    }`}
                placeholder="Nhập email"
              />
              {errors.email && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.email.message}
                </p>
              )}
            </div>
          </div>
        </div>

        {/* Phương thức nhận hàng */}
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">
            PHƯƠNG THỨC NHẬN HÀNG
          </h2>
          <div className="grid grid-cols-2 gap-4">
            <button
              type="button"
              onClick={() => {
                setShippingFee(30000);
                setHomeDelivery(true);
              }}
              className={`border p-4 rounded flex items-center justify-center gap-2 ${
                homeDelivery ? "bg-blue-50 border-blue-500" : "hover:bg-gray-50"
              }`}
            >
              <span>🚚</span>
              Giao hàng tận nhà
            </button>
            <button
              onClick={() => {
                setShippingFee(0);
                setHomeDelivery(false);
              }}
              type="button"
              className={`border p-4 rounded flex items-center justify-center gap-2 ${
                !homeDelivery
                  ? "bg-blue-50 border-blue-500"
                  : "hover:bg-gray-50"
              }`}
            >
              <span>🏪</span>
              Nhận hàng tại cửa hàng
            </button>
          </div>

          <div className="bg-gray-100 p-4 mt-4 rounded">
            {homeDelivery ? (
              <>
                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <label className="block text-gray-600 mb-1">
                      Tỉnh/Thành phố <span className="text-red-500">*</span>
                    </label>
                    <Controller
                      name="province"
                      control={control}
                      rules={{ required: "Vui lòng chọn tỉnh/thành phố" }}
                      render={({ field }) => (
                        <Select
                          {...field}
                          options={provinces}
                          styles={customStyles}
                          placeholder="Chọn Tỉnh/Thành phố"
                          noOptionsMessage={() => "Không tìm thấy kết quả"}
                          isSearchable={true}
                        />
                      )}
                    />
                    {errors.province && (
                      <p className="text-red-500 text-sm mt-1">
                        {errors.province.message}
                      </p>
                    )}
                  </div>

                  <div>
                    <label className="block text-gray-600 mb-1">
                      Quận/Huyện <span className="text-red-500">*</span>
                    </label>
                    <Controller
                      name="district"
                      control={control}
                      rules={{ required: "Vui lòng chọn quận/huyện" }}
                      render={({ field }) => (
                        <Select
                          {...field}
                          options={districts}
                          styles={customStyles}
                          placeholder="Chọn Quận/Huyện"
                          noOptionsMessage={() =>
                            "Vui lòng chọn Tỉnh/Thành phố trước"
                          }
                          isSearchable={true}
                          isDisabled={!selectedProvince}
                        />
                      )}
                    />
                    {errors.district && (
                      <p className="text-red-500 text-sm mt-1">
                        {errors.district.message}
                      </p>
                    )}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-gray-600 mb-1">
                      Phường/Xã <span className="text-red-500">*</span>
                    </label>
                    <Controller
                      name="ward"
                      control={control}
                      rules={{ required: "Vui lòng chọn phường/xã" }}
                      render={({ field }) => (
                        <Select
                          {...field}
                          options={wards}
                          styles={customStyles}
                          placeholder="Chọn Phường/Xã"
                          noOptionsMessage={() =>
                            "Vui lòng chọn Quận/Huyện trước"
                          }
                          isSearchable={true}
                          isDisabled={!selectedDistrict}
                        />
                      )}
                    />
                    {errors.ward && (
                      <p className="text-red-500 text-sm mt-1">
                        {errors.ward.message}
                      </p>
                    )}
                  </div>
                  <div>
                    <label className="block text-gray-600 mb-1">
                      Địa chỉ <span className="text-red-500">*</span>
                    </label>
                    <input
                      {...register("address", {
                        required: "Vui lòng nhập địa chỉ",
                      })}
                      type="text"
                      className={`w-full border rounded p-2 focus:outline-none focus:ring-1 focus:ring-blue-500
                        ${
                          errors.address
                            ? "focus:ring-red-500"
                            : "focus:ring-blue-500"
                        }`}
                      placeholder="Nhập số nhà, tên đường"
                    />
                    {errors.address && (
                      <p className="text-red-500 text-sm mt-1">
                        {errors.address.message}
                      </p>
                    )}
                  </div>
                </div>
              </>
            ) : (
              <div className="bg-white w-fit p-2">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    defaultChecked
                    className="accent-blue-500"
                  />
                  <p>
                    Cửa hàng chính của Shop IoT
                    <br />
                    Số 87-89, phố Hạ Đình, Phường Thanh Xuân Trung, Quận Thanh
                    Xuân, Hà Nội
                  </p>
                </label>
              </div>
            )}
          </div>
        </div>

        {/* Hình thức thanh toán */}
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">
            HÌNH THỨC THANH TOÁN
          </h2>
          <div className="flex sm:flex-row flex-col gap-3 justify-between">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="radio"
                name="payment"
                value="COD"
                checked={paymentMethod === "COD"}
                onChange={(e) => setPaymentMethod(e.target.value)}
                className="accent-blue-500"
              />
              Thanh toán tiền mặt khi nhận hàng (COD)
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="radio"
                name="payment"
                value="ZALOPAY"
                checked={paymentMethod === "ZALOPAY"}
                onChange={(e) => setPaymentMethod(e.target.value)}
                className="accent-blue-500"
              />
              Thanh toán bằng Ví ZaloPay
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="radio"
                name="payment"
                value="VNPAY"
                checked={paymentMethod === "VNPAY"}
                onChange={(e) => setPaymentMethod(e.target.value)}
                className="accent-blue-500"
              />
              Thanh toán bằng Ví VNPay
            </label>
          </div>
        </div>

        {/* Ghi chú */}
        <div className="mb-8">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">Ghi chú</h2>
          <TextField
            value={note}
            onChange={(e) => setNote(e.target.value)}
            id="filled-textarea"
            placeholder="Nhập ghi chú cần lưu ý khi chuyển hàng"
            multiline
            className="w-full border rounded p-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      {/* Panel đơn hàng bên phải */}
      <div className="w-1/4 bg-white p-6 border-l shadow-md">
        <div className="sticky top-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="font-medium">Đơn hàng</h3>
            <Link to={"/gio-hang"} className="text-blue-500 hover:underline">
              Sửa
            </Link>
          </div>

          {products.map((p, index) => (
            <div key={index} className="flex items-center gap-4 mb-6">
              <img
                src={p.image}
                alt={p.name}
                className="w-16 h-16 object-cover rounded"
              />
              <div>
                <p className="font-medium">{p.name}</p>
                <p className="text-gray-600">
                  {`${p.quantity} x ${formatNumber(p.unitCost)}`} đ
                </p>
              </div>
            </div>
          ))}

          <div className="space-y-3 mb-4">
            <div className="flex justify-between text-gray-600">
              <span>Tạm tính</span>
              <span>{formatNumber(cartSummary.total)} đ</span>
            </div>
            <div className="flex justify-between text-gray-600">
              <span>Phí vận chuyển</span>
              <span>{formatNumber(shippingFee)} đ</span>
            </div>
            <div className="flex justify-between font-bold pt-3 border-t">
              <span>Tổng cộng</span>
              <span className="text-red-600">
                {formatNumber(cartSummary.total + shippingFee)} đ
              </span>
            </div>
            <p className="text-sm text-gray-500">(Đã bao gồm VAT)</p>
          </div>

          <button
            type="submit"
            className="w-full bg-red-600 text-white py-3 rounded hover:bg-red-700 transition-colors"
          >
            Đặt mua
          </button>
        </div>
      </div>
      {loading && <CircularIndeterminate />}
    </form>
  );
}

export default CreateOrder;
