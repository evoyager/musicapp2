package com.epam.resource.repository;

import java.util.List;

public interface CustomResourceRepository {
    List<Long> deleteAllByIdInReturnIds(List<Long> ids);
}
