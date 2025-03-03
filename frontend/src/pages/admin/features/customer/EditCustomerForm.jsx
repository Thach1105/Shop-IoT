import { faX } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useState } from "react";
import { updateCustomer } from "../../../../services/apiCustomer";

/* eslint-disable react/prop-types */
function EditCustomerForm({ customer, onClose, setUsers }) {
  const [fullName, setFullName] = useState(customer?.fullName);
  const [email, setEmail] = useState(customer?.email);
  const [phone, setPhone] = useState(customer?.phoneNumber);
  const [dob, setDob] = useState(customer?.dob || undefined);

  async function handleUpdateUser(e) {
    e.preventDefault();
    const updateUser = {
      fullName,
      email,
      phoneNumber: phone,
      dob,
    };

    try {
      const response = await updateCustomer(updateUser, customer.id);
      console.log(response);

      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.id === customer.id ? { ...user, ...updateUser } : user
        )
      );

      onClose();
    } catch (error) {
      console.log(error);
    }
  }

  return (
    <div className="justify-center items-center flex overflow-x-hidden overflow-y-auto fixed inset-0 z-50 outline-none bg-gray-400/45">
      <div className="bg-white p-6 rounded shadow-md w-1/2 relative ">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold">CHỈNH SỬA NGƯỜI DÙNG</h2>
          <button className="text-gray-500 p-2" onClick={onClose}>
            <FontAwesomeIcon icon={faX} />
          </button>
        </div>
        <form onSubmit={handleUpdateUser}>
          <div className="mb-4">
            <label className="block text-gray-700">Họ và tên</label>
            <input
              type="text"
              className="w-full p-2 border rounded"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
            />
          </div>
          {/* <div className="mb-4">
        <label className="block text-gray-700">Customers Photo</label>
        <input type="file" className="w-full p-2 border rounded" />
      </div> */}
          <div className="mb-4 flex">
            <div className="w-1/2 mr-2">
              <label className="block text-gray-700">ID</label>
              <input
                type="text"
                className="w-full p-2 border rounded"
                value={`#${customer.id}`}
                disabled
              />
            </div>
            <div className="w-1/2 ml-2">
              <label className="block text-gray-700">Ngày sinh</label>
              <input
                type="date"
                className="w-full p-2 border rounded"
                value={dob}
                onChange={(e) => setDob(e.target.value)}
              />
            </div>
          </div>
          <div className="mb-4 flex">
            <div className="w-1/2 mr-2">
              <label className="block text-gray-700">Email</label>
              <input
                type="email"
                className="w-full p-2 border rounded"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="w-1/2 ml-2">
              <label className="block text-gray-700">Số điện thoại</label>
              <input
                type="text"
                className="w-full p-2 border rounded"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
            </div>
          </div>
          {/* <div className="mb-4">
        <label className="block text-gray-700">Total Order</label>
        <input type="text" className="w-full p-2 border rounded" value="02" />
      </div> */}
          <div className="flex justify-end">
            <button
              type="submit"
              className="bg-purple-600 text-white px-4 py-2 rounded"
            >
              Lưu
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EditCustomerForm;
