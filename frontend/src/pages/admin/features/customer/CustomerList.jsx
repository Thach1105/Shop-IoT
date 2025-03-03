import { faPenToSquare } from "@fortawesome/free-regular-svg-icons";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { getUsers } from "../../../../services/apiCustomer";
import TablePagination from "@mui/material/TablePagination";
import EditCustomerForm from "./EditCustomerForm";
import { Link } from "react-router-dom";

function CustomerList() {
  const [users, setUsers] = useState([]);
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [pageDetail, setPageDetail] = useState({});
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    async function fetchUser() {
      try {
        const { data } = await getUsers(pageNumber + 1, pageSize);
        setUsers(data.content);
        setPageDetail(data.pageDetails);
      } catch (error) {
        console.log(error.response);
      }
    }
    fetchUser();
  }, [pageNumber, pageSize]);

  const handleChangePage = (event, newPage) => {
    setPageNumber(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setPageSize(parseInt(event.target.value, 10));
    setPageNumber(0);
  };

  function onCloseEditForm() {
    setIsEditing(false);
    setSelectedCustomer(null);
  }

  return (
    <div className="p-8">
      {isEditing && (
        <EditCustomerForm
          customer={selectedCustomer}
          onClose={onCloseEditForm}
          setUsers={setUsers}
        />
      )}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Quản lý người dùng</h1>
        <button className="bg-purple-500 text-white px-4 py-2 rounded flex items-center">
          <FontAwesomeIcon className="pr-2" icon={faPlus} /> Thêm người dùng
        </button>
      </div>
      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                ID
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                TÊN
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                NGÀY SINH
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                EMAIL
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                ĐIỆN THOẠI
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                TÙY CHỈNH
              </th>
            </tr>
          </thead>
          <tbody>
            {users.map((customer, index) => (
              <tr key={index}>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    #{customer.id}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <div className="flex items-center">
                    <div className="flex-shrink-0 w-10 h-10">
                      <img
                        className="w-full h-full rounded-full"
                        src="https://placehold.co/40x40"
                        alt={`Avatar of ${customer.name}`}
                      />
                    </div>
                    <div className="ml-3">
                      <Link to={`/admin/customer-details/${customer.id}`}>
                        <p className="text-gray-900 whitespace-no-wrap hover:text-yellow-500">
                          {customer.fullName}
                        </p>
                      </Link>
                    </div>
                  </div>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {customer.dob}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {customer.email}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {customer.phoneNumber}
                  </p>
                </td>

                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <button
                    className="text-green-600 hover:text-green-900 mr-2"
                    onClick={() => {
                      setIsEditing(true);
                      setSelectedCustomer(customer);
                    }}
                  >
                    <FontAwesomeIcon icon={faPenToSquare} size="lg" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <TablePagination
          component="div"
          count={pageDetail.totalElements}
          page={pageNumber}
          onPageChange={handleChangePage}
          rowsPerPage={pageSize}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </div>
    </div>
  );
}

export default CustomerList;
