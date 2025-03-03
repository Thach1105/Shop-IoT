/* eslint-disable react/prop-types */
import { Backdrop, Box, Modal } from "@mui/material";
import { useForm } from "react-hook-form";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faEyeSlash, faXmark } from "@fortawesome/free-solid-svg-icons";
import { useState, useEffect } from "react";

import { changePassword } from "../../../../services/apiCustomer";

const styleModal = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "50%",
  bgcolor: "background.paper",
  p: 4,
};

function FormChangePassword({
  open,
  handleOff,
  setContentSB,
  setOpenSB,
  userId,
}) {
  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false,
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setError,
    reset,
  } = useForm({
    defaultValues: {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    },
  });

  useEffect(() => {
    if (!open) {
      reset({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      setShowPasswords({
        current: false,
        new: false,
        confirm: false,
      });
    }
  }, [open, reset]);

  const newPassword = watch("newPassword");

  const onSubmit = async (data) => {
    const bodyRequest = {
      oldPassword: data.currentPassword,
      newPassword: data.newPassword,
    };
    console.log(bodyRequest);

    try {
      await changePassword(bodyRequest, userId);
      setContentSB("Đổi mật khẩu thành công");
      setOpenSB(true);
      handleOff();
    } catch (e) {
      setError("currentPassword", {
        type: "server",
        message: e.response?.data?.message,
      });
    }
  };

  const togglePassword = (field) => {
    setShowPasswords((prev) => ({
      ...prev,
      [field]: !prev[field],
    }));
  };
  return (
    <>
      <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        open={open}
        onClose={handleOff}
        closeAfterTransition
        slots={{ backdrop: Backdrop }}
        slotProps={{
          backdrop: {
            timeout: 500,
          },
        }}
      >
        <Box sx={styleModal}>
          <div className="w-full text-right">
            <FontAwesomeIcon
              onClick={handleOff}
              className="text-red-600 cursor-pointer"
              size="xl"
              icon={faXmark}
            />
          </div>
          <h1 className="text-xl text-black underline mb-6">Đổi mật khẩu:</h1>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block mb-1">Mật khẩu hiện tại *</label>
              <div className="relative">
                <input
                  type={showPasswords.current ? "text" : "password"}
                  {...register("currentPassword", {
                    required: "Vui lòng nhập mật khẩu hiện tại",
                    minLength: {
                      value: 8,
                      message: "Mật khẩu phải có ít nhất 8 ký tự",
                    },
                  })}
                  className={`w-full px-3 py-2 border rounded-md pr-10 ${
                    errors.currentPassword
                      ? "border-red-500"
                      : "border-gray-300"
                  }`}
                  placeholder="Nhập mật khẩu hiện tại"
                />
                <button
                  type="button"
                  onClick={() => togglePassword("current")}
                  className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500"
                >
                  <FontAwesomeIcon
                    icon={showPasswords.current ? faEyeSlash : faEye}
                  />
                </button>
              </div>
              {errors.currentPassword && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.currentPassword.message}
                </p>
              )}
            </div>

            <div>
              <label className="block mb-1">Mật khẩu mới *</label>
              <div className="relative">
                <input
                  type={showPasswords.new ? "text" : "password"}
                  {...register("newPassword", {
                    required: "Vui lòng nhập mật khẩu mới",
                    minLength: {
                      value: 8,
                      message: "Mật khẩu mới phải có ít nhất 8 ký tự",
                    },
                  })}
                  className={`w-full px-3 py-2 border rounded-md pr-10 ${
                    errors.newPassword ? "border-red-500" : "border-gray-300"
                  }`}
                  placeholder="Nhập mật khẩu mới"
                />
                <button
                  type="button"
                  onClick={() => togglePassword("new")}
                  className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500"
                >
                  <FontAwesomeIcon
                    icon={showPasswords.new ? faEyeSlash : faEye}
                  />
                </button>
              </div>
              {errors.newPassword && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.newPassword.message}
                </p>
              )}
            </div>

            <div>
              <label className="block mb-1">Cập nhật mật khẩu mới *</label>
              <div className="relative">
                <input
                  type={showPasswords.confirm ? "text" : "password"}
                  {...register("confirmPassword", {
                    required: "Vui lòng nhập lại mật khẩu mới",
                    validate: (value) =>
                      value === newPassword || "Mật khẩu nhập lại không khớp",
                  })}
                  className={`w-full px-3 py-2 border rounded-md pr-10 ${
                    errors.confirmPassword
                      ? "border-red-500"
                      : "border-gray-300"
                  }`}
                  placeholder="Nhập lại mật khẩu mới"
                />
                <button
                  type="button"
                  onClick={() => togglePassword("confirm")}
                  className="absolute right-2 top-1/2 -translate-y-1/2 text-gray-500"
                >
                  <FontAwesomeIcon
                    icon={showPasswords.confirm ? faEyeSlash : faEye}
                  />
                </button>
              </div>
              {errors.confirmPassword && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.confirmPassword.message}
                </p>
              )}
            </div>

            <button
              type="submit"
              className="w-full py-2 bg-blue-100 hover:bg-blue-200 rounded-md transition-colors"
            >
              Lưu thay đổi
            </button>
          </form>
        </Box>
      </Modal>
    </>
  );
}

export default FormChangePassword;
