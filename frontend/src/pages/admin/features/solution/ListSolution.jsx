import { Link, useLoaderData } from "react-router-dom";

function ListSolution() {
  const solutions = useLoaderData();
  return (
    <div className="max-w-6xl mx-auto bg-white shadow-md mt-10 p-6">
      <h1 className="text-2xl font-bold mb-6">Danh Sách Giải Pháp</h1>
      <table className="w-full bg-white">
        <thead>
          <tr>
            <th className="py-2 px-4 border-b text-left">ID</th>
            <th className="py-2 px-4 border-b text-left">Tên Giải Pháp</th>
            <th className="py-2 px-4 border-b text-left">Trạng Thái</th>
            <th className="py-2 px-4 border-b text-left">Tùy chọn</th>
          </tr>
        </thead>
        <tbody>
          {solutions.length > 0 ? (
            solutions.map((solution) => (
              <tr key={solution.id}>
                <td className="py-2 px-4 border-b text-left">#{solution.id}</td>
                <td className="py-2 px-4 border-b text-left">
                  {solution.name}
                </td>
                <td
                  className={`py-2 px-4 border-b text-left font-semibold ${
                    solution.enabled ? "text-green-500" : "text-red-500"
                  }`}
                >
                  {solution.enabled ? "Hiển thị" : "Ẩn"}
                </td>
                <td className="py-2 px-4 border-b text-left">
                  <Link
                    to={`/admin/solution/${solution.id}`}
                    className="text-blue-500 hover:text-blue-700 mr-2"
                  >
                    Chỉnh sửa
                  </Link>
                </td>
              </tr>
            ))
          ) : (
            <div> Không tìm thấy giải pháp </div>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default ListSolution;
