package com.rtsoju.dku_council_homepage.domain.post.controller;

import com.rtsoju.dku_council_homepage.common.ResponseResult;
import com.rtsoju.dku_council_homepage.common.SuccessResponseResult;
import com.rtsoju.dku_council_homepage.common.jwt.JwtProvider;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.page.PageAnnounceDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.page.PageRes;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.request.RequestAnnounceDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.response.IdResponseDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.response.ResponseAnnounceDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.subentity.Announce;
import com.rtsoju.dku_council_homepage.domain.post.service.AnnounceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announce")
public class AnnounceController {
    private final AnnounceService announceService;
    private final JwtProvider jwtProvider;

    /**
     * 목록조회 (keyWord로 조회하기)추가하기!
     * api/announce 호출시 uri param ?page, size, sort, q(query) custom 가능!
     */
    @GetMapping
    public PageRes<PageAnnounceDto> list(@RequestParam(value = "query", required = false)String query, @RequestParam(value = "category",required = false)String category,  Pageable pageable){
        Page<PageAnnounceDto> map = announceService.announcePage(query, category, pageable);
        return new PageRes<>(map.getContent(), map.getPageable(), map.getTotalElements());
    }

    /**
     * 단건 등록
     * @param data : title, text, url(null)
     * @param request : header
     * @return :announce PK
     */
    @PostMapping
    public ResponseEntity<ResponseResult> create(@Valid @ModelAttribute RequestAnnounceDto data, HttpServletRequest request) {
        String token = jwtProvider.getTokenInHttpServletRequest(request);
        Long userId = Long.parseLong(jwtProvider.getUserId(token));
        IdResponseDto announce = announceService.createAnnounce(userId, data);
        return ResponseEntity.created(URI.create("/api/announce/"+announce.getId()))
                .body(new SuccessResponseResult("등록 완료", announce));
    }

    /**
     * id값으로 단건조회 가능.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult> findOne(@PathVariable("id") Long id) {
        ResponseAnnounceDto response = announceService.findOne(id);
        return ResponseEntity.ok() //200
                .body(new SuccessResponseResult(response));
    }

    /**
     * 삭제
     * 메시지만? pk값 필요없고, only Message
     * 주의!! 공지사항은 ADMIN계정만 삭제할 수 있음!!!
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseResult> deleteOne(@PathVariable("id") Long id){
        announceService.deleteOne(id);
        return ResponseEntity.ok()
                .body(new SuccessResponseResult(id+"번 announce 삭제완료"));
    }



}
