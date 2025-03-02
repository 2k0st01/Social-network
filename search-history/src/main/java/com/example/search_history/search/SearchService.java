package com.example.search_history.search;

import com.example.search_history.DTO.RequestForSearch;
import com.example.search_history.search.Search;
import com.example.search_history.search.SearchRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    @Transactional
    @CacheEvict(value = "search", key = "#userId")
    public void saveUserSearchData(String userId, RequestForSearch request) {
        Search search = new Search();
        search.setUserId(request.getUserId());
        search.setUsername(request.getUsername());
        search.setOwnId(userId);
        search.setTime(LocalDateTime.now());
        searchRepository.save(search);
    }

    @Transactional(readOnly=true)
    @Cacheable(value = "search", key = "#userId")
    public List<RequestForSearch> getUserSearchData(String userId) {
        return searchRepository.findTop10ByOwnIdOrderByTimeDesc(userId).stream().map(a -> {
            RequestForSearch rq = new RequestForSearch();
            rq.setUserId(a.getUserId());
            rq.setUsername(a.getUsername());
            return rq;
        }).collect(Collectors.toList());
    }
}
