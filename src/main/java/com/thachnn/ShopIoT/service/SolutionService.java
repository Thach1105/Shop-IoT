package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.SolutionRequest;
import com.thachnn.ShopIoT.dto.response.SolutionResponse;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.SolutionMapper;
import com.thachnn.ShopIoT.model.Solution;
import com.thachnn.ShopIoT.repository.SolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolutionService {


    @Autowired
    SolutionRepository solutionRepository;

    @Autowired
    SolutionMapper solutionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public SolutionResponse createNewSolution(SolutionRequest request){
        Solution solution = solutionRepository.findByName(request.getName());
        if(solution == null) {
            if(solutionRepository.existsBySlug(request.getSlug()))
                throw new AppException(ErrorApp.SOLUTION_SLUG_EXISTED);

            Solution newSolution = solutionMapper.toSolution(request);
            Solution saveSolution = solutionRepository.save(newSolution);
            return solutionMapper.toSolutionResponse(saveSolution);

        } else {
           throw new AppException(ErrorApp.SOLUTION_NAME_EXISTED);
        }
    }

    public List<SolutionResponse> getListSolution(Authentication authentication){
        if(checkRoleAdmin(authentication)){
            List<Solution> solutionList = solutionRepository.findAllForAdmin();
            return solutionList.stream().map(solution ->
                 SolutionResponse.builder()
                        .id(solution.getId())
                        .name(solution.getName())
                        .slug(solution.getSlug())
                        .enabled(solution.isEnabled())
                         .build()
            ).toList();

        } else {
            List<Solution> solutionList = solutionRepository.findAllForCustomer();
            return solutionList.stream().map(solution ->
                    SolutionResponse.builder()
                            .id(solution.getId())
                            .name(solution.getName())
                            .slug(solution.getSlug())
                            .enabled(solution.isEnabled())
                            .build()
            ).toList();
        }
    }

    public SolutionResponse getSolutionById(Integer id){
        Solution solution = solutionRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorApp.SOLUTION_NOT_FOUND)
        );

        return solutionMapper.toSolutionResponse(solution);
    }

    public SolutionResponse getSolutionBySlug(String slug){
        Solution solution = solutionRepository.findBySlug(slug).orElseThrow(
                () -> new AppException(ErrorApp.SOLUTION_NOT_FOUND)
        );
        return solutionMapper.toSolutionResponse(solution);
    }

    public List<Solution> getListSolutionForCustomer(){
        return solutionRepository.findAllForCustomer();
    }

    public boolean checkRoleAdmin(Authentication authentication){
        if(authentication == null || !authentication.isAuthenticated()) return false;
        return authentication.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
    }

    public SolutionResponse update(SolutionRequest request, Integer id){
        Solution preSolution =  solutionRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorApp.SOLUTION_NOT_FOUND)
        );

        if(!preSolution.getName().equals(request.getName())
                && solutionRepository.existsByName(request.getName())
        ){
            throw new AppException(ErrorApp.SOLUTION_NAME_EXISTED);
        }

        if(!preSolution.getSlug().equals(request.getSlug())
                && solutionRepository.existsBySlug(request.getSlug())
        ){
            throw new AppException(ErrorApp.SOLUTION_SLUG_EXISTED);
        }

        Solution postSolution = solutionMapper.toSolution(request);
        postSolution.setId(id);
        solutionRepository.save(postSolution);

        return solutionMapper.toSolutionResponse(postSolution);
    }
}
