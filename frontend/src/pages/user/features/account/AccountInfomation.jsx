import { useEffect, useState } from "react";
import { getMyInfo } from "../../../../services/authentication";
import Loading from "../../../../utils/Loading";
import FormChangePassword from "./FormChangePassword";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import { useAuth } from "../../../../provider/authProvider";
import { jwtDecode } from "jwt-decode";
import { updateCustomer } from "../../../../services/apiCustomer";
import { useUserState } from "../../../../provider/UserContext";

function AccountInfomation() {
  const { token } = useAuth();
  const decode = jwtDecode(token);

  const { setChangedUserInfo } = useUserState();

  const [myInfo, setMyInfo] = useState();
  const [formData, setFormData] = useState({
    fullName: "",
    birthDate: "",
    gender: "",
    email: "",
    phone: "",
  });
  const [openChangePw, setOpenChangePw] = useState(false);
  const [openSB, setOpenSB] = useState(false);
  const [contentSB, setContentSB] = useState("");

  useEffect(() => {
    async function fetchData() {
      const response = await getMyInfo();
      const { data } = response;
      const { content } = data;
      setMyInfo(content);

      setFormData({
        fullName: content.fullName || "",
        birthDate: content.dob || "",
        gender: content.sex || "",
        email: content.email || "",
        phone: content.phoneNumber || "",
      });
    }
    fetchData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const userInfoUpdate = {
      fullName: formData.fullName,
      email: formData.email,
      phoneNumber: formData.phone,
      sex: formData.gender.length > 0 ? formData.gender.at(0) : "",
      dob: formData.birthDate,
    };
    console.log(userInfoUpdate);
    try {
      const response = await updateCustomer(userInfoUpdate, decode.data.id);
      const { data } = response;
      const { content } = data;
      setMyInfo(content);

      setFormData({
        fullName: content.fullName || "",
        birthDate: content.dob || "",
        gender: content.sex || "",
        email: content.email || "",
        phone: content.phoneNumber || "",
      });

      setOpenSB(true);
      setContentSB("Cập nhật thông tin thành công");
      setChangedUserInfo(true);
    } catch (e) {
      console.log(e);
    }
  };
  const handleOffChangePassword = () => {
    setOpenChangePw(false);
  };
  return myInfo ? (
    <div className="p-6">
      <Snackbar
        anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
        open={openSB}
        autoHideDuration={6000}
        onClose={() => setOpenSB(false)}
      >
        <Alert severity="success" variant="filled" sx={{ width: "100%" }}>
          {contentSB}
        </Alert>
      </Snackbar>
      <FormChangePassword
        open={openChangePw}
        handleOff={handleOffChangePassword}
        setContentSB={setContentSB}
        setOpenSB={setOpenSB}
        userId={decode.data.id}
      />
      <h1 className="text-2xl font-bold mb-6">Thông tin tài khoản</h1>

      <div className="grid grid-cols-2 gap-8">
        {/* Cột thông tin cá nhân */}
        <form onSubmit={handleSubmit}>
          <h2 className="text-xl font-semibold mb-4">Thông tin cá nhân</h2>

          <div className="space-y-4">
            <div>
              <label className="block mb-1">Họ và tên *</label>
              <input
                type="text"
                className="w-full border rounded p-2"
                name="fullName"
                value={formData.fullName}
                onChange={handleInputChange}
              />
            </div>

            <div>
              <label className="block mb-1">Ngày sinh</label>
              <input
                className="p-2 border rounded w-full"
                type="date"
                name="birthDate"
                value={formData.birthDate}
                onChange={handleInputChange}
              />
            </div>

            <div>
              <label className="block mb-1">Giới tính</label>
              <div className="flex gap-4">
                <label className="flex items-center">
                  <input
                    type="radio"
                    name="gender"
                    value="M"
                    checked={formData.gender === "M"}
                    onChange={handleInputChange}
                  />{" "}
                  Nam
                </label>
                <label className="flex items-center">
                  <input
                    type="radio"
                    name="gender"
                    value="F"
                    checked={formData.gender === "F"}
                    onChange={handleInputChange}
                  />{" "}
                  Nữ
                </label>
                <label className="flex items-center">
                  <input
                    type="radio"
                    name="gender"
                    value="O"
                    checked={formData.gender === "O"}
                    onChange={handleInputChange}
                  />
                  Khác
                </label>
              </div>
            </div>

            <button
              type="submit"
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Lưu thay đổi
            </button>
          </div>
        </form>

        {/* Cột tài khoản */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Tài khoản</h2>

          <div className="space-y-4">
            <div>
              <label className="block mb-1">Số điện thoại</label>
              <div className="flex justify-between items-center">
                <input
                  type="tel"
                  className="w-full border rounded p-2"
                  name="phone"
                  value={formData.phone}
                  onChange={handleInputChange}
                />
              </div>
            </div>

            <div>
              <label className="block mb-1">Địa chỉ email</label>
              <div className="flex justify-between items-center">
                <span className="text-gray-500">{formData.email}</span>
                {/* <button className="text-blue-500">Cập nhật</button> */}
              </div>
            </div>

            <div>
              <label className="block mb-1">Mật khẩu</label>
              <div className="flex justify-between items-center">
                <span>********</span>
                <button
                  onClick={() => setOpenChangePw(true)}
                  className="text-blue-500"
                >
                  Đổi mật khẩu
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  ) : (
    <Loading />
  );
}

export default AccountInfomation;
