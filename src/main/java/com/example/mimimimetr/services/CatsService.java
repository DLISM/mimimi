package com.example.mimimimetr.services;

import com.example.mimimimetr.dto.CreateCatDto;
import com.example.mimimimetr.exceptions.PairsNotFoundException;
import com.example.mimimimetr.exceptions.UpdateException;
import com.example.mimimimetr.mappers.CatMapper;
import com.example.mimimimetr.models.Cat;
import com.example.mimimimetr.models.CatPair;
import com.example.mimimimetr.models.UserPairsVote;
import com.example.mimimimetr.repositories.CatPairRepository;
import com.example.mimimimetr.repositories.CatRepository;
import com.example.mimimimetr.repositories.UserPairsVoteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatsService {

    private final CatPairRepository catPairRepository;
    private final UserPairsVoteRepository userPairsVoteRepository;
    private final CatRepository catRepository;

    @Value("${files.upload}")
    private String uploadDir;

    @Value("${files.link}")
    private String link;

    @PostConstruct
    private void initialize() {
        if (catPairRepository.count() == 0) {
            catPairRepository.generateAllUniquePairs();
        }
    }

    /**
     * Сохраняет результаты голосования пользователя.
     *
     * @param userPairsVote Объект, содержащий информацию о голосе пользователя (пара котов, выбранный кот и др.).
     * @param userSessionId Идентификатор сессии пользователя.
     * @throws UpdateException Если возникает ошибка при сохранении результатов голосования.
     */
    public void addVotesResult(UserPairsVote userPairsVote, String userSessionId) throws UpdateException {
        try {
            userPairsVote.setUserSession(userSessionId);
            userPairsVoteRepository.save(userPairsVote);
        } catch (Exception e) {
            throw new UpdateException("Ошибка при сохранении результатов голосования");
        }
    }

    /**
     * Возвращает следующую пару котиков для голосование
     *
     * @param userSession
     * @return
     */
    public CatPair getPair(String userSession) throws PairsNotFoundException {
        List<CatPair> pairsNotVotedByUser = catPairRepository.findPairsNotVotedByUser(userSession);

        if (pairsNotVotedByUser.isEmpty()) {
            throw new PairsNotFoundException("Пары для голосования не найдены");
        }
        return pairsNotVotedByUser.get(0);
    }

    private void generatePairs() {
        try {
            catPairRepository.generateAllUniquePairs();
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при генерации пар", e);
        }
    }


    /**
     * Возвращает список котов, упорядоченных по количеству голосов в порядке убывания.
     *
     * @return Список котов, начиная с кота с наибольшим количеством голосов.
     */
    public List<Cat> getTop() {
        List<Object[]> topVotedCats = userPairsVoteRepository.findTopVotedCats();
        return topVotedCats.stream()
                .map(arr -> (Cat) arr[0])
                .collect(toList());
    }

    /**
     * Добавляет нового кота и генерирует для нового кота пары
     * Выполняет загрузку изображения кота в директорию uploads
     *
     * @param newCat Объект CreateCatDto, содержащий данные для создания нового кота.
     * @throws UpdateException Если произошла ошибка при добавлении кота.
     */
    public void addCat(CreateCatDto newCat) {
        try {
            uploadFile(newCat);
            Cat cat = CatMapper.INSTANCE.dtoToEntity(newCat);
            Cat savedCat = catRepository.save(cat);
            catPairRepository.generateForOne(savedCat.getId());
        } catch (DataIntegrityViolationException e) {
            throw new UpdateException("Ошибка при добавления");
        } catch (Exception e) {
            log.error("Ошибка при добавления записи", e);
        }
    }

    /**
     * Выполняет загрузку файла изображения для нового кота.
     * Генерирует уникальное имя файла, создает директорию для загрузки, сохраняет файл в директории,
     * обновляет путь к файлу в объекте CreateCatDto.
     *
     * @param createCatDto Объект CreateCatDto, содержащий файл изображения для загрузки.
     * @throws IOException Если произошла ошибка при обработке файла изображения.
     */
    private void uploadFile(CreateCatDto createCatDto) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(createCatDto.getFile().getOriginalFilename()));
        fileName = UUID.randomUUID() + fileName;
        File uploadPath = new File(uploadDir);

        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(createCatDto.getFile().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        String savedFilePath = link + fileName;
        createCatDto.setPhoto(savedFilePath);
    }
}
