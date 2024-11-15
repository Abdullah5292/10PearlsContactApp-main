package com.abdullahs.contacts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.abdullahs.contacts.constant.Constant;
import com.abdullahs.contacts.domain.Contact;
import com.abdullahs.contacts.repo.ContactRepo;

import jakarta.transaction.Transactional;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor

public class ContactService {
  private final ContactRepo contactRepo;

  public Page<Contact> getAllContact(int page, int size) {
    return contactRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
  }

  public Contact geContact(String id) {
    return contactRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
  }

  public Contact createContact(Contact contact) {
    return contactRepo.save(contact);
  }

  public void deleteContact(Contact contact) {
    // Assignment
  }

  public String uploadPhoto(String id, MultipartFile file) {
    log.info("Saving info for User ID");
    Contact contact = geContact(id);
    String photoUrl = uploadPhoto(id, file);
    contact.setPhotoUrl(photoUrl);
    contactRepo.save(contact);
    return photoUrl;
  }

  private final Function<String, String> fileExtension = filename -> Optional.of(filename)
      .filter(name -> name.contains("."))
      .map(name -> name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

  private final BiFunction<String, MultipartFile, String> uploadPhoto = (id, file) -> {
    String filename = id + fileExtension.apply(file.getOriginalFilename());
    try {
      Path fileStorageLocation = Paths.get(Constant.PHOTO_DIRECTORY).toAbsolutePath().normalize();
      if (!Files.exists(fileStorageLocation)) {
        Files.createDirectories(fileStorageLocation);
      }
      Files.copy(file.getInputStream(), fileStorageLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
      return ServletUriComponentsBuilder.fromCurrentContextPath().path("/contacts/image/" + filename).toUriString();
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload photo");
    }
  };

}
