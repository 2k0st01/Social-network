package com.example.search_history.search;

import com.example.search_history.search.Search;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository
extends JpaRepository<Search, Long> {
    List<Search> findTop10ByOwnIdOrderByTimeDesc(String ownID);
}
