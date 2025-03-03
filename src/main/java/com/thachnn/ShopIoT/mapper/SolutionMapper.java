package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.request.SolutionRequest;
import com.thachnn.ShopIoT.dto.response.SolutionResponse;
import com.thachnn.ShopIoT.model.Solution;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolutionMapper {

    Solution toSolution (SolutionRequest request);

    SolutionResponse toSolutionResponse (Solution solution);
}
