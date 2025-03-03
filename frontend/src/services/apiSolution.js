import axiosInstance from "../configs/axiosInstance";
// import config from "../configs/config";

// const url = config.url_Backend;

export async function createSolution(solution) {
  console.log(solution);
  const response = await axiosInstance.post(`/solutions`, solution, {
    headers: {
      "Content-Type": "application/json",
    },
  });
  return response;
}

export async function uploadImageSolution(image) {
  const formData = new FormData();
  console.log(image);

  if (image) formData.append("file", image);

  const response = await axiosInstance.post(
    `/solutions/upload-image`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
  console.log(response);
  return response;
}

export async function getListSolution() {
  const response = await axiosInstance.get(`/solutions`);
  return response;
}

export async function getSolutionById(id) {
  const response = await axiosInstance.get(`/solutions/${id}`);
  return response;
}

export async function updateSolution(id, postSolution) {
  const response = await axiosInstance.put(`/solutions/${id}`, postSolution);
  return response;
}

export async function getListSolutionPublic() {
  const response = await axiosInstance.get(`/solutions`);
  return response;
}

export async function getSolutionBySlug(slug) {
  const response = await axiosInstance.get(`/solutions/slug/${slug}`);
  return response;
}
