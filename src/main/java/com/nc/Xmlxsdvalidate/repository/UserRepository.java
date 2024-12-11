package com.nc.Xmlxsdvalidate.repository;

import com.nc.Xmlxsdvalidate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
