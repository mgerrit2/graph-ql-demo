package com.graph.ql.graph_ql_demo.repository;

import com.graph.ql.graph_ql_demo.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    List<BookEntity> findByPersonIdIn(Collection<Long> personIds);
}
