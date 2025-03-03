/* eslint-disable react/prop-types */
// import "react-quill/dist/quill.snow.css";
import loadable from "@loadable/component";
const ReactQuill = loadable(() => import("react-quill"));

function TextEditor({ text, setText }) {
  const modules = {
    toolbar: [
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      ["bold", "italic", "underline", "strike", "blockquote"],
      [{ size: [] }],
      [{ font: [] }],
      [{ align: ["right", "center", "justify"] }],
      [{ list: "ordered" }, { list: "bullet" }],
      ["link", "image"],
      ["clean"],
      [
        {
          color: [
            "red",
            "#785412",
            "green",
            "yellow",
            "black",
            "blue",
            "pink",
            "gray",
          ],
        },
      ],
      [
        {
          background: [
            "red",
            "#785412",
            "green",
            "yellow",
            "black",
            "blue",
            "pink",
            "gray",
          ],
        },
      ],
    ],
    clipboard: {
      matchVisual: false,
    },
  };

  const formats = [
    "header",
    "bold",
    "italic",
    "underline",
    "strike",
    "blockquote",
    "list",
    "bullet",
    "link",
    "color",
    "background",
    "align",
    "size",
    "font",
  ];

  // eslint-disable-next-line no-unused-vars
  const handleProcedureContentChange = (content, delta, source, editor) => {
    setText(content);
  };

  return (
    <>
      <ReactQuill
        theme="snow"
        modules={modules}
        formats={formats}
        value={text}
        onChange={handleProcedureContentChange}
      />
    </>
  );
}

export default TextEditor;
