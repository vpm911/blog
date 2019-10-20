package com.blog.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

	Optional<User> findOneByResetKey(String resetKey);

	Optional<User> findOneByEmailIgnoreCase(String email);

	Optional<User> findOneByLogin(String login);

	@EntityGraph(attributePaths = "authorities")
	Optional<User> findOneWithAuthoritiesById(Long id);

	@EntityGraph(attributePaths = "authorities")
	Optional<User> findOneWithAuthoritiesByLogin(String login);

	@EntityGraph(attributePaths = "authorities")
	Optional<User> findOneWithAuthoritiesByEmail(String email);

	Page<User> findAllByLoginNot(Pageable pageable, String login);

	Optional<User> findOneByActivationKey(String key);
}
