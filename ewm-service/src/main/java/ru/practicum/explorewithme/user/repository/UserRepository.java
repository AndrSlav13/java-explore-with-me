package ru.practicum.explorewithme.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCriteria {
    List<User> findUsersByIdIn(List<Long> ids, Pageable pg);

    List<User> findUsersByIdIn(List<Long> ids);

    Optional<User> findUserById(Long id);
}