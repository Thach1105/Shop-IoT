import { useState, useRef } from "react";
import SunEditor from "suneditor-react";
import "suneditor/dist/css/suneditor.min.css"; // Import CSS của SunEditor
import {
  updateSolution,
  uploadImageSolution,
} from "../../../../services/apiSolution";
import { useLoaderData } from "react-router-dom";
import { Alert, Snackbar } from "@mui/material";

export default function UpdateSolution() {
  const loaderData = useLoaderData();
  const [title, setTitle] = useState(loaderData.name);
  const [slug, setSlug] = useState(loaderData.slug);
  const [status, setStatus] = useState(loaderData.enabled);
  const [editorContent, setEditorContent] = useState(loaderData.content || "");
  const [openSnackBar, setOpenSnackBar] = useState(false);
  const editorRef = useRef(null);

  const handleChange = (content) => {
    setEditorContent(content);
  };

  const handleCloseSnackBar = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setOpenSnackBar(false);
  };

  const handleSave = async () => {
    try {
      const postSolution = {
        name: title,
        slug,
        enabled: status,
        content: editorContent,
      };
      console.log(postSolution);
      await updateSolution(loaderData.id, postSolution);
      setOpenSnackBar(true);
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="w-full h-fit mx-auto py-4 px-6 bg-white rounded-lg shadow-md">
      <Snackbar
        open={openSnackBar}
        autoHideDuration={6000}
        onClose={handleCloseSnackBar}
      >
        <Alert
          onClose={handleCloseSnackBar}
          severity="success"
          variant="filled"
          sx={{ width: "100%" }}
        >
          Cập nhật bài viết thành công
        </Alert>
      </Snackbar>
      <h1 className="text-2xl font-bold mb-4">Chỉnh sửa giải pháp</h1>
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700">
          Tên giải pháp
        </label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="mt-1 block w-full border border-gray-300 rounded-md p-2"
          placeholder="Nhập tên bài viết"
        />
      </div>
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700">
          Đường dẫn
        </label>
        <input
          type="text"
          value={slug}
          onChange={(e) => setSlug(e.target.value)}
          className="mt-1 block w-full border border-gray-300 rounded-md p-2"
          placeholder="Nhập slug bài viết"
        />
      </div>
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700">
          Trạng Thái
        </label>
        <select
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          className="mt-1 block w-fit border border-gray-300 rounded-md p-2"
        >
          <option value={Boolean(true)}>Hiện</option>
          <option value={Boolean(false)}>Ẩn</option>
        </select>
      </div>
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700">
          Nội Dung
        </label>
        <SunEditor
          setOptions={{
            height: "auto",
            autoHeight: true,
            buttonList: [
              ["undo", "redo"],
              ["bold", "italic", "underline", "strike"],
              ["font", "fontSize", "formatBlock", "fontColor", "hiliteColor"],
              ["subscript", "superscript"],
              ["align", "horizontalRule", "list", "lineHeight"],
              ["image", "link"],
              ["table"],
            ],
          }}
          onImageUploadBefore={(files, info, uploadHandler) => {
            console.log("Selected file(s):", files);

            const handleUpload = async () => {
              try {
                // Gửi ảnh lên server
                const response = await uploadImageSolution(
                  files[0],
                  "your-solution-id"
                );
                const { data } = response;

                // URL ảnh sau khi tải lên server
                const uploadedImageUrl = data?.content;

                // Thay thế `src` của ảnh trong editor
                const editorContent = editorRef.current.getContents(); // Lấy nội dung hiện tại của editor
                const updatedContent = editorContent.replace(
                  info.src, // Đường dẫn tạm thời của ảnh
                  uploadedImageUrl + " " // Đường dẫn mới từ server
                );

                editorRef.current.setContents(updatedContent); // Cập nhật nội dung mới
                uploadHandler({ result: [{ url: uploadedImageUrl }] }); // Thông báo URL mới cho SunEditor
              } catch (error) {
                console.error("Error uploading image:", error);
                uploadHandler({ errorMessage: "Error uploading image" });
              }
            };

            handleUpload();
          }}
          value={editorContent}
          defaultValue={loaderData.content}
          onChange={handleChange}
          getSunEditorInstance={(editor) => {
            editorRef.current = editor; // Lưu tham chiếu đến SunEditor
          }}
        />
      </div>
      <div className="flex w-full justify-center">
        <button
          onClick={handleSave}
          className="mt-4 w-fit px-4 bg-blue-600 text-white font-bold py-2 rounded-md hover:bg-blue-800"
        >
          Chỉnh sửa bài viết
        </button>
      </div>
    </div>
  );
}
