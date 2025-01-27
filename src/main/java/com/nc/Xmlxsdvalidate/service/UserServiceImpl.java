package com.nc.Xmlxsdvalidate.service;


import com.nc.Xmlxsdvalidate.dto.UserDto;
import com.nc.Xmlxsdvalidate.dto.UserMapper;
import com.nc.Xmlxsdvalidate.entity.User;
import com.nc.Xmlxsdvalidate.exception.ResourceNotFoundException;
import com.nc.Xmlxsdvalidate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final XmlService xmlService;
    private final CompressionService compressionService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           XmlService xmlService, CompressionService compressionService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.xmlService = xmlService;
        this.compressionService = compressionService;
    }


    @Override
    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void processUserData(UserDto userDto, MultipartFile xsdFile, String xsdUrl) throws Exception {

        String xsdContent = null;

        if (xsdFile != null && !xsdFile.isEmpty()) {
            xsdContent = new String(xsdFile.getBytes(), StandardCharsets.UTF_8);
        } else if (xsdUrl != null && !xsdUrl.isEmpty()) {
            URL url = new URL(xsdUrl);
            try (var inputStream = url.openStream()) {
                xsdContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } else {
            throw new IllegalArgumentException("Either xsdFile or xsdUrl must be provided.");
        }

        // Créer XML
        String xmlContent = xmlService.createXml(userDto);

        // Valider XML
        if (xmlService.validateXml(xmlContent, xsdContent)) {
            // Écrire XML dans un fichier temporaire
            File tempFile = File.createTempFile("user", ".xml");
            Files.write(tempFile.toPath(), xmlContent.getBytes());

            // Compresser le fichier
            String compressedFilePath = tempFile.getParent() + File.separator + "user.tar.gz";
            compressionService.compressFile(tempFile.getAbsolutePath(), compressedFilePath);

            // Nettoyer le fichier temporaire
            tempFile.delete();

            System.out.println("Fichier XML créé, validé et compressé : " + compressedFilePath);
        } else {
            throw new IllegalArgumentException("Le XML généré n'est pas valide selon le schéma XSD fourni.");
        }
    }

}

