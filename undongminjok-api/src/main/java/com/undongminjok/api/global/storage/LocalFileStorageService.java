package com.undongminjok.api.global.storage;

import com.undongminjok.api.global.exception.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class LocalFileStorageService implements FileStorage {

  private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp");

  private final Path rootDir;

  public LocalFileStorageService(@Value("${file.root-dir}") String rootDir) {
    this.rootDir = Paths.get(rootDir)
                          .normalize()
                          .toAbsolutePath();

    try {
      Files.createDirectories(this.rootDir);
    } catch (IOException e) {
      throw new BusinessException(FileErrorCode.FILE_DIR_CREATE_FAILED);
    }
  }

  @Override
  public String store(MultipartFile file, ImageCategory category) {
    // 파일 유효성 검증
    if (file == null || file.isEmpty()) {
      throw new BusinessException(FileErrorCode.FILE_EMPTY);
    }

    // 확장자 추출 및 검증
    final String originalName = file.getOriginalFilename();
    if (originalName == null || originalName.isBlank()) {
      throw new BusinessException(FileErrorCode.FILE_NAME_NOT_PRESENT);
    }
    final String ext = Optional.of(originalName)
                               .filter(name -> name.contains("."))
                               .map(name -> name.substring(
                                   name.lastIndexOf('.') + 1)) // 마지막 점 이후 부분 추출
                               .map(String::toLowerCase)
                               .orElse("");
    if (!ALLOWED_EXT.contains(ext)) {
      throw new BusinessException(FileErrorCode.FILE_EXTENSION_NOT_ALLOWED);
    }

    // 파일명 랜덤화 (UUID)
    final String fileName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

    Path categoryDir = rootDir.resolve(category.getDir())
                                .normalize()
                                .toAbsolutePath();

    try {
      Files.createDirectories(categoryDir);
    } catch (IOException e) {
      throw new BusinessException(FileErrorCode.FILE_DIR_CREATE_FAILED);
    }

    // 안전한 파일 경로 생성
    final Path target = safeResolve(categoryDir, fileName);

    // 파일 저장
    try (InputStream in = file.getInputStream()) {
      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      throw new BusinessException(FileErrorCode.FILE_SAVE_IO_ERROR);
    }

    return category.getDir() + "/" + fileName;
  }

  @Override
  public void delete(String storedPath) {
    Path target = rootDir.resolve(storedPath)
                           .normalize()
                           .toAbsolutePath();

    if (!target.startsWith(rootDir)) {
      throw new BusinessException(FileErrorCode.FILE_PATH_TRAVERSAL_DETECTED);
    }

    try {
      Files.deleteIfExists(target);
    } catch (IOException ex) {
      throw new BusinessException(FileErrorCode.FILE_DELETE_IO_ERROR);
    }
  }

  @Override
  public void deleteQuietly(String storedPath) {
    delete(storedPath);
  }

  private Path safeResolve(Path base, String fileName) {
    Path p = base.resolve(fileName)
                 .normalize()
                 .toAbsolutePath();
    if (!p.startsWith(base)) {
      throw new BusinessException(FileErrorCode.FILE_PATH_TRAVERSAL_DETECTED);
    }
    return p;
  }
}
