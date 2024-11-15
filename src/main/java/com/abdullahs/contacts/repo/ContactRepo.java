package com.abdullahs.contacts.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abdullahs.contacts.domain.Contact;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
  Optional<Contact> findById(String id);

}
