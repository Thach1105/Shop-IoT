import { faPenToSquare } from "@fortawesome/free-regular-svg-icons";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useState } from "react";
import { useLoaderData } from "react-router-dom";
import BrandAdd from "./BrandAdd";
import BrandEdit from "./BrandEdit";

function BrandList() {
  const brandList = useLoaderData();
  const [addModal, setAddModal] = useState(false);
  const [editModal, setEditModal] = useState(false);
  const [brandSelected, setBrandSelected] = useState();

  return (
    <div className="container mx-auto p-4">
      {addModal && <BrandAdd open={addModal} setOpen={setAddModal} />}
      {editModal && (
        <BrandEdit
          brand={brandSelected}
          open={editModal}
          setOpen={setEditModal}
        />
      )}
      <h1 className="text-3xl font-bold mb-4">Quản lý Thương hiệu</h1>
      <div className="items-center mb-4">
        <button
          onClick={() => setAddModal(true)}
          className="bg-purple-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          <FontAwesomeIcon icon={faPlus} /> Thêm thương hiệu mới
        </button>
      </div>
      <div className="flex flex-col justify-around items-center">
        <div className=" grid gap-4 grid-cols-1 md:grid-cols-3">
          {brandList.map((brand, index) => (
            <div key={index} className="bg-white p-4 rounded shadow">
              <div className="relative">
                <img
                  src={brand.logo_url}
                  alt="Note Diaries"
                  className="w-auto h-48 object-cover rounded mx-auto"
                />
                <i className="fas fa-heart absolute top-2 right-2 text-red-500"></i>
              </div>
              <div className="mt-4">
                <h3 className="font-bold mt-2 text-center">{brand.name}</h3>
                <button
                  onClick={() => {
                    setBrandSelected(brand);
                    setEditModal(true);
                  }}
                  className="bg-purple-500 text-white px-4 py-2 rounded mt-2"
                >
                  <FontAwesomeIcon icon={faPenToSquare} /> Chỉnh sửa
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default BrandList;
