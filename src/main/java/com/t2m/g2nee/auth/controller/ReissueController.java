package com.t2m.g2nee.auth.controller;

import com.t2m.g2nee.auth.repository.RefreshTokenRepository;
import com.t2m.g2nee.auth.service.tokenService.ReissueService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@ResponseBody
public class ReissueController {


    private final ReissueService reissueService;

    public ReissueController(ReissueService reissueService, RefreshTokenRepository refreshTokenRepository) {

        this.reissueService = reissueService;
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        return reissueService.reissue(request, response);

    }
}
