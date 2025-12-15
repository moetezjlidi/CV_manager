package com.architecture.gestion_de_cvs.repository;

import com.architecture.gestion_de_cvs.security.XUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XUserRepository extends JpaRepository<XUser, String> {}
