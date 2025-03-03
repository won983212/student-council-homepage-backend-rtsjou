package com.rtsoju.dku_council_homepage.domain.post.service;

import com.rtsoju.dku_council_homepage.common.nhn.service.FileUploadService;
import com.rtsoju.dku_council_homepage.common.nhn.service.NHNAuthService;
import com.rtsoju.dku_council_homepage.common.nhn.service.ObjectStorageService;
import com.rtsoju.dku_council_homepage.domain.post.entity.PostFile;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.page.PageAnnounceDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.request.RequestAnnounceDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.response.IdResponseDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.dto.response.ResponseAnnounceDto;
import com.rtsoju.dku_council_homepage.domain.post.entity.subentity.Announce;
import com.rtsoju.dku_council_homepage.domain.post.repository.AnnounceRepository;
import com.rtsoju.dku_council_homepage.domain.user.model.entity.User;
import com.rtsoju.dku_council_homepage.domain.user.repository.UserRepository;
import com.rtsoju.dku_council_homepage.exception.BadRequestException;
import com.rtsoju.dku_council_homepage.exception.FindPostWithIdNotFoundException;
import com.rtsoju.dku_council_homepage.exception.FindUserWithIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnnounceService {
    private final AnnounceRepository announceRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    public Page<PageAnnounceDto> announcePage(String query, String category, Pageable pageable){
        Page<Announce> page;
        if(query == null && category == null) {
            page = announceRepository.findAll(pageable);
        }else if(query != null && category == null){
            page = announceRepository.findAllByTitleContainsOrTextContains(query,query,pageable);
        }
        else if(query == null && category != null){
            page = announceRepository.findAllByCategory(category, pageable);
        }else{
            page = announceRepository.findAllByCategoryAndTitleContainsOrTextContains(category, query, query, pageable);
        }
        return page.map(PageAnnounceDto::new);
    }

    @Transactional
    public IdResponseDto createAnnounce(Long userId, RequestAnnounceDto data) {
        User user = userRepository.findById(userId).orElseThrow(FindUserWithIdNotFoundException::new);
        ArrayList<PostFile> postFiles = fileUploadService.uploadFiles(data.getFiles(), "announce");
        Announce announce = new Announce(user, data, postFiles);
        Announce save = announceRepository.save(announce);
        return new IdResponseDto(save.getId());
    }

    @Transactional
    public ResponseAnnounceDto findOne(Long id) {
        Announce announce = announceRepository.findById(id).orElseThrow(FindPostWithIdNotFoundException::new);
        announce.plusHits();
        return new ResponseAnnounceDto(announce);
    }

    @Transactional
    public void deleteOne(Long id) {
        Announce announce = announceRepository.findById(id).orElseThrow(FindPostWithIdNotFoundException::new);
        List<PostFile> fileList = announce.getFileList();
        fileUploadService.deletePostFiles(fileList);
        announceRepository.delete(announce);
    }


}
