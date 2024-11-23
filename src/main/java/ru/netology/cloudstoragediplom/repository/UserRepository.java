package ru.netology.cloudstoragediplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstoragediplom.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
