package com.example.mimimimetr.repositories;

import com.example.mimimimetr.models.CatPair;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatPairRepository extends JpaRepository<CatPair, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO pairs (cat_one_id, cat_two_id) " +
            "SELECT c1.id, c2.id FROM cat c1, cat c2 WHERE c1.id < c2.id", nativeQuery = true)
    void generateAllUniquePairs();

    @Query("SELECT cp FROM CatPair cp " +
            "WHERE NOT EXISTS (" +
            "   SELECT 1 FROM UserPairsVote upv " +
            "   WHERE upv.catPair = cp AND upv.userSession = :userSession" +
            ")")
    List<CatPair> findPairsNotVotedByUser(@Param("userSession") String userSession);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO pairs (cat_one_id, cat_two_id) " +
            "SELECT c1.id, :newCatId FROM cat c1 WHERE c1.id < :newCatId", nativeQuery = true)
    void generateForOne(@Param("newCatId") Long newCatId);

    long count();
}