package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.SolutionRequest;
import com.thachnn.ShopIoT.dto.response.SolutionResponse;
import com.thachnn.ShopIoT.model.Solution;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ISolutionService {

    public SolutionResponse createNewSolution(SolutionRequest request);

    public List<SolutionResponse> getListSolution(Authentication authentication);

    public SolutionResponse getSolutionById(Integer id);

    public SolutionResponse getSolutionBySlug(String slug);

    public List<Solution> getListSolutionForCustomer();

    public boolean checkRoleAdmin(Authentication authentication);

    public SolutionResponse update(SolutionRequest request, Integer id);
}
