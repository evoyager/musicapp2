package com.epam.song.repository;

import java.util.List;

public interface CustomSongRepository {
    void checkSongIdExists(Long id);

    List<Long> deleteAllByIdInReturnIds(List<Long> ids);
}
