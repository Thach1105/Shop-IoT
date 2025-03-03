import { useLoaderData } from "react-router-dom";
import SunEditor from "suneditor-react";
import "suneditor/dist/css/suneditor.min.css";

function ViewSolution() {
  const solution = useLoaderData();
  console.log(solution.content);

  return (
    <div>
      <SunEditor
        readOnly={true}
        hideToolbar={true}
        setContents={solution.content}
        height="auto"
      />
    </div>
  );
}

export default ViewSolution;
