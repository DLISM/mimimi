package com.example.mimimimetr.repositories;

import com.example.mimimimetr.models.Cat;
import com.example.mimimimetr.models.UserPairsVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPairsVoteRepository extends JpaRepository<UserPairsVote, Long> {

    @Query("SELECT uv.cat, COUNT(uv) AS voteCount " +
            "FROM UserPairsVote uv " +
            "GROUP BY uv.cat " +
            "ORDER BY voteCount DESC " +
            "LIMIT 10")
    List<Object[]> findTopVotedCats();
}
