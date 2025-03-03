/* eslint-disable react/prop-types */
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import Box from "@mui/material/Box";
import Modal from "@mui/material/Modal";
import { useState } from "react";
import { updateBrand } from "../../../../services/apiBrand";
import { useNavigate } from "react-router-dom";

const style = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 600,
  bgcolor: "background.paper",
  border: "2px solid #000",
  boxShadow: 24,
  p: 4,
};

function BrandEdit({ brand, open, setOpen }) {
  const handleClose = () => setOpen(false);
  const [name, setName] = useState(brand.name);
  const [logo, setLogo] = useState();
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setLogo(file);
  };

  async function handleCreateBrand(e) {
    e.preventDefault();

    try {
      const response = await updateBrand({ name }, logo, brand.id);
      console.log(response.data.content);

      handleClose();
      navigate(0);
    } catch (error) {
      console.log(error);
    }
  }
  return (
    <div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          <div className="flex items-center justify-center bg-gray-100">
            <div className="bg-white rounded-lg shadow-lg p-4 w-full ">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold">{`Edit Brand #${brand.id}`}</h2>
                <button
                  onClick={handleClose}
                  className="text-gray-500 hover:text-gray-700"
                >
                  <FontAwesomeIcon icon={faXmark} />
                </button>
              </div>
              <form>
                <div className="mb-4">
                  <label className="block text-gray-700 mb-1">
                    Tên Thương hiệu
                  </label>
                  <input
                    type="text"
                    className="w-full px-3 py-2 border rounded"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                  />
                </div>

                <div className="mb-4">
                  <label className="block text-gray-700 mb-1">
                    Logo thương hiệu
                  </label>
                  {logo ? (
                    <div className="flex flex-col items-center">
                      <img
                        src={URL.createObjectURL(logo)}
                        alt="Product"
                        className="object-cover mb-4"
                      />
                      <button
                        onClick={() => setLogo(null)}
                        className="text-red-600 py-1 px-2 bg-gray-100 border-red-600 border-2 rounded-2xl"
                      >
                        Xóa ảnh
                      </button>
                    </div>
                  ) : (
                    <div className="flex flex-col items-center">
                      <img
                        src={brand.logo_url}
                        alt={brand.name}
                        className="object-cover mb-4"
                      />
                      <input
                        type="file"
                        accept="image/*"
                        className="w-full px-3 py-2 border rounded"
                        onChange={handleFileChange}
                      />
                    </div>
                  )}
                </div>

                <div className="flex justify-end space-x-2">
                  <button
                    onClick={handleCreateBrand}
                    type="submit"
                    className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-green-600 hover:font-semibold"
                  >
                    Lưu
                  </button>
                </div>
              </form>
            </div>
          </div>
        </Box>
      </Modal>
    </div>
  );
}

export default BrandEdit;
