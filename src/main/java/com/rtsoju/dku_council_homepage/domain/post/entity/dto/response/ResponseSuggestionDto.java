package com.rtsoju.dku_council_homepage.domain.post.entity.dto.response;

import com.rtsoju.dku_council_homepage.domain.post.entity.Comment;
import com.rtsoju.dku_council_homepage.domain.post.entity.subentity.Suggestion;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ResponseSuggestionDto {
    private Long id;
    private String title;
    private String text;
    private List<FileUrlWithNameDto> fileList;
    private String createDate;
    private int postHits;
    private String category;
    private List<CommentResponseDto> commentList;
    private String answer;

    private boolean isMine;

    public ResponseSuggestionDto(Long userId, Suggestion suggestion) {
        this.id = suggestion.getId();
        this.title = suggestion.getTitle();
        this.text = suggestion.getText();
        this.fileList = suggestion.getFiles();
        this.createDate = suggestion.convertDate(suggestion.getCreateDate());
        this.postHits = suggestion.getHitCount();
        this.category = suggestion.getCategory();
        this.commentList = suggestion.getComments()
                .stream()
                .map((comment) -> new CommentResponseDto(userId, comment))
                .collect(Collectors.toList());
        this.isMine = suggestion.getUser().getId().equals(userId);
    }
}
