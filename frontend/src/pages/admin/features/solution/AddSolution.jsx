import { useState, useRef } from "react";
import SunEditor from "suneditor-react";
import "suneditor/dist/css/suneditor.min.css"; // Import CSS của SunEditor
import {
  createSolution,
  uploadImageSolution,
} from "../../../../services/apiSolution";

export default function AddSolution() {
  const [title, setTitle] = useState("");
  const [slug, setSlug] = useState("");
  const [status, setStatus] = useState("visible"); // "visible" hoặc "hidden"
  const [editorContent, setEditorContent] = useState("");
  const editorRef = useRef(null);

  //const [solutionId, setSolutionId] = useState();

  const handleChange = (content) => {
    setEditorContent(content);
  };

  // Hàm tải ảnh lên server và thay thế đường dẫn ảnh trong nội dung

  // Hàm xử lý nội dung editor và thay thế các ảnh
  const handleSave = async () => {
    try {
      const newOrder = {
        name: title,
        slug,
        enable: status === "visible" ? true : false,
        content: editorContent,
      };
      console.log(newOrder);
      const response = await createSolution(newOrder);
      const { data } = response;
      const newSolution = data?.content;
      //   setSolutionId(newSolution.id);

      console.log(response);
    } catch (error) {
      console.log(error);
    }
    // let content = editorContent;
    // // Tìm tất cả các thẻ <img> trong nội dung và thay thế
    // const imgTags = content.match(/<img[^>]+src="([^">]+)"/g); // Tìm tất cả ảnh
    // console.log(imgTags);
    // if (imgTags) {
    //   for (const tag of imgTags) {
    //     const imgSrc = tag.match(/src="([^"]+)"/)[1]; // Lấy đường dẫn ảnh gốc
    //     console.log(imgSrc);
    //     //Gửi ảnh lên backend và lấy URL mới từ server
    //     const newUrl = await handleImageUpload(imgSrc, id);
    //     if (newUrl) {
    //       // Thay thế src của ảnh trong nội dung với URL từ server
    //       content = content.replace(imgSrc, newUrl);
    //     }
    //   }
    // }
    // // Sau khi thay thế, bạn có thể lưu nội dung mới vào database
    // console.log("Updated content:", content);
    // setEditorContent(content);
  };

  return (
    <div className="w-full h-fit mx-auto py-4 px-6 bg-white rounded-lg shadow-md">
      <h1 className="text-2xl font-bold mb-4">Thêm giải pháp mới</h1>
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
          placeholder="Nhập đường dẫn bài viết"
        />
      </div>
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700">
          Trạng Thái
        </label>
        <select
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          className="mt-1 block border border-gray-300 rounded-md p-2"
        >
          <option value="visible">Hiện thị</option>
          <option value="hidden">Ẩn</option>
        </select>
      </div>
      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-2">
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
          defaultValue="Hãy viết giải pháp mới..."
          onChange={handleChange}
          getSunEditorInstance={(editor) => {
            editorRef.current = editor; // Lưu tham chiếu đến SunEditor
          }}
        />
      </div>
      <button
        onClick={handleSave}
        className="mt-4 w-full bg-blue-600 text-white font-bold py-2 rounded-md hover:bg-blue-700"
      >
        Lưu Bài Viết
      </button>
    </div>
  );
}
